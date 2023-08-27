package mugres.function.builtin.literal;

import mugres.common.Context;
import mugres.common.DataType;
import mugres.common.Length;
import mugres.function.Function.EventsFunction;
import mugres.function.utils.EventAccumulator;
import mugres.parametrizable.Parameter;
import mugres.tracker.Event;
import java.util.List;
import java.util.Map;

import static mugres.function.utils.EventAccumulator.OnExcessAction.SHORTEN;

public class Literal extends EventsFunction {
    public Literal() {
        super("literal", "Literal",
                Parameter.of(LITERAL, "Literal", 1, "Literal events to play",
                        DataType.LITERAL, false),
                Parameter.of(LOOP, "Loop", 2, "Keep looping until max length is reached",
                        DataType.BOOLEAN, true, false)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final boolean loop = (boolean)arguments.get(LOOP);
        final mugres.common.literal.Literal literal = (mugres.common.literal.Literal) arguments.get(LITERAL);

        if (loop)
            return EventAccumulator.of(length, SHORTEN).fillWith(literal.events()).accumulated();
        else
            return literal.takeUpTo(length);
    }

    public static final String LITERAL = "literal";
    public static final String LOOP = "loop";
}
