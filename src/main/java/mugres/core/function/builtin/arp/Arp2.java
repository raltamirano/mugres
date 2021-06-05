package mugres.core.function.builtin.arp;

import mugres.core.common.Context;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.common.Pitch;
import mugres.core.common.Value;
import mugres.core.function.Function.EventsFunction;
import mugres.core.function.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static mugres.core.common.Value.QUARTER;
import static mugres.core.function.Function.Parameter.DataType.BOOLEAN;
import static mugres.core.function.Function.Parameter.DataType.INTEGER;
import static mugres.core.function.Function.Parameter.DataType.PITCH;
import static mugres.core.function.Function.Parameter.DataType.TEXT;
import static mugres.core.function.builtin.arp.Utils.ARP_PATTERN;
import static mugres.core.utils.Randoms.random;
import static mugres.core.utils.Utils.rangeClosed;
import static mugres.core.function.builtin.arp.Utils.REST;

public class Arp2 extends EventsFunction {
    public Arp2() {
        super("arp2", "Arpeggiates provided pitches",
                Parameter.of(PITCHES, "Ordered pitches to arpeggiate",
                        PITCH, false, emptyList(), true),
                Parameter.of(PATTERN, "Arp pattern",
                        TEXT, false, null)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = new ArrayList<>();
        final String pattern = (String) arguments.get("pattern");
        final Matcher matcher = ARP_PATTERN.matcher(pattern);

        return events;
    }


    private static List<Event> arpeggiate(final List<Event> chord, final Matcher matcher,
                                          final int octavesUp, final int octavesDown,
                                          final boolean restart) {
        final List<Event> arpeggio = new ArrayList<>();
        final Length totalLength = chord.get(0).getValue().length();

        Length position = chord.get(0).getPosition();
        Length controlLength = Length.ZERO;

        if (restart)
            matcher.reset();

        while(controlLength.lessThan(totalLength)) {
            if (matcher.hitEnd())
                matcher.reset();

            while(controlLength.lessThan(totalLength) && matcher.find()) {
                final String element = matcher.group(2);
                final boolean isRest = REST.equals(element);
                final Value value = parseNoteValue(matcher.group(3));
                final Value actualValue = controlLength.plus(value.length()).greaterThan(totalLength) ?
                        Value.forLength(totalLength.minus(controlLength)) : value;

                if (!isRest) {
                    final int index = isRest ? 0 : Integer.parseInt(element);
                    final Event event = index < chord.size() ? chord.get(index) : chord.get(0);
                    arpeggio.add(Event.of(position, getActualPitch(event.getPlayed().getPitch(), octavesUp, octavesDown), actualValue, event.getPlayed().getVelocity()));
                }

                position = position.plus(value.length());
                controlLength = controlLength.plus(value.length());
            }
        }

        return arpeggio;
    }

    public static final String PITCHES = "pitches";
    public static final String PATTERN = "pattern";
}
