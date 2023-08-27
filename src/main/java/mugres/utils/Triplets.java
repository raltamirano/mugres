package mugres.utils;

import mugres.common.Length;
import mugres.common.Pitch;
import mugres.common.Value;
import mugres.tracker.Event;

import java.util.ArrayList;
import java.util.List;

import static mugres.common.Pitch.DEFAULT_VELOCITY;

public class Triplets {
    private Triplets() {}

    public static List<Event> triplet(final Value duration,
                                      final Pitch pitch) {
        return triplet(duration, pitch, pitch, pitch, DEFAULT_VELOCITY, DEFAULT_VELOCITY, DEFAULT_VELOCITY);
    }

    public static List<Event> triplet(final Value duration,
                                      final Pitch pitch1,
                                      final Pitch pitch2,
                                      final Pitch pitch3) {
        return triplet(duration, pitch1, pitch2, pitch3, DEFAULT_VELOCITY, DEFAULT_VELOCITY, DEFAULT_VELOCITY);
    }

    public static List<Event> triplet(final Value duration,
                                      final Pitch pitch,
                                      final int velocity1,
                                      final int velocity2,
                                      final int velocity3) {
        return triplet(duration, pitch, pitch, pitch, velocity1, velocity2, velocity3);
    }

    public static List<Event> triplet(final Value duration,
                                      final Pitch pitch1,
                                      final Pitch pitch2,
                                      final Pitch pitch3,
                                      final int velocity1,
                                      final int velocity2,
                                      final int velocity3) {
        final List<Event> result = new ArrayList<>();

        final Length third = duration.length().divide(3);
        final Length remainder = duration.length().remainder(3);

        final Length position1 = Length.ZERO;
        final Length position2 = third;
        final Length position3 = third.multiply(2).plus(remainder);
        final Length length1 = third;
        final Length length2 = third.plus(remainder);
        final Length length3 = third;

        result.add(pitch1 != null ? Event.of(position1, pitch1, length1, velocity1) : Event.rest(position1, length1));
        result.add(pitch2 != null ? Event.of(position2, pitch2, length2, velocity2) : Event.rest(position2, length2));
        result.add(pitch3 != null ? Event.of(position3, pitch3, length3, velocity3) : Event.rest(position3, length3));

        return result;
    }
}
