package mugres.core.filters;

import mugres.core.common.Context;
import mugres.core.model.Events;

import javax.sound.midi.Receiver;
import java.util.PriorityQueue;

import static java.util.Comparator.comparingLong;

public final class Output extends AbstractFilter {
    private final PriorityQueue<Events.Event> queue;
    private final Thread worker;
    private final Receiver outputPort;

    public Output(final Receiver outputPort) {
        this.outputPort = outputPort;

        queue = new PriorityQueue<>(comparingLong(Events.Event::getTimestamp));
        worker = createWorkerThread();
        worker.start();
    }

    @Override
    protected boolean canHandle(final Context context, final Events events) {
        return true;
    }

    @Override
    protected Events handle(final Context context, final Events events) {
        final long now = System.currentTimeMillis();

        for(Events.Event e : events)
            if (e.getTimestamp() <= now)
                outputPort.send(e.getMessage(), -1);
            else
                queue.add(e);

        return Events.empty();
    }

    private Thread createWorkerThread() {
        return new Thread(() -> {
            while(true)
                try {
                    if(!queue.isEmpty()) {
                        long now = System.currentTimeMillis();
                        boolean run = true;
                        while(run)
                            if (queue.peek().getTimestamp() <= now) {
                                outputPort.send(queue.remove().getMessage(), -1);
                                run = !queue.isEmpty();
                            }
                            else
                                run = false;
                    }
                } catch (final Throwable ignore) {
                    // Do nothing!
                } finally {
                    try { Thread.sleep(50); } catch (final Throwable ignore) {}
                }
        });
    }
}
