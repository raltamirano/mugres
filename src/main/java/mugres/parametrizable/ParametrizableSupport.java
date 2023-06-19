package mugres.parametrizable;

import mugres.common.DataType;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ParametrizableSupport implements Parametrizable {
    private final Set<Parameter> parameters;
    private final Map<String, Object> values;
    private final Object target;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private ParametrizableSupport(final Set<Parameter> parameters,
                                  final Map<String, Object> values) {
        if (parameters == null || parameters.isEmpty())
            throw new IllegalArgumentException("parameters");

        this.parameters = parameters;

        this.values = new ConcurrentHashMap<>();
        if (values != null && !values.isEmpty())
            this.values.putAll(values);

        this.target = null;
    }

    private ParametrizableSupport(final Set<Parameter> parameters,
                                  final Object target) {
        if (parameters == null || parameters.isEmpty())
            throw new IllegalArgumentException("parameters");
        if (target == null)
            throw new IllegalArgumentException("target");


        this.parameters = parameters;
        this.values = null;
        this.target = target;
    }

    public static ParametrizableSupport of(final Set<Parameter> parameters) {
        return new ParametrizableSupport(parameters, null);
    }

    public static ParametrizableSupport of(final Set<Parameter> parameters,
                                           final Map<String, Object> values) {
        return new ParametrizableSupport(parameters, values);
    }

    public static ParametrizableSupport of(final Set<Parameter> parameters,
                                           final Object target) {
        return new ParametrizableSupport(parameters, target);
    }

    public Object target() {
        return target;
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
}
