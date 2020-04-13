package mugres.core.function;

import mugres.core.common.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static Call parse(final String input) {
        return parse(input, Collections.emptyMap());
    }

    public static Call parse(final String input, final Map<String, String> argumentsMap) {
        if (input == null || input.trim().isEmpty())
            throw new IllegalArgumentException("input");

        if (!Pattern.matches("^" + FUNCTION_CALL.pattern() + "$", input))
            throw new IllegalArgumentException("Invalid function call format: " + input);

        final Matcher functionCallMatcher = FUNCTION_CALL.matcher(input);
        functionCallMatcher.matches();

        // Get function
        final String functionName = functionCallMatcher.group(1);
        final Function function = getFunction(functionName);

        // Get arguments
        final String functionArguments = functionCallMatcher.group(2).trim();
        final Matcher argumentsMatcher = NAMED_ARGS_LIST.matcher(functionArguments);

        final Map<String, String> allArguments = new HashMap<>(argumentsMap);
        // Override any arguments provided with the arguments map with those
        // provided inline in the call specification itself.
        while(argumentsMatcher.find()) {
            final String parameterName = argumentsMatcher.group(1);
            final String argumentString = argumentsMatcher.group(2).trim();
            allArguments.put(parameterName, argumentString);
        }

        final Map<String, Object> arguments = new HashMap<>();
        for(Map.Entry<String, String> entry : allArguments.entrySet()) {
            final String parameterName = entry.getKey();
            final String argumentString = entry.getValue().trim();

            final Function.Parameter parameter = function.getParameter(parameterName);
            if (parameter == null)
                throw new RuntimeException(String.format("Unexpected parameter '%s' for function '%s'",
                        parameterName, functionName));

            Object argument = null;
            if (argumentString != null && !argumentString.isEmpty()) {
                switch(parameter.getDataType()) {
                    case LENGTH:
                        argument = Length.of(Integer.parseInt(argumentString));
                        break;
                    case VALUE:
                        argument = Value.valueOf(argumentString);
                        break;
                    case NOTE:
                        argument = Note.of(argumentString);
                        break;
                    case PITCH:
                        argument = Pitch.of(Integer.parseInt(argumentString));
                        break;
                    case TEXT:
                        if (!argumentString.startsWith("'") || !argumentString.endsWith("'"))
                            throw new IllegalArgumentException("TEXT function parameter's values must be " +
                                    "enclosed in single quotes (')");
                        argument = argumentString.substring(1, argumentString.length() - 1);
                        break;
                    case INTEGER:
                        argument = Integer.parseInt(argumentString);
                        break;
                }
            }

            if (argument != null)
                arguments.put(parameterName, argument);
        }

        if (arguments.isEmpty() && !functionArguments.isEmpty())
            throw new IllegalArgumentException("Invalid function call arguments format: " + functionArguments);

        return of(function, arguments);
    }

    private static Function getFunction(final String functionName) {
        try {
            return Function.WellKnownFunctions.forName(functionName).getFunction();
        } catch (final Throwable ignore) {
            throw new RuntimeException("Unknown function: " + functionName);
        }
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

    public int getLengthInMeasures() {
        return (int)arguments.get(LENGTH_PARAMETER.getName());
    }

    private static final Pattern FUNCTION_CALL = Pattern.compile("([a-z][0-9a-zA-Z_-]+[0-9a-zA-Z])\\((.*)\\)");
    private static final Pattern NAMED_ARGS_LIST = Pattern.compile("([a-z][0-9a-zA-Z_-]*[0-9a-zA-Z])\\=(\\'(?:[\\[\\]\\{\\}\\|\\s0-9a-zA-Z_-]+)\\'|(?:\\-?\\d+(?:\\.\\d+)?)|true|false|yes|no|y|n|(?:[0-9a-zA-Z_-]+))");
}
