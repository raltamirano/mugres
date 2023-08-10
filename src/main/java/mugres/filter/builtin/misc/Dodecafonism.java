package mugres.filter.builtin.misc;

import mugres.common.Context;
import mugres.common.Note;
import mugres.common.Pitch;
import mugres.common.ttm.TwelveToneMatrix;
import mugres.filter.Filter;
import mugres.live.Signal;
import mugres.live.Signals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class Dodecafonism extends Filter {
    public static final String NAME = "Dodecafonism";
    private final TwelveToneMatrix twelveToneMatrix;
    private final Map<Pitch, Pitch> mapping = new HashMap<>();
    private int index = 0;

    public Dodecafonism(final Map<String, Object> arguments) {
        super(arguments);

        twelveToneMatrix = getTwelveToneMatrix(arguments);
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

        for (Signal s : signals.signals()) {
            if (s.isNoteOn()) {
                if (!mapping.containsKey(s.pitch())) {
                    final Pitch toPlay = getNextNote().pitch(s.pitch().octave());
                    mapping.put(s.pitch(), toPlay);
                    result.add(s.modifiedPitch(toPlay));
                }
            } else {
                final Pitch playing = mapping.remove(s.pitch());
                if (playing != null) {
                    result.add(s.modifiedPitch(playing));
                }
            }
        }

        return result;
    }

    private Note getNextNote() {
        return twelveToneMatrix.noteAt(0, index++ % TwelveToneMatrix.SIZE);
    }

    private TwelveToneMatrix getTwelveToneMatrix(final Map<String, Object> arguments) {
        if (arguments.containsKey("ttm"))
            return new TwelveToneMatrix(Arrays.stream(arguments.get("ttm").toString().split(";"))
                    .map(Note::of).collect(Collectors.toList()));
        return new TwelveToneMatrix();
    }
}
