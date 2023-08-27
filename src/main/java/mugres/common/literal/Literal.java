package mugres.common.literal;

import mugres.common.Length;
import mugres.tracker.Event;
import mugres.utils.Events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Literal {
    private final List<Event> events;

    private Literal(final List<Event> events) {
        if (events == null)
            throw new IllegalArgumentException("events");

        this.events = Collections.unmodifiableList(new ArrayList<>(events));
    }

    public static Literal of(final List<Event> events) {
        return new Literal(events);
    }

    public List<Event> events() {
        return events;
    }

    public List<Event> takeUpTo(final Length length) {
        return Events.takeUpTo(events, length);
    }
}
