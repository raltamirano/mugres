package mugres.core.function.builtin.bm;

import mugres.core.common.*;
import mugres.core.function.builtin.riffer.Riffer;
import mugres.core.function.common.ByStrategiesFunction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static mugres.core.common.Value.SIXTEENTH;
import static mugres.core.function.Function.WellKnownFunctions.RIFFER;

public class BlackMetal extends ByStrategiesFunction {
    public BlackMetal() {
        super("blackMetal", "Creates Black Metal riffs");

        addStrategy(new Strategy0001());
    }

    static class Strategy0001 implements Strategy {
        @Override
        public List<Event> execute(final Context context, final int totalMeasures) {
            final List<Event> events = new ArrayList<>();

            final Note root = context.getKey().getRoot();
            final String chordType1 = random(BM_CHORD_TYPES);
            final String chordType2 = random(BM_CHORD_TYPES);
            final String pattern = "rct1 | rct2 | rct1 | rct2 |";

            events.addAll(RIFFER.execute(context, pattern
                    .replaceAll("r", root.name())
                    .replaceAll("ct1", chordType1)
                    .replaceAll("ct2", chordType2)));

            return events;
        }

        @Override
        public int getMeasures() {
            return 4;
        }

        private static final List<String> BM_CHORD_TYPES = asList("3", "b4", "4", "b5", "5", "b6");
    }

    /** Creates a 16th note event at desired position */
    private Event e16th(final Length position, final Pitch pitch) {
        return Event.of(position, pitch, SIXTEENTH, 100);
    }
}
