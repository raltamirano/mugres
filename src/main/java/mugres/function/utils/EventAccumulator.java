package mugres.function.utils;

import mugres.common.Length;
import mugres.tracker.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EventAccumulator {
    private final Length totalLength;
    private final OnExcessAction onExcessAction;
    private final List<Event> accumulated;
    private Length accumulatedLength;
    private boolean fulfilled;

    private EventAccumulator(final Length totalLength, final OnExcessAction onExcessAction) {
        if (totalLength == null)
            throw new IllegalArgumentException("totalLength");

        this.totalLength = totalLength;
        this.onExcessAction = onExcessAction;
        this.accumulated = new ArrayList<>();
        this.accumulatedLength = Length.ZERO;
        this.fulfilled = accumulatedLength.equals(totalLength);
    }

    public static EventAccumulator of(final Length length, final OnExcessAction onExcessAction) {
        return new EventAccumulator(length, onExcessAction);
    }

    public Length length() {
        return totalLength;
    }

    public OnExcessAction onExcessAction() {
        return onExcessAction;
    }

    public List<Event> accumulated() {
        return Collections.unmodifiableList(accumulated);
    }

    public boolean fulfilled() {
        return fulfilled;
    }

    public boolean offer(final Event event) {
        if (event == null)
            throw new IllegalArgumentException("event");

        return offer(Collections.singletonList(event));
    }

    public boolean offer(final List<Event> events) {
        if (fulfilled || events == null || events.isEmpty())
            return fulfilled;

        final Iterator<Event> iterator = events.iterator();
        while(accumulatedLength.lessThan(totalLength) && iterator.hasNext()) {
            addEvent(iterator.next());
            if (fulfilled) break;
        }

        return fulfilled;
    }

    public void fillWith(final Event event) {
        if (event == null)
            throw new IllegalArgumentException("event");

        fillWith(Collections.singletonList(event));
    }

    public void fillWith(final List<Event> events) {
        if (events == null || events.isEmpty())
            throw new IllegalArgumentException("events");

        while (!fulfilled)
            offer(events);
    }

    public boolean fillWith(final List<Event> events, final Length lengthToFill) {
        if (events == null || events.isEmpty())
            throw new IllegalArgumentException("events");
        if (lengthToFill == null)
            throw new IllegalArgumentException("lengthToFill");

        Length control = Length.ZERO;
        while(!fulfilled && control.lessThan(lengthToFill)) {
            for (Event event : events) {
                if (fulfilled)
                    break;
                addEvent(event);
                control = control.plus(event.length());
            }
        }

        return fulfilled;
    }

    private boolean willExceedTotalLength(final Length proposedLength) {
        return accumulatedLength.plus(proposedLength).greaterThan(totalLength);
    }

    private Length unfulfilledLength() {
        return totalLength.minus(accumulatedLength);
    }

    private void addEvent(final Event event) {
        if (fulfilled)
            throw new IllegalStateException("Already fulfilled!");

        final boolean willExceed = willExceedTotalLength(event.length());
        final Length actualLength = willExceed ? unfulfilledLength() : event.length();

        if (!willExceed || onExcessAction == OnExcessAction.SHORTEN)
            accumulated.add(Event.of(accumulatedLength, event.pitch(), actualLength, event.velocity()));

        accumulatedLength = accumulatedLength.plus(actualLength);
        fulfilled = accumulatedLength.equals(totalLength);
    }

    public enum OnExcessAction {
        SHORTEN,
        LEAVE_OUT
    }
}
