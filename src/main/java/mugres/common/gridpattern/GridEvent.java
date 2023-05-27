package mugres.common.gridpattern;

public class GridEvent<E> implements Comparable<GridEvent<E>> {
    private final int slot;
    private final String element;
    private final E data;

    private GridEvent(final int slot, final String element, final  E data) {
        this.slot = slot;
        this.element = element;
        this.data = data;
    }

    public static <X> GridEvent of(final int slot, final String element, final X data) {
        return new GridEvent(slot, element, data);
    }

    public int getSlot() {
        return slot;
    }

    public String getElement() {
        return element;
    }

    public E getData() {
        return data;
    }

    public boolean isEmpty() {
        return  data == null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(String.format("Slot: %s%n", slot));
        sb.append(String.format("Element: %s%n", element));
        sb.append(String.format("Empty: %s%n", isEmpty()));
        if (!isEmpty())
            sb.append(String.format("Data: %s%n", data));

        return sb.toString();
    }

    @Override
    public int compareTo(GridEvent<E> o) {
        return Integer.compare(slot, o.slot);
    }
}
