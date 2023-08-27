package mugres.function.builtin.arp;

import mugres.common.Context;
import mugres.tracker.Event;
import mugres.common.Length;
import mugres.common.Pitch;
import mugres.function.Function.EventsFunction;
import mugres.parametrizable.Parameter;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static mugres.common.DataType.PITCH;
import static mugres.common.DataType.TEXT;
import static mugres.function.utils.Arpeggios.arpeggiate;

public class Arp extends EventsFunction {
    public Arp() {
        super("arp", "Arpeggiates provided pitches",
                Parameter.of(PITCHES, "Pitches", 1,  "Ordered list of pitches to arpeggiate",
                        PITCH, false, emptyList(), true, false),
                Parameter.of(PATTERN, "Pattern", 2, "Arp pattern",
                        TEXT, false, null)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final List<Pitch> pitches = (List<Pitch>) arguments.get(PITCHES);
        final String pattern = (String) arguments.get(PATTERN);
        return arpeggiate(pitches, pattern, length);
    }

    public static final String PITCHES = "pitches";
    public static final String PATTERN = "pattern";
}
