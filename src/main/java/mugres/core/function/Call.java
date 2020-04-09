package mugres.core.function;

import mugres.core.common.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Function call */
public class Call {
    private final Function function;
    private final Map<String, Object> arguments = new HashMap<>();

    public Call(final Function function, final Map<String, Object> arguments) {
        this.function = function;
        this.arguments.putAll(arguments);
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
