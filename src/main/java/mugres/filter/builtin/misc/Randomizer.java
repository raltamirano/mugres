package mugres.filter.builtin.misc;

import mugres.common.*;
import mugres.filter.Filter;
import mugres.live.Signal;
import mugres.live.Signals;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static mugres.common.Pitch.BASE_OCTAVE;
import static mugres.utils.Randoms.random;

public class Randomizer extends Filter {
    public static final String NAME = "Randomizer";

    public Randomizer(final Map<String, Object> arguments) {
        super(arguments);
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
        final Signals result = Signals.create();
        final int startingOctave = getStartingOctave(arguments);
        final int octaves = getOctaves(arguments);
        final Mode mode = getMode(arguments);
        final boolean deactivationFollows = getDeactivationFollowsFlag(arguments);

        Scale scale = Scale.CHROMATIC;
        Note root = getRoot(context, arguments);
        if (mode != Mode.CHROMATIC)
            scale = getScale(context, arguments);

        final List<Pitch> availablePitches = scale.pitches(root, octaves, startingOctave);
        for(final Signal in : signals.signals()) {
            if (in.isNoteOn()) {
                final Pitch newPitch = random(availablePitches);
                result.add(in.modifiedPitch(newPitch));
                if (deactivationFollows)
                    RANDOMIZER_MAP.put(in.discriminator(), newPitch);
            } else {
                if (deactivationFollows) {
                    final Pitch randomizedPitch = RANDOMIZER_MAP.remove(in.discriminator());
                    if (randomizedPitch != null)
                        result.add(in.modifiedPitch((randomizedPitch)));
                    else
                        result.add(in.modifiedPitch((random(availablePitches))));
                } else {
                    result.add(in.modifiedPitch(random(availablePitches)));
                }
            }
        }

        return result;
    }

    private boolean getDeactivationFollowsFlag(final Map<String, Object> arguments) {
        try {
            return arguments.containsKey("deactivationFollows") ?
                    Boolean.parseBoolean(arguments.get("deactivationFollows").toString()) :
                    true;
        }  catch (final Throwable ignore) {
            return true;
        }
    }

    private Scale getScale(final Context context, final Map<String, Object> arguments) {
        try {
            return arguments.containsKey("scale") ?
                    Scale.of(arguments.get("scale").toString()) :
                    context.key().defaultScale();
        }  catch (final Throwable ignore) {
            return context.key().defaultScale();
        }
    }

    private Note getRoot(final Context context, final Map<String, Object> arguments) {
        try {
            return arguments.containsKey("root") ?
                Note.of(arguments.get("root").toString()) :
                context.key().root();
        } catch (final Throwable ignore) {
            return context.key().root();
        }
    }

    private int getStartingOctave(final Map<String, Object> arguments) {
        try {
            return arguments.containsKey("startingOctave") ?
                    Double.valueOf(arguments.get("startingOctave").toString()).intValue() :
                    BASE_OCTAVE;
        } catch (final Throwable ignore) {
            return BASE_OCTAVE;
        }
    }

    private int getOctaves(final Map<String, Object> arguments) {
        try {
            return arguments.containsKey("octaves") ?
                    Double.valueOf(arguments.get("octaves").toString()).intValue() :
                    1;
        } catch (final Throwable ignore) {
            return 1;
        }
    }

    private Mode getMode(final Map<String, Object> arguments) {
        return Mode.valueOf(arguments.get("mode").toString());
    }

    public enum Mode {
        CHROMATIC,
        DIATONIC
    }

    private final Map<Integer, Pitch> RANDOMIZER_MAP = new ConcurrentHashMap<>();
}
