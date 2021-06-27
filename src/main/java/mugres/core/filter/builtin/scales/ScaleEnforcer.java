package mugres.core.filter.builtin.scales;

import mugres.core.common.*;
import mugres.core.filter.Filter;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ScaleEnforcer extends Filter {
    public static final String NAME = "ScaleEnforcer";

    public ScaleEnforcer(final Map<String, Object> arguments) {
        super(arguments);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final  Signals signals) {
        final Signals result = Signals.create();
        final List<Note> scaleNotes = getScaleNotes(context, arguments);
        final CorrectionMode correctionMode = getCorrectionMode(arguments);

        for(final Signal in : signals.signals()) {
            if (scaleNotes.contains(in.getPlayed().pitch().getNote()))
                result.add(in);
            else
                try {
                    switch (correctionMode) {
                        case UP:
                            correctUp(result, scaleNotes, in);
                            break;
                        case DOWN:
                            correctDown(result, scaleNotes, in);
                            break;
                        case RANDOM:
                            if (RND.nextBoolean())
                                correctUp(result, scaleNotes, in);
                            else
                                correctDown(result, scaleNotes, in);
                            break;
                        case DISCARD:
                            // do nothing
                            break;
                    }
                } catch (final Throwable ignore) {
                    // discard event in case of any errors
                }
        }

        return result;
    }

    private void correctUp(final Signals result, final List<Note> scaleNotes, final Signal in) {
        Pitch newPitch = in.getPlayed().pitch();
        while(!scaleNotes.contains(newPitch.getNote()))
            newPitch = newPitch.up(1);
        if (newPitch.getMidi() < in.getPlayed().pitch().getMidi())
            newPitch = newPitch.up(Interval.OCTAVE);
        result.add(in.modifiedPlayed(Played.of(newPitch, in.getPlayed().velocity())));
    }

    private void correctDown(final Signals result, final List<Note> scaleNotes, final Signal in) {
        Pitch newPitch = in.getPlayed().pitch();
        while(!scaleNotes.contains(newPitch.getNote()))
            newPitch = newPitch.down(1);
        if (newPitch.getMidi() > in.getPlayed().pitch().getMidi())
            newPitch = newPitch.down(Interval.OCTAVE);
        result.add(in.modifiedPlayed(Played.of(newPitch, in.getPlayed().velocity())));
    }

    private List<Note> getScaleNotes(final Context context, final Map<String, Object> arguments) {
        try {
            if (arguments.containsKey("scale") && arguments.containsKey("root")) {
                final Scale scale = Scale.of(arguments.get("scale").toString());
                final Note root = Note.of(arguments.get("root").toString());
                return scale.notes(root);
            } else {
                return context.getKey().notes();
            }
        } catch (final Throwable ignore) {
            return context.getKey().notes();
        }
    }

    private CorrectionMode getCorrectionMode(final Map<String, Object> arguments) {
        return CorrectionMode.valueOf(arguments.get("correctionMode").toString());
    }

    public enum CorrectionMode {
        UP,
        DOWN,
        RANDOM,
        DISCARD
    }

    private static final Random RND = new Random();
}
