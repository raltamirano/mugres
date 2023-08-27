package mugres.utils;

import mugres.common.Length;
import mugres.common.Pitch;
import mugres.common.Value;
import mugres.tracker.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Events {
    private Events() {}

    public static List<Event> takeUpTo(final List<Event> from, final Length requested) {
        if (from == null)
            throw new IllegalArgumentException("from");
        if (requested == null)
            throw new IllegalArgumentException("requested");
        if (requested.isEmpty())
            return Collections.emptyList();

        final List<Event> result = new ArrayList<>();
        Length taken = Length.ZERO;
        int index = 0;
        while(taken.lessThan(requested)) {
            if (index > from.size() - 1)
                break;
            final Event event = from.get(index++);
            result.add(event);
            taken = taken.plus(event.length());
        }

        return result;
    }

    public static List<Event> fromPitches(final List<Pitch> pitches, final Value value,
                                          final int velocity) {
        final List<Event> result = new ArrayList<>();
        Length position = Length.ZERO;
        for(Pitch pitch : pitches) {
            result.add(Event.of(position, pitch, value, velocity));
            position = position.plus(value.length());
        }
        return result;

    }

    public static List<Event> fromPitches(final List<Pitch> pitches, final Length length,
                                          final int velocity) {
        final List<Event> result = new ArrayList<>();
        Length position = Length.ZERO;
        for(Pitch pitch : pitches) {
            result.add(Event.of(position, pitch, length, velocity));
            position = position.plus(length);
        }
        return result;
    }

    public static List<Event> toRelativeToZero(final List<Event> events) {
        if (events == null)
            throw new IllegalArgumentException("events");
        if (events.isEmpty())
            return Collections.emptyList();

        final List<Event> result = new ArrayList<>();
        final Length minPosition = events.stream().min(Comparator.naturalOrder()).get().position();
        events.forEach(e -> result.add(e.withPosition(e.position().minus(minPosition))));
        return result;
    }

    public static List<Event> offset(final List<Event> events, final Length offset) {
        if (events == null)
            throw new IllegalArgumentException("events");
        if (offset == null)
            throw new IllegalArgumentException("offset");
        if (events.isEmpty())
            return Collections.emptyList();

        final List<Event> result = new ArrayList<>();
        events.forEach(e -> result.add(e.offset(offset)));
        return result;
    }
}
