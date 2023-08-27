package mugres.common;

import mugres.tracker.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Rhythm {
    private final List<Item> items;

    private Rhythm(final List<Item> items) {
        if (items == null)
            throw new IllegalArgumentException("items");

        this.items = new ArrayList<>(items);
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

    public class Builder {
        private final List<Item> items = new ArrayList<>();

        public Builder add(final Length position, final Value value, final boolean rest) {
            items.add(new Item(position, value.length(), rest));
            return this;
        }

        public Builder add(final Length position, final Length length, final boolean rest) {
            items.add(new Item(position, length, rest));
            return this;
        }

        public Builder note(final Length position, final Length length) {
            return add(position, length, false);
        }
        public Builder note(final Length position, final Value value) {
            return add(position, value, false);
        }

        public Builder rest(final Length position, final Length length) {
            return add(position, length, true);
        }

        public Builder rest(final Length position, final Value value) {
            return add(position, value, true);
        }

        public Rhythm build() {
            return new Rhythm(items);
        }
    }
}
