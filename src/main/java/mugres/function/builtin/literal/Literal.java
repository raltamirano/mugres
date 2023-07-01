package mugres.function.builtin.literal;

import mugres.common.Context;
import mugres.common.DataType;
import mugres.common.Length;
import mugres.function.Function.EventsFunction;
import mugres.parametrizable.Parameter;
import mugres.tracker.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mugres.tracker.Song.MAX_BEAT_SUBDIVISION;
import static mugres.tracker.Song.MIN_BEAT_SUBDIVISION;

public class Literal extends EventsFunction {
    public Literal() {
        super("literal", "Literal",
                Parameter.of("beatSubdivision", "Beat Subdivision", 1,
                        "Beat Subdivision", DataType.INTEGER, false, MIN_BEAT_SUBDIVISION,
                        MIN_BEAT_SUBDIVISION, MAX_BEAT_SUBDIVISION, false),
                Parameter.of(LITERAL, "Literal", 2, "Literal events to play",
                        DataType.LITERAL, false)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = new ArrayList<>();
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        return events;
    }

    public static final String LITERAL = "literal";
}
