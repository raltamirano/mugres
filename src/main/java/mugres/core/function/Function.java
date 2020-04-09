package mugres.core.function;

import mugres.core.common.*;

import java.util.*;

/** Function that generates musical artifacts */
public abstract class Function {
    private final String name;
    private final Set<Parameter> parameters = new HashSet<>();

    public Function(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Parameter> getParameters() {
        return Collections.unmodifiableSet(parameters);
    }

    public Function addParameter(final Parameter parameter) {
        if (parameters.contains(parameter.name))
            throw new IllegalArgumentException(String.format("Parameter '%s' already exists!", parameter.name));

        parameters.add(parameter);
        return this;
    }

    public abstract List<Event> execute(final Context context, final Map<String, Object> arguments);

    public static class Parameter {
        private final String name;
        private final String documentation;
        private final DataType dataType;
        private final boolean optional;
        private final Object defaultValue;

        public Parameter(String name, String documentation, DataType dataType, boolean optional, Object defaultValue) {
            this.name = name;
            this.documentation = documentation;
            this.dataType = dataType;
            this.optional = optional;
            this.defaultValue = defaultValue;
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
