package mugres.function.utils;

import mugres.common.Length;
import mugres.common.Pitch;
import mugres.common.Value;
import mugres.tracker.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static mugres.common.Pitch.DEFAULT_VELOCITY;
import static mugres.function.builtin.arp.Utils.parseNoteValue;

public class Arpeggios {
    private Arpeggios() {}

    /**
     * A single arpeggio.
     */
    public static List<Event> arpeggiate(final List<Pitch> pitches, final String pattern) {
        final Matcher matcher = ARP_PATTERN.matcher(pattern);
        final List<Event> result = new ArrayList<>();

        Length position = Length.ZERO;
        while(matcher.find()) {
            final String element = matcher.group(2);
            final boolean isRest = REST.equals(element);
            final Value value = parseNoteValue(matcher.group(3));

            if (!isRest) {
                final int index = Integer.parseInt(element) - 1;
                if (index >= 0 && index < pitches.size())
                    result.add(Event.of(position, pitches.get(index), value, DEFAULT_VELOCITY));
            }

            position = position.plus(value.length());
        }

        return result;
    }

    /**
     * Arpeggio until the specified total length is reach.
     */
    public static List<Event> arpeggiate(final List<Pitch> pitches, final String pattern, final Length totalLength) {
        final Matcher matcher = ARP_PATTERN.matcher(pattern);
        if (!matcher.matches()) return emptyList();

        final List<Event> result = new ArrayList<>();

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
                        result.add(Event.of(position, pitches.get(index), actualValue, DEFAULT_VELOCITY));
                }

                position = position.plus(value.length());
                controlLength = controlLength.plus(value.length());
            }
        }

        return result;
    }

    public static final String REST = "R";
    public static final Pattern ARP_PATTERN = Pattern.compile("(([1-9]|" + REST + ")(w|h|q|e|s|t|m)?)+?");
}
