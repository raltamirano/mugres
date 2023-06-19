package mugres.parametrizable;

import mugres.common.DataType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static mugres.utils.Reflections.getMethodFor;

public final class ParametrizableSupport implements Parametrizable {
    private final Set<Parameter> parameters;
    private final Map<String, Object> values;
    private final Object target;
    private final Parametrizable parentParameterValuesSource;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
        if (parameters == null || parameters.isEmpty())
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
        propertyChangeSupport.firePropertyChange(name, oldValue, value);
    }

    @Override
    public Object parameterValue(final String name) {
        if (target != null)
            return parameter(name).dataType().get(target, name);
        else
            return values.get(name);
    }

    @Override
    public boolean hasParameterValue(final String name) {
        if (target != null)
            try {
                if (target instanceof Parametrizable)
                    return ((Parametrizable)target).hasParameterValue(name);
                else
                    return getMethodFor(target.getClass(), name).invoke(target) != null;
            } catch (final Exception ignore) {
                return false;
            }
        else
            return values.containsKey(name);
    }

    @Override
    public Map<String, Object> parameterValues() {
        return Collections.unmodifiableMap(values);
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void setUpParentParameterValuesSource() {
        if (parentParameterValuesSource == null)
            return;

        parentParameterValuesSource.addPropertyChangeListener(e -> {
            if (!hasParameterValue(e.getPropertyName()))
                propertyChangeSupport.firePropertyChange(new FromParentPropertyChangeEvent(this,
                        e.getPropertyName(), e.getOldValue(), e.getNewValue()));
        });
    }

    public static class FromParentPropertyChangeEvent extends PropertyChangeEvent {
        public FromParentPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
            super(source, propertyName, oldValue, newValue);
        }
    }
}
