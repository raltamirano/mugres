package mugres.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class Randoms {
    private Randoms() {}

    public static int randomBetween(final int lower, final int upper) {
        if (lower > upper)
            throw new IllegalArgumentException("upper");
        if (lower == upper)
            return lower;

        return lower + RND.nextInt((upper - lower) + 1);
    }

    public static <X> X random(final X[] items, final X... avoid) {
        return random(new ArrayList<X>(Arrays.asList(items)), avoid);
    }

    public static <X> X random(final Set<X> items, final X... avoid) {
        return random(new ArrayList<>(items), avoid);
    }

    public static <X> X random(final List<X> items, final X... avoid) {
        if (items.isEmpty())
            return null;

        final Set<X> avoidSet = new HashSet<>(asList(avoid));
        for(int i=0; i<10_000; i++) { // safety loop
            final X item = items.get(RND.nextInt(items.size()));
            if (!avoidSet.contains(item))
                return item;
        }

        throw new RuntimeException(String.format("Could not get random value. Items=%s - Exclusions: %s",
                items, avoidSet));
    }

    public static <X> List<X> randoms(final Set<X> items, final int count, final boolean allowDuplicates) {
        return randoms(new ArrayList<>(items), count, allowDuplicates);
    }

    public static <X> List<X> randoms(final List<X> items, final int count, final boolean allowDuplicates) {
        final List<X> result = new ArrayList<>();

        int safetyCounter = 0;
        while(safetyCounter++ < 10_000 && result.size() < count) {
            final X item = random(items);
            if (allowDuplicates || !result.contains(item))
                result.add(item);
        }

        if (result.size() == count)
            return result;

        throw new RuntimeException(String.format("Could not get random values. Items=%s - Count: %d",
                items, count));
    }

    public static final java.util.Random RND = new java.util.Random();
}
