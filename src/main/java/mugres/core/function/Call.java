package mugres.core.function;

import mugres.core.common.Context;
import mugres.core.common.Length;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static mugres.core.function.Function.LENGTH_PARAMETER;

/** Function call. */
public class Call {
    private final Function function;
    private final Map<String, Object> arguments = new HashMap<>();

    private Call(final Function function, final Map<String, Object> arguments) {
        this.function = function;
        this.arguments.putAll(arguments);
    }

    private Call(final Function function, final int lengthInMeasures) {
        this.function = function;
        this.arguments.put(LENGTH_PARAMETER.getName(), lengthInMeasures);
    }

    public static Call of(final Function function, final Map<String, Object> arguments) {
        return new Call(function, arguments);
    }

    public static Call of(final Function function, final int lengthInMeasures) {
        return new Call(function, lengthInMeasures);
    }

    public Function getFunction() {
        return function;
    }

    public Result execute(final Context context) {
        try {
            return new Result(function.execute(context, arguments));
        } catch (final Throwable t) {
            return new Result(t);
        }
    }

    public Map<String, Object> getArguments() {
        return Collections.unmodifiableMap(arguments);
    }
}
