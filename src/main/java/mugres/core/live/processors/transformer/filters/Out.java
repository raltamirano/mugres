package mugres.core.live.processors.transformer.filters;

import mugres.core.common.Context;
import mugres.core.common.io.Output;
import mugres.core.common.Signal;
import mugres.core.common.Signals;

import java.util.Map;
import java.util.PriorityQueue;

import static java.util.Comparator.comparingLong;

public final class Out extends Filter {
    private final Context context;
    private final Output output;
    private final Thread worker;
    private final PriorityQueue<Signal> queue;

    public Out(final Context context, final Output output) {
        super("Out");

        this.context = context;
        this.output = output;

        queue = new PriorityQueue<>(comparingLong(Signal::getTime));
        worker = createWorkerThread();
        worker.start();
    }

    @Override
    protected boolean canHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals handle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        final long now = System.currentTimeMillis();

        for(Signal e : signals.signals())
            if (e.getTime() <= now)
                output.send(e);
            else
                queue.add(e);

        return Signals.empty();
    }

    private Thread createWorkerThread() {
        return new Thread(() -> {
            while(true)
                try {
                    if(!queue.isEmpty()) {
                        long now = System.currentTimeMillis();
                        boolean run = true;
                        while(run)
                            if (queue.peek().getTime() <= now) {
                                output.send(queue.remove());
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
