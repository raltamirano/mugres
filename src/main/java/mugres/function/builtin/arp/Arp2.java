package mugres.function.builtin.arp;

import mugres.common.Context;
import mugres.tracker.Event;
import mugres.common.Length;
import mugres.common.Pitch;
import mugres.common.Value;
import mugres.function.Function.EventsFunction;
import mugres.parametrizable.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static java.util.Collections.emptyList;
import static mugres.common.Pitch.DEFAULT_VELOCITY;
import static mugres.common.DataType.PITCH;
import static mugres.common.DataType.TEXT;
import static mugres.function.builtin.arp.Utils.ARP_PATTERN;
import static mugres.function.builtin.arp.Utils.REST;
import static mugres.function.builtin.arp.Utils.parseNoteValue;

public class Arp2 extends EventsFunction {
    public Arp2() {
        super("arp2", "Arpeggiates provided pitches",
                Parameter.of(PITCHES, "Ordered list of pitches to arpeggiate",
                        PITCH, false, emptyList(), true),
                Parameter.of(PATTERN, "Arp pattern",
                        TEXT, false, null)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final List<Pitch> pitches = (List<Pitch>) arguments.get(PITCHES);
        final String pattern = (String) arguments.get(PATTERN);
        final Matcher matcher = ARP_PATTERN.matcher(pattern);

        if (!matcher.matches())
            return emptyList();

        return arpeggiate(pitches, matcher, length);
    }


    private static List<Event> arpeggiate(final List<Pitch> pitches, final Matcher matcher,
                                          final Length totalLength) {
        final List<Event> arpeggio = new ArrayList<>();

        Length position = Length.ZERO;
        Length controlLength = Length.ZERO;

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
                    final int index = Integer.parseInt(element) - 1;
                    if (index >= 0 && index < pitches.size())
                        arpeggio.add(Event.of(position, pitches.get(index), actualValue, DEFAULT_VELOCITY));
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
