package mugres.core.function;

import mugres.core.common.*;
import mugres.core.function.builtin.drums.DrumPattern;
import mugres.core.function.builtin.dyadriffer.DyadRiffer;
import mugres.core.function.builtin.random.Random;

import java.util.*;

/** Function that generates musical artifacts. */
public abstract class Function {
    private final String name;
    private final String description;
    private final Set<Parameter> parameters = new HashSet<>();

    public Function(final String name,
                    final String description,
                    final Parameter... parameters) {
        this.name = name;
        this.description = description;
        for(Parameter parameter : parameters)
            addParameter(parameter);
        // Every function must specify these mandatory parameters
        addParameter(LENGTH_PARAMETER);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Parameter> getParameters() {
        return Collections.unmodifiableSet(parameters);
    }

    public Parameter getParameter(final String name) {
        for(Parameter parameter : parameters)
            if (parameter.name.equals(name))
                return parameter;
        return null;
    }

    private void addParameter(final Parameter parameter) {
        if (parameters.contains(parameter.name))
            throw new IllegalArgumentException(String.format("Parameter '%s' already exists!", parameter.name));

        parameters.add(parameter);
    }

    public List<Event> execute(final Context context, final Map<String, Object> arguments) {
        final List<Event> events = doExecute(context, prepareArguments(arguments));
        // TODO: Validate length/complete to lenght with rests / etc.
        return events;
    }

    protected abstract List<Event> doExecute(final Context context, final Map<String, Object> arguments);

    protected Length readMeasuresLength(final Context context, final Map<String, Object> arguments) {
        final int measures = (Integer) arguments.get(LENGTH_PARAMETER.getName());
        final TimeSignature timeSignature = context.getTimeSignature();
        return timeSignature.measuresLength(measures);
    }

    private Map<String, Object> prepareArguments(final Map<String, Object> arguments) {
        final Map<String, Object> preparedArguments = new HashMap<>();

        if (parameters.isEmpty()) {
            if (arguments.isEmpty())
                return arguments;
            else
                throw new IllegalArgumentException(String.format("No arguments expected for function '%s'. " +
                        "Provided: '%s'", name, arguments));
        } else {
            for(Parameter parameter : parameters) {
                Object argument = arguments.get(parameter.getName());
                if (argument == null) {
                    if (parameter.optional)
                        argument = parameter.defaultValue;
                    else
                        throw new IllegalArgumentException(String.format("No value provided for parameter '%s' " +
                                        "while calling function '%s'", parameter.name, name));
                }

                preparedArguments.put(parameter.name, argument);
            }

            for(String argumentName : arguments.keySet())
                if (!parameters.stream().anyMatch(p -> p.name.equals(argumentName)))
                    throw new IllegalArgumentException(String.format("Unexpected argument '%s' " +
                            "while calling function '%s'. Value: '%s'",
                            argumentName, name, arguments.get(argumentName)));
        }

        // Validate common parameters
        final int length = (Integer)preparedArguments.get(LENGTH_PARAMETER.name);
        if (length <= 0)
            throw new IllegalArgumentException(String.format("'%s' parameter must be always > 0",
                    LENGTH_PARAMETER.name));

        return preparedArguments;
    }

    /** Mandatory length parameter every function must have */
    public static final Parameter LENGTH_PARAMETER = new Parameter("len", "Length in measures",
            Parameter.DataType.INTEGER);

    // Builtin functions

    public enum WellKnownFunctions {
        RANDOM(new Random()),
        DRUM_PATTERN(new DrumPattern()),
        DYAD_RIFFER(new DyadRiffer());

        private final Function function;

        WellKnownFunctions(final Function function) {
            this.function = function;
        }

        public static WellKnownFunctions forName(final String name) {
            for(WellKnownFunctions w : values())
                if (w.function.name.equals(name))
                    return w;

            throw new IllegalArgumentException("Unknown function: " + name);
        }

        public Function getFunction() {
            return function;
        }
    }

    public static class Parameter {
        private final String name;
        private final String documentation;
        private final DataType dataType;
        private final boolean optional;
        private final Object defaultValue;

        private Parameter(String name, String documentation, DataType dataType) {
            this(name, documentation, dataType, false, null);
        }

        private Parameter(String name, String documentation, DataType dataType, boolean optional, Object defaultValue) {
            this.name = name;
            this.documentation = documentation;
            this.dataType = dataType;
            this.optional = optional;
            this.defaultValue = defaultValue;
        }

        public static Parameter of(String name, String documentation, DataType dataType) {
            return new Parameter(name, documentation, dataType);
        }

        public static Parameter of(String name, String documentation, DataType dataType, boolean optional, Object defaultValue) {
            return new Parameter(name, documentation, dataType, optional, defaultValue);
        }

        public String getName() {
            return name;
        }

        public String getDocumentation() {
            return documentation;
        }

        public DataType getDataType() {
            return dataType;
        }

        public boolean isOptional() {
            return optional;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Parameter parameter = (Parameter) o;
            return Objects.equals(name, parameter.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        public enum DataType {
            /** {@link Length} */
            LENGTH,
            /** {@link Value} */
            VALUE,
            /** {@link Note} */
            NOTE,
            /** {@link Pitch} */
            PITCH,
            /** Plain text */
            TEXT,
            /** Integer numbers */
            INTEGER
        }
    }
}
