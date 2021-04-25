package mugres.core.filter.builtin.misc;

import mugres.core.common.*;
import mugres.core.filter.Filter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static mugres.core.common.Note.BASE_OCTAVE;
import static mugres.core.utils.Randoms.random;

public class Randomizer extends Filter {
    public Randomizer() {
        super("Randomizer");
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
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
            if (in.isActive()) {
                final Pitch newPitch = random(availablePitches);
                result.add(in.modifiedPlayed(in.getPlayed().repitch(newPitch)));
                if (deactivationFollows)
                    RANDOMIZER_MAP.put(in.discriminator(), newPitch);
            } else {
                if (deactivationFollows) {
                    final Pitch randomizedPitch = RANDOMIZER_MAP.remove(in.discriminator());
                    if (randomizedPitch != null)
                        result.add(in.modifiedPlayed(in.getPlayed().repitch(randomizedPitch)));
                    else
                        result.add(in.modifiedPlayed(in.getPlayed().repitch(random(availablePitches))));
                } else {
                    result.add(in.modifiedPlayed(in.getPlayed().repitch(random(availablePitches))));
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
                    context.getKey().defaultScale();
        }  catch (final Throwable ignore) {
            return context.getKey().defaultScale();
        }
    }

    private Note getRoot(final Context context, final Map<String, Object> arguments) {
        try {
            return arguments.containsKey("root") ?
                Note.of(arguments.get("root").toString()) :
                context.getKey().getRoot();
        } catch (final Throwable ignore) {
            return context.getKey().getRoot();
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
