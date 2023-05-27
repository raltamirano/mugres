package mugres.filter.builtin.scales;

import mugres.common.Context;
import mugres.common.Interval;
import mugres.common.Note;
import mugres.common.Pitch;
import mugres.common.Played;
import mugres.common.Scale;
import mugres.common.Signal;
import mugres.common.Signals;
import mugres.filter.Filter;

import java.util.List;
import java.util.Map;

import static mugres.utils.Randoms.RND;

public class ScaleEnforcer extends Filter {
    public static final String NAME = "ScaleEnforcer";

    public ScaleEnforcer(final Map<String, Object> arguments) {
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
    protected Signals internalHandle(final Context context, final  Signals signals) {
        final Signals result = Signals.create();
        final List<Note> scaleNotes = getScaleNotes(context, arguments);
        final CorrectionMode correctionMode = getCorrectionMode(arguments);

        for(final Signal in : signals.signals()) {
            if (scaleNotes.contains(in.played().pitch().note()))
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
        Pitch newPitch = in.played().pitch();
        while(!scaleNotes.contains(newPitch.note()))
            newPitch = newPitch.up(1);
        if (newPitch.midi() < in.played().pitch().midi())
            newPitch = newPitch.up(Interval.OCTAVE);
        result.add(in.modifiedPlayed(Played.of(newPitch, in.played().velocity())));
    }

    private void correctDown(final Signals result, final List<Note> scaleNotes, final Signal in) {
        Pitch newPitch = in.played().pitch();
        while(!scaleNotes.contains(newPitch.note()))
            newPitch = newPitch.down(1);
        if (newPitch.midi() > in.played().pitch().midi())
            newPitch = newPitch.down(Interval.OCTAVE);
        result.add(in.modifiedPlayed(Played.of(newPitch, in.played().velocity())));
    }

    private List<Note> getScaleNotes(final Context context, final Map<String, Object> arguments) {
        try {
            if (arguments.containsKey("scale") && arguments.containsKey("root")) {
                final Scale scale = Scale.of(arguments.get("scale").toString());
                final Note root = Note.of(arguments.get("root").toString());
                return scale.notes(root);
            } else {
                return context.key().notes();
            }
        } catch (final Throwable ignore) {
            return context.key().notes();
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
}
