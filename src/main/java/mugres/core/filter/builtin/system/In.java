package mugres.core.filter.builtin.system;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.Signals;
import mugres.core.common.io.Input;
import mugres.core.filter.Filter;

import java.util.Map;
import java.util.PriorityQueue;

import static java.util.Comparator.comparingLong;

public final class In extends Filter {
    private final Context context;
    private final Input input;
    private final PriorityQueue<Signal> queue;
    private final Thread worker;

    public In(final Context context, final Input input) {
        super("In");

        this.context = context;
        this.input = input;

        queue = new PriorityQueue<>(comparingLong(Signal::getTime));

        this.worker = createWorkerThread();
        this.worker.setDaemon(true);
        this.worker.start();
    }

    public Input getInput() {
        return input;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        updateSignalsState(signals);
        return signals;
    }

    private void updateSignalsState(final Signals signals) {
        final long now = System.currentTimeMillis();
        for(final Signal in : signals.signals())
            if (in.getTime() <= now)
                handleSignal(in);
            else
                queue.add(in);
    }

    private Thread createWorkerThread() {
        return new Thread(() -> {
            while(true)
                try {
                    if(!queue.isEmpty()) {
                        long now = System.currentTimeMillis();
                        boolean run = true;
                        while(run) {
                            if (queue.peek().getTime() <= now) {
                                handleSignal(queue.remove());
                                run = !queue.isEmpty();
                            } else {
                                run = false;
                            }
                        }
                    }
                } catch (final Throwable ignore) {
                    // Do nothing!
                } finally {
                    try { Thread.sleep(1); } catch (final Throwable ignore) {}
                }
        });
    }

    private void handleSignal(final Signal signal) {
        if (signal.isActive())
            Filter.activateSignal(signal.getChannel(), signal.getPlayed().getPitch(), signal.getEventId());
        else
            Filter.deactivateSignal(signal.getChannel(), signal.getPlayed().getPitch());
    }
}
