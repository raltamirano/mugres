package mugres.common;

import mugres.tracker.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static mugres.common.Pitch.DEFAULT_VELOCITY;

public class Rhythm {
    private final List<Item> items;

    private Rhythm(final List<Item> items) {
        if (items == null)
            throw new IllegalArgumentException("items");
        if (items.stream().filter(Item::rest).count() == items.size())
            throw new IllegalArgumentException("A rhythm can't be comprised of rests only!");

        this.items = new ArrayList<>(items);
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Item> items() {
        return Collections.unmodifiableList(items);
    }

    public Length length() {
        Length length = Length.ZERO;
        for (Item i : items)
            length = length.plus(i.length());
        return length;
    }

    public List<Event> applyToEvents(final List<Event> events) {
        if (events == null)
            throw new IllegalArgumentException("events");
        if (events.isEmpty())
            return Collections.emptyList();

        final List<Event> filteredEvents = events.stream()
                .filter(e -> !e.rest())
                .collect(Collectors.toList());
        if (filteredEvents.isEmpty())
            return Collections.emptyList();

        final List<Event> result = new ArrayList<>();
        boolean keepAddingEvents = true;
        int eventsIndex = 0;
        Length startPosition = Length.ZERO;
        while(keepAddingEvents) {
            for(Item item : items) {
                final Length newPosition = startPosition.plus(item.position());
                final Event eventToAdd;
                if (item.rest()) {
                    eventToAdd = Event.rest(newPosition, item.length());
                } else {
                    eventToAdd = filteredEvents.get(eventsIndex++ % filteredEvents.size())
                            .withPosition(newPosition)
                            .withLength(item.length());
                }
                result.add(eventToAdd);
            }
            keepAddingEvents = eventsIndex < (filteredEvents.size() - 1);
            startPosition = startPosition.plus(length());
        }

        return result;
    }

    public List<Event> applyToPitches(final List<Pitch> pitches) {
        if (pitches == null)
            throw new IllegalArgumentException("pitches");
        if (pitches.isEmpty())
            return Collections.emptyList();

        final List<Event> result = new ArrayList<>();
        boolean keepAddingEvents = true;
        int pitchesIndex = 0;
        Length startPosition = Length.ZERO;
        while(keepAddingEvents) {
            for(Item item : items) {
                final Length newPosition = startPosition.plus(item.position());
                final Event eventToAdd;
                if (item.rest()) {
                    eventToAdd = Event.rest(newPosition, item.length());
                } else {
                    eventToAdd = Event.of(newPosition,
                            pitches.get(pitchesIndex++ % pitches.size()),
                            item.length(), DEFAULT_VELOCITY);
                }
                result.add(eventToAdd);
            }
            keepAddingEvents = pitchesIndex < (pitches.size() - 1);
            startPosition = startPosition.plus(length());
        }

        return result;
    }

    public static class Item {
        private final Length position;
        private final Length length;
        private final boolean rest;

        private Item(final Length position, final Length length, final boolean rest) {
            this.position = position;
            this.length = length;
            this.rest = rest;
        }

        public Length position() {
            return position;
        }

        public Length length() {
            return length;
        }

        public boolean rest() {
            return rest;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return rest == item.rest && length == item.length && position == item.position;
        }

        @Override
        public int hashCode() {
            return Objects.hash(length, rest);
        }

        @Override
        public String toString() {
            return "Rhythm.Item{" +
                    "position=" + position +
                    ",length=" + length +
                    ", silence=" + rest +
                    '}';
        }
    }

    public static class Builder {
        private Length position = Length.ZERO;
        private final List<Item> items = new ArrayList<>();

        public Builder add(final Length length, final boolean rest) {
            items.add(new Item(position, length, rest));
            position = position.plus(length);
            return this;
        }

        public Builder add(final Value value, final boolean rest) {
            return add(value.length(), rest);
        }

        public Builder note(final Length length) {
            return add(length, false);
        }
        public Builder note(final Value value) {
            return add(value, false);
        }

        public Builder triplet(final Length spanning,
                               final boolean oneIsRest,
                               final boolean twoIsRest,
                               final boolean threeIsRest) {
            final Length third = spanning.divide(3);
            final Length remainder = spanning.remainder(3);

            final Length length1 = third;
            final Length length2 = third.plus(remainder);
            final Length length3 = third;

            add(length1, oneIsRest);
            add(length2, twoIsRest);
            add(length3, threeIsRest);

            return this;
        }

        public Builder triplet(final Length spanning) {
            return triplet(spanning, false, false, false);
        }

        public Builder tripletNRN(final Length spanning) {
            return triplet(spanning, false, true, false);
        }

        public Builder triplet(final Value value) {
            return triplet(value.length());
        }

        public Builder tripletNRN(final Value value) {
            return triplet(value.length(), false, true, false);
        }

        public Builder rest(final Length length) {
            return add(length, true);
        }

        public Builder rest(final Value value) {
            return add(value, true);
        }

        public Rhythm build() {
            return new Rhythm(items);
        }
    }
}
