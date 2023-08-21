package mugres.common;

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
            length = length.plus(i.value());
        return length;
    }

    public static class Item {
        private final Value value;
        private final boolean rest;

        private Item(Value value, boolean rest) {
            this.value = value;
            this.rest = rest;
        }

        public Value value() {
            return value;
        }

        public boolean rest() {
            return rest;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return rest == item.rest && value == item.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, rest);
        }

        @Override
        public String toString() {
            return "Rhythm.Item{" +
                    "value=" + value +
                    ", silence=" + rest +
                    '}';
        }
    }

    public class Builder {
        private final List<Item> items = new ArrayList<>();

        public Builder add(final Value value, final boolean rest) {
            items.add(new Item(value, rest));
            return this;
        }

        public Builder note(final Value value) {
            return add(value, false);
        }

        public Builder rest(final Value value) {
            return add(value, true);
        }

        public Rhythm build() {
            return new Rhythm(items);
        }
    }
}
