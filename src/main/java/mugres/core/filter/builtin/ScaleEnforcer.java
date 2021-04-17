package mugres.core.filter.builtin;

import mugres.core.common.*;
import mugres.core.filter.Filter;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ScaleEnforcer extends Filter {
    public ScaleEnforcer() {
        super("ScaleEnforcer");
    }

    @Override
    protected boolean canHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals handle(final Context context, final  Signals signals, final Map<String, Object> arguments) {
        final Signals result = Signals.create();
        final List<Note> scaleNotes = getScaleNotes(context, arguments);
        final CorrectionMode correctionMode = getCorrectionMode(arguments);

        for(final Signal in : signals.signals()) {
            if (scaleNotes.contains(in.getPlayed().getPitch().getNote()))
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
        final Note playedNote = in.getPlayed().getPitch().getNote();

        for (int i = 0; i < scaleNotes.size(); i++) {
            if (scaleNotes.get(i).number() > playedNote.number()) {
                final Note targetNote = scaleNotes.get(i);
                Pitch newPitch = Pitch.of(targetNote, in.getPlayed().getPitch().getOctave());
                if (newPitch.getMidi() < in.getPlayed().getPitch().getMidi())
                    newPitch = newPitch.up(Interval.OCTAVE);
                result.add(in.modifiedPlayed(Played.of(newPitch, in.getPlayed().getVelocity())));
                break;
            }
        }
    }

    private void correctDown(final Signals result, final List<Note> scaleNotes, final Signal in) {
        final Note playedNote = in.getPlayed().getPitch().getNote();

        for (int i = scaleNotes.size() - 1; i >= 0; i--) {
            if (scaleNotes.get(i).number() < playedNote.number()) {
                final Note targetNote = scaleNotes.get(i);
                Pitch newPitch = Pitch.of(targetNote, in.getPlayed().getPitch().getOctave());
                if (newPitch.getMidi() > in.getPlayed().getPitch().getMidi())
                    newPitch = newPitch.down(Interval.OCTAVE);
                result.add(in.modifiedPlayed(Played.of(newPitch, in.getPlayed().getVelocity())));
                break;
            }
        }
    }

    private List<Note> getScaleNotes(final Context context, final Map<String, Object> arguments) {
        final Scale scale = Scale.of(arguments.get("scale").toString());
        final Note root = Note.of(arguments.get("root").toString());

        return scale.notes(root);
    }

    private CorrectionMode getCorrectionMode(Map<String, Object> arguments) {
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
