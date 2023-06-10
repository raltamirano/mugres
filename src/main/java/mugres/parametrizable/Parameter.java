package mugres.parametrizable;

import mugres.common.DataType;

import java.util.Objects;

public class Parameter {
    private final String name;
    private final String documentation;
    private final DataType dataType;
    private final boolean optional;
    private final Object defaultValue;
    private final boolean multiple;

    private Parameter(final String name, final String documentation, final DataType dataType) {
        this(name, documentation, dataType, false, null, false);
    }

    private Parameter(final String name, final String documentation, final DataType dataType,
                      final boolean optional, final Object defaultValue, final boolean multiple) {
        this.name = name;
        this.documentation = documentation;
        this.dataType = dataType;
        this.optional = optional;
        this.defaultValue = defaultValue;
        this.multiple = multiple;
    }

    public static Parameter of(final String name, final String documentation, final DataType dataType) {
        return new Parameter(name, documentation, dataType);
    }

    public static Parameter of(final String name, final String documentation, final DataType dataType,
                               boolean optional, final Object defaultValue) {
        return new Parameter(name, documentation, dataType, optional, defaultValue, false);
    }

    public static Parameter of(final String name, final String documentation, final DataType dataType,
                               final boolean optional, final Object defaultValue, final boolean multiple) {
        return new Parameter(name, documentation, dataType, optional, defaultValue, multiple);
    }

    public String name() {
        return name;
    }

    public String documentation() {
        return documentation;
    }

    public DataType dataType() {
        return dataType;
    }

    public boolean isOptional() {
        return optional;
    }

    public Object defaultValue() {
        return defaultValue;
    }

    public boolean isMultiple() {
        return multiple;
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
}
