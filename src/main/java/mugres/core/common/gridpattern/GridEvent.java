package mugres.core.common.gridpattern;

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

    @Override
    public int compareTo(GridEvent<E> o) {
        return Integer.compare(slot, o.slot);
    }
}
