package mugres.parametrizable;

import mugres.common.DataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Parameter implements Comparable<Parameter> {
    private final String name;
    private final String label;
    private final int order;
    private final String documentation;
    private final DataType dataType;
    private final boolean optional;
    private final Object defaultValue;
    private final boolean multiple;
    private final Object min;
    private final Object max;
    private final Collection<Object> domain;

    private Parameter(final String name, final String label, final int order, final String documentation,
                      final DataType dataType, final boolean optional, final Object defaultValue,
                      final boolean multiple, final Object min, final Object max, final Collection<Object> domain) {
        this.name = name;
        this.label = label;
        this.order = order;
        this.documentation = documentation;
        this.dataType = dataType;
        this.optional = optional;
        this.defaultValue = defaultValue;
        this.multiple = multiple;
        this.min = min;
        this.max = max;
        this.domain = domain != null ? new ArrayList<>(domain) : Collections.emptyList();
    }

    public static Parameter of(final String name, final String label, final int order, final String documentation,
                               final DataType dataType) {
        return new Parameter(name, label, order, documentation, dataType, false, null, false,
                null, null, null);
    }

    public static Parameter of(final String name, final String label, final int order, final String documentation,
                               final DataType dataType, final boolean optional, final Object defaultValue) {
        return new Parameter(name, label, order, documentation, dataType, optional, defaultValue, false,
                null, null, null);
    }

    public static Parameter of(final String name, final String label, final int order, final String documentation,
                               final DataType dataType, final boolean optional, final Object defaultValue,
                               final Object min, final Object max) {
        return new Parameter(name, label, order, documentation, dataType, optional, defaultValue, false,
                min, max, null);
    }

    public static Parameter of(final String name, final String label, final int order, final String documentation,
                               final DataType dataType, final boolean optional, final Object defaultValue,
                               final Collection<Object> domain) {
        return new Parameter(name, label, order, documentation, dataType, optional, defaultValue, false,
                null, null, domain);
    }

    public static Parameter of(final String name, final String label, final int order, final String documentation,
                               final DataType dataType, final boolean optional, final Object defaultValue,
                               final boolean multiple) {
        return new Parameter(name, label, order, documentation, dataType, optional, defaultValue, multiple,
                null, null, null);
    }

    public String name() {
        return name;
    }

    public String label() {
        return label;
    }

    public int order() {
        return order;
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

    public Object min() {
        return min;
    }

    public Object max() {
        return max;
    }

    public Collection<Object> domain() {
        return domain;
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

    @Override
    public int compareTo(final Parameter o) {
        return Integer.compare(this.order, o.order);
    }
}
