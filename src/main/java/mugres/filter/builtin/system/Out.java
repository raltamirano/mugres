package mugres.filter.builtin.system;

import mugres.common.Context;
import mugres.common.Pitch;
import mugres.common.Signal;
import mugres.common.Signals;
import mugres.common.io.Output;
import mugres.filter.Filter;

import java.util.PriorityQueue;
import java.util.UUID;

import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparingLong;

public final class Out extends Filter {
    private static final String NAME = "Out";
    private final Context context;
    private final Output output;
    private final Thread worker;
    private final PriorityQueue<Signal> queue;

    public Out(final Context context, final Output output) {
        super(emptyMap());

        this.context = context;
        this.output = output;

        queue = new PriorityQueue<>(comparingLong(Signal::time));
        worker = createWorkerThread();
        worker.setDaemon(true);
        worker.start();

        addSignalEventListener(createSignalEventListener());
    }


    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals) {
        final long now = System.currentTimeMillis();

        for(Signal e : signals.signals())
            queue.add(e);

        return Signals.create();
    }

    private Thread createWorkerThread() {
        return new Thread(() -> {
            while(true)
                try {
                    if(!queue.isEmpty()) {
                        long now = System.currentTimeMillis();
                        boolean run = true;
                        while(run) {
                            if (queue.peek().time() <= now) {
                                output.send(queue.remove());
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

    private SignalEventListener createSignalEventListener() {
        return new SignalEventListener() {
            @Override
            public void activated(final UUID activated, final int channel, final Pitch pitch) {
                // Do nothing
            }

            @Override
            public void deactivated(final UUID deactivated, final int channel, final Pitch pitch) {
                queue.removeIf(s -> { if (s!= null) s.id().equals(deactivated); return true; });
            }
        };
    }
}
