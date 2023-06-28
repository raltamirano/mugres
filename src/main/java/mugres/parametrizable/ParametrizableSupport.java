package mugres.parametrizable;

import mugres.common.DataType;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static mugres.utils.Reflections.getMethodFor;

public final class ParametrizableSupport implements Parametrizable {
    private final Set<Parameter> parameters;
    private final Map<String, Object> values;
    private final Object target;
    private final Parametrizable parentParameterValuesSource;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private Function<String, Boolean> customHasParameterValueLogic;

    private ParametrizableSupport(final Set<Parameter> parameters,
                                  final Map<String, Object> values,
                                  final Parametrizable parentParameterValuesSource) {
        if (parameters == null || parameters.isEmpty())
            throw new IllegalArgumentException("parameters");

        this.parameters = parameters;

        this.values = new ConcurrentHashMap<>();
        if (values != null && !values.isEmpty())
            this.values.putAll(values);

        this.target = null;
        this.parentParameterValuesSource = parentParameterValuesSource;
        setUpParentParameterValuesSource();
    }

    private ParametrizableSupport(final Set<Parameter> parameters,
                                  final Object target,
                                  final Parametrizable parentParameterValuesSource) {
        if (parameters == null)
            throw new IllegalArgumentException("parameters");
        if (target == null)
            throw new IllegalArgumentException("target");


        this.parameters = parameters;
        this.values = null;
        this.target = target;
        this.parentParameterValuesSource = parentParameterValuesSource;
        setUpParentParameterValuesSource();
    }

    public static ParametrizableSupport of(final Set<Parameter> parameters) {
        return new ParametrizableSupport(parameters, null, null);
    }

    public static ParametrizableSupport of(final Set<Parameter> parameters,
                                           final Parametrizable parentParameterValuesSource) {
        if (parentParameterValuesSource == null)
            throw new IllegalArgumentException("parentParameterValuesSource");

        return new ParametrizableSupport(parameters, null, parentParameterValuesSource);
    }

    public static ParametrizableSupport of(final Set<Parameter> parameters,
                                           final Map<String, Object> values) {
        return new ParametrizableSupport(parameters, values, null);
    }

    public static ParametrizableSupport of(final Set<Parameter> parameters,
                                           final Map<String, Object> values,
                                           final Parametrizable parentParameterValuesSource) {
        if (parentParameterValuesSource == null)
            throw new IllegalArgumentException("parentParameterValuesSource");

        return new ParametrizableSupport(parameters, values, parentParameterValuesSource);
    }

    public static ParametrizableSupport forTarget(final Set<Parameter> parameters,
                                           final Object target) {
        return new ParametrizableSupport(parameters, target, null);
    }

    public static ParametrizableSupport forTarget(final Set<Parameter> parameters,
                                           final Object target,
                                           final Parametrizable parentParameterValuesSource) {
        if (parentParameterValuesSource == null)
            throw new IllegalArgumentException("parentParameterValuesSource");

        return new ParametrizableSupport(parameters, target, parentParameterValuesSource);
    }

    public Object target() {
        return target;
    }

    public Parametrizable parentParameterValuesSource() {
        return parentParameterValuesSource;
    }

    @Override
    public Set<Parameter> parameters() {
        return Collections.unmodifiableSet(parameters);
    }

    @Override
    public Parameter parameter(final String name) {
        return parameters.stream().filter(p -> p.name().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void parameterValue(final String name, final Object value) {
        final Object oldValue;
        if (target != null) {
            final DataType dataType = parameter(name).dataType();
            oldValue = dataType.get(target, name);
            dataType.set(target, name, value);
        } else {
            oldValue = values.get(name);
            values.put(name, value);
        }
        propertyChangeSupport.firePropertyChange(name, oldValue, ChangedValue.of(value, false));
    }

    @Override
    public Object parameterValue(final String name) {
        if (target != null)
            return parameter(name).dataType().get(target, name);
        else
            return values.get(name);
    }

    @Override
    public boolean overrides(final String name) {
        if (customHasParameterValueLogic != null) {
            final Boolean result = customHasParameterValueLogic.apply(name);
            if (result != null)
                return result;
        }

        if (target != null)
            try {
                return getMethodFor(target.getClass(), name).invoke(target) != null;
            } catch (final Exception ignore) {
                return false;
            }
        else
            return values.containsKey(name);
    }

    @Override
    public void undoOverride(final String name) {
        if (overrides(name)) {
            final Object oldValue;
            if (target != null) {
                final DataType dataType = parameter(name).dataType();
                oldValue = dataType.get(target, name);
                dataType.clear(target, name);
            } else {
                oldValue = values.remove(name);
            }
            propertyChangeSupport.firePropertyChange(name, oldValue, ChangedValue.of(parameterValue(name), true));
        }
    }

    @Override
    public boolean hasParentParameterValueSource() {
        return parentParameterValuesSource != null;
    }

    @Override
    public Map<String, Object> parameterValues() {
        return Collections.unmodifiableMap(values);
    }

    @Override
    public void addParameterValueChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removeParameterValueChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void setUpParentParameterValuesSource() {
        if (parentParameterValuesSource == null)
            return;

        parentParameterValuesSource.addParameterValueChangeListener(e -> {
            if (!overrides(e.getPropertyName()))
                propertyChangeSupport.firePropertyChange(e.getPropertyName(), e.getOldValue(),
                        ChangedValue.of(e.getNewValue(), true));
        });
    }

    public void setCustomHasParameterValueLogic(final Function<String, Boolean> customHasParameterValueLogic) {
        this.customHasParameterValueLogic = customHasParameterValueLogic;
    }

    public static class ChangedValue {
        private final Object value;
        private final boolean fromParent;

        private ChangedValue(final Object value, final boolean fromParent) {
            this.value = unwrap(value);
            this.fromParent = fromParent;
        }

        public static ChangedValue of(final Object value, final boolean fromParent) {
            return new ChangedValue(value, fromParent);
        }

        public static Object unwrap(final Object value) {
            return value instanceof ChangedValue ? ((ChangedValue)value).value() : value;
        }

        public Object value() {
            return value;
        }

        public boolean fromParent() {
            return fromParent;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (o instanceof ChangedValue) {
                final ChangedValue that = (ChangedValue) o;
                return Objects.equals(value, that.value);
            } else {
                return Objects.equals(value, o);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "ChangedValue{" +
                    "value=" + value +
                    ", fromParent=" + fromParent +
                    '}';
        }
    }
}
