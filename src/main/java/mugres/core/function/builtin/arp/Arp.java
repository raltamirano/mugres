package mugres.core.function.builtin.arp;

import mugres.core.common.*;
import mugres.core.function.Function.EventsFunction;
import mugres.core.function.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import java.util.stream.Collectors;

import static mugres.core.common.Value.QUARTER;
import static mugres.core.function.Function.Parameter.DataType.*;
import static mugres.core.function.builtin.arp.Utils.ARP_PATTERN;
import static mugres.core.function.builtin.arp.Utils.REST;
import static mugres.core.utils.Randoms.random;
import static mugres.core.utils.Utils.rangeClosed;

public class Arp extends EventsFunction {
    public Arp() {
        super("arp", "Arpeggiates composed call's events",
                Parameter.of("pattern", "Arp pattern",
                        TEXT, true, "1232"),
                Parameter.of("octavesUp", "Octaves up (for transposition)",
                        INTEGER, true, 0),
                Parameter.of("octavesDown", "Octaves down (for transposition)",
                        INTEGER, true, 0),
                Parameter.of("restart", "Restart the pattern every new chord",
                        BOOLEAN, true, true)

        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final Result<List<Event>> composed = getComposedCallResult(arguments);
        final List<Event> events = new ArrayList<>();
        final String pattern = (String) arguments.get("pattern");
        final int octavesUp = (Integer) arguments.get("octavesUp");
        final int octavesDown = (Integer) arguments.get("octavesDown");
        final boolean restart = (Boolean) arguments.get("restart");
        final Matcher matcher = ARP_PATTERN.matcher(pattern);

        // Big assumptions here:
        // all chord notes start at the same position and last for the same amount of time (value)

        extractPositions(composed.getData())
                .stream()
                .map(p -> getChordEvents(composed.getData(), p))
                .map(c -> arpeggiate(c, matcher, octavesUp, octavesDown, restart))
                .forEach(events::addAll);

        return events;
    }

    private static List<Length> extractPositions(final List<Event> data) {
        return data.stream()
                .map(Event::getPosition)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private static List<Event> getChordEvents(final List<Event> data, final Length position) {
        return data.stream()
                .filter(e -> e.getPosition().equals(position))
                .collect(Collectors.toList());
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
                    final int index = Integer.parseInt(element);
                    final Event event = index < chord.size() ? chord.get(index) : chord.get(0);
                    arpeggio.add(Event.of(position, getActualPitch(event.getPlayed().getPitch(), octavesUp, octavesDown), actualValue, event.getPlayed().getVelocity()));
                }

                position = position.plus(value.length());
                controlLength = controlLength.plus(value.length());
            }
        }

        return arpeggio;
    }

    private static Pitch getActualPitch(final Pitch pitch, final int octavesUp, final int octavesDown) {
        try {
            if (octavesUp == 0 && octavesDown == 0) return pitch;
            if (octavesUp < 0 || octavesDown < 0) return pitch;

            final int originalOctave = pitch.getOctave();
            final List<Integer> octaves = rangeClosed(originalOctave - octavesDown, originalOctave + octavesUp);
            final int newOctave = random(octaves);
            final int octaveDiff = originalOctave - newOctave;

            if (octaveDiff > 0)
                return pitch.down(octaveDiff * 12);
            else if (octaveDiff < 0)
                return pitch.up(octaveDiff * 12);
            else
                return pitch;
        } catch (final Throwable ignore) {
            return pitch;
        }
    }

    private static Value parseNoteValue(final String input) {
        return input == null || input.trim().isEmpty() ? QUARTER : Value.forId(input);
    }
}
