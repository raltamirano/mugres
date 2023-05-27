package mugres.live.signaler;

import mugres.common.*;
import mugres.common.frequency.Frequency;
import mugres.common.frequency.builtin.Fixed;
import mugres.common.io.Input;
import mugres.live.signaler.config.Configuration;

import java.util.PriorityQueue;

import static java.util.Comparator.comparingLong;

public class Signaler {
    private final Configuration config;
    private Input target;
    private Frequency frequency;
    private long duration;
    private final Thread worker;
    private final PriorityQueue<Entry> queue;

    private Signaler(final Configuration config) {
        if (config == null)
            throw new IllegalArgumentException("config");

        this.config = config;

        queue = new PriorityQueue<>(comparingLong(Entry::time));
        worker = createWorkerThread();
        worker.setDaemon(true);
        worker.start();
    }

    public static Signaler forConfig(final Configuration config) {
        return new Signaler(config);
    }

    public void start(final Context context, final Input target) {
        if (frequency != null && frequency.isRunning())
            throw new IllegalStateException("Already running!");

        if (target == null)
            throw new IllegalArgumentException("target");

        this.target = target;

        frequency = createFrequency(context);
        frequency.addListener(createFrequencyListener());

        duration = getDuration(context);

        this.frequency.start();
    }

    private Fixed createFrequency(final Context context) {
        try {
            final long millis = Long.parseLong(config.frequency().value());
            return Frequency.fixed(millis);
        } catch(final NumberFormatException e) {
            final Value value = Value.of(config.frequency().value().toString());
            return Frequency.fixed(value, context.tempo());
        }
    }

    private long getDuration(final Context context) {
        try {
            return Long.parseLong(config.duration());
        } catch(final NumberFormatException e) {
            try {
                final Value value = Value.of(config.duration());
                return value.length().toMillis(context.tempo());
            } catch (final Throwable ignore) {
                return 500;
            }
        }
    }

    public void stop(final Context context) {
        frequency.stop();
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
                                target.send(queue.remove().signal());
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

    private Frequency.Listener createFrequencyListener() {
        return now -> {
            final Signal on = Signal.on(DEFAULT_CHANNEL, Played.of(Pitch.MIDDLE_C, 100));
            config.tags().forEach(on::addTag);
            queue.add(new Entry(now, on));

            final Signal off = Signal.off(DEFAULT_CHANNEL, Played.of(Pitch.MIDDLE_C, 100));
            config.tags().forEach(off::addTag);
            queue.add(new Entry(now + duration, off));
        };
    }

    private static final int DEFAULT_CHANNEL = 1;

    private class Entry {
        private final long time;
        private final Signal signal;

        Entry(long time, Signal signal) {
            this.time = time;
            this.signal = signal;
        }

        public long time() {
            return time;
        }

        public Signal signal() {
            return signal;
        }
    }
}
