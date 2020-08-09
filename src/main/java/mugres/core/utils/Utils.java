package mugres.core.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private Utils() {}

    public static List<Integer> rangeClosed(final int start, final int end) {
        final List<Integer> items = new ArrayList<>();
        for (int index = start; index <= end; index++)
            items.add(index);
        return items;
    }
}
