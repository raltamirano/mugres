package mugres.function.utils;

import mugres.common.Length;
import mugres.common.Octave;
import mugres.common.Pitch;
import mugres.common.Value;
import mugres.tracker.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mugres.common.Pitch.DEFAULT_VELOCITY;
import static mugres.common.Value.QUARTER;
import static mugres.function.utils.EventAccumulator.OnExcessAction.SHORTEN;

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
            final String element = matcher.group(1);
            final boolean isRest = REST.equals(element);
            final Length length = parseNoteValues(matcher.group(2));
            final Octave octave = matcher.group(3) == null ? Octave.SAME : Octave.of(matcher.group(3));

            if (isRest) {
                result.add(Event.rest(position, length));
            } else {
                final int index = Integer.parseInt(element) - 1;
                if (index >= 0 && index < pitches.size())
                    result.add(Event.of(position, octave.apply(pitches.get(index)), length, DEFAULT_VELOCITY));
            }

            position = position.plus(length);
        }

        return result;
    }

    /**
     * Arpeggio until the specified total length is reach.
     */
    public static List<Event> arpeggiate(final List<Pitch> pitches, final String pattern, final Length totalLength) {
        final EventAccumulator accumulator = EventAccumulator.of(totalLength, SHORTEN);
        accumulator.fillWith(Arpeggios.arpeggiate(pitches, pattern));
        return accumulator.accumulated();
    }

    private static Length parseNoteValues(final String input) {
        if (input == null || input.trim().isEmpty())
            return QUARTER.length();

        Length length = Length.ZERO;
        for (char c : input.toCharArray())
            length = length.plus(Value.of(c).length());

        return length;
    }

    public static final String REST = "R";
    public static final Pattern ARP_PATTERN = Pattern.compile("(?:([1-9A-K]|" + REST + ")([whqestm]*)(" + Octave.OCTAVE_PATTERN.pattern() + ")?)+?");
}
