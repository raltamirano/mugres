package mugres.core.live.signaler;

import mugres.core.common.*;
import mugres.core.common.frequency.Frequency;
import mugres.core.common.frequency.builtin.Fixed;
import mugres.core.common.io.Input;
import mugres.core.live.signaler.config.Configuration;

import java.util.UUID;

public class Signaler {
    private final Configuration config;
    private Input target;
    private Frequency frequency;
    private long duration;

    private Signaler(final Configuration config) {
        if (config == null)
            throw new IllegalArgumentException("config");

        this.config = config;
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
            final long millis = Long.parseLong(config.getFrequency().getValue());
            return Frequency.fixed(millis);
        } catch(final NumberFormatException e) {
            final Value value = Value.forId(config.getFrequency().getValue().toString());
            return Frequency.fixed(value, context.getTempo());
        }
    }

    private long getDuration(final Context context) {
        try {
            return Long.parseLong(config.getDuration());
        } catch(final NumberFormatException e) {
            try {
                final Value value = Value.forId(config.getDuration());
                return value.length().toMillis(context.getTempo());
            } catch (final Throwable ignore) {
                return 500;
            }
        }
    }

    public void stop(final Context context) {
        frequency.stop();
    }

    private Frequency.Listener createFrequencyListener() {
        return () -> {
            final long now = System.currentTimeMillis();

            final Signal on = Signal.on(UUID.randomUUID(), now, DEFAULT_CHANNEL,
                    Played.of(Pitch.MIDDLE_C, 100));
            config.getTags().forEach(on::addTag);
            target.send(on);

            final Signal off = Signal.off(UUID.randomUUID(), now + duration, DEFAULT_CHANNEL,
                    Played.of(Pitch.MIDDLE_C, 100));
            config.getTags().forEach(off::addTag);
            target.send(off);
        };
    }

    private static final int DEFAULT_CHANNEL = 1;
}
