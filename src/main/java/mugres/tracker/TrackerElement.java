package mugres.tracker;


import mugres.common.Context;
import mugres.common.Key;
import mugres.common.TimeSignature;
import mugres.parametrizable.Parameter;
import mugres.parametrizable.Parametrizable;
import mugres.parametrizable.ParametrizableSupport;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Base class for all base classes for the tracker world.
 */
public abstract class TrackerElement implements Parametrizable, Comparable<TrackerElement> {
    private final UUID id;
    private String name;
    private final Context context;
    private ParametrizableSupport parametrizableSupport;
    private final PropertyChangeSupport propertyChangeSupport;

    protected TrackerElement(final UUID id, final String name, final Context context) {
        if (id == null)
            throw new IllegalArgumentException("id");
        validateName(name);

        this.id = id;
        this.name = name;
        this.context = context;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void name(final String name) {
        validateName(name);

        final String oldValue = this.name;
        this.name = name;
        propertyChangeSupport().firePropertyChange("name", oldValue, name);
    }


    public Context context() {
        return context;
    }

    public int tempo() {
        return context.tempo();
    }

    public void tempo(final int tempo) {
        final int oldValue = tempo();
        context.tempo(tempo);
        propertyChangeSupport().firePropertyChange(Context.TEMPO, oldValue, tempo);
    }

    public Key key() {
        return context.key();
    }

    public void key(final Key key) {
        final Key oldValue = key();
        context.key(key);
        propertyChangeSupport().firePropertyChange(Context.KEY, oldValue, key);
    }

    public TimeSignature timeSignature() {
        return context.timeSignature();
    }

    public void timeSignature(final TimeSignature timeSignature) {
        final TimeSignature oldValue = timeSignature();
        context.timeSignature(timeSignature);
        propertyChangeSupport().firePropertyChange(Context.TIME_SIGNATURE, oldValue, timeSignature);
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public Set<Parameter> parameters() {
        return parametrizableSupport().parameters();
    }

    @Override
    public Parameter parameter(final String name) {
        return parametrizableSupport().parameter(name);
    }

    @Override
    public void parameterValue(final String name, Object value) {
        parametrizableSupport().parameterValue(name, value);
    }

    @Override
    public Object parameterValue(final String name) {
        return parametrizableSupport().parameterValue(name);
    }

    @Override
    public boolean overrides(final String name) {
        return parametrizableSupport().overrides(name);
    }

    @Override
    public void undoOverride(final String name) {
        parametrizableSupport().undoOverride(name);
    }

    @Override
    public boolean hasParentParameterValueSource() {
        return parametrizableSupport().hasParentParameterValueSource();
    }

    @Override
    public Map<String, Object> parameterValues() {
        return parametrizableSupport().parameterValues();
    }

    @Override
    public void addParameterValueChangeListener(final PropertyChangeListener listener) {
        parametrizableSupport().addParameterValueChangeListener(listener);
    }

    @Override
    public void removeParameterValueChangeListener(final PropertyChangeListener listener) {
        parametrizableSupport().removeParameterValueChangeListener(listener);
    }

    protected abstract ParametrizableSupport createParametrizableSupport();

    protected PropertyChangeSupport propertyChangeSupport() {
        return propertyChangeSupport;
    }

    protected synchronized ParametrizableSupport parametrizableSupport() {
        if (parametrizableSupport == null)
            parametrizableSupport = createParametrizableSupport();

        return parametrizableSupport;
    }

    private static void validateName(final String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name");
    }

    @Override
    public int compareTo(final TrackerElement o) {
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackerElement that = (TrackerElement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
