package mugres.core.function;

import mugres.core.common.Event;

import java.util.Collections;
import java.util.List;

public class Result {
    private final List<Event> events;
    private final Throwable error;

    public Result(final List<Event> events) {
        if (events == null)
            throw new IllegalArgumentException("events");

        this.events = events;
        this.error = null;
    }

    public Result(final Throwable error) {
        if (error == null)
            throw new IllegalArgumentException("error");

        this.events = Collections.emptyList();
        this.error = error;
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public Throwable getError() {
        return error;
    }

    public boolean succeeded() {
        return error == null;
    }
}
