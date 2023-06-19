package mugres.parametrizable;

import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Set;

/**
 * Contract for things that can be parametrized.
 */
public interface Parametrizable {
    /**
     * Available parameters
     */
    Set<Parameter> parameters();

    default Parameter parameter(final String name) {
        for (Parameter parameter : parameters())
            if (parameter.name().equals(name))
                return parameter;
        return null;
    }

    /**
     * Sets the parameter value
     */
    void parameterValue(final String name, final Object value);

    /**
     * Gets the parameter value
     */
    Object parameterValue(final String name);

    /**
     * Tells whether this Parametrizable has value for the specified parameter name within itself or not
     */
    boolean hasParameterValue(final String name);

    /**
     * Gets all parameter values
     */
    Map<String, Object> parameterValues();

    /**
     * Registers a PropertyChangeListener
     */
    void addPropertyChangeListener(final PropertyChangeListener listener);

    /**
     * Unregisters a PropertyChangeListener
     */
    void removePropertyChangeListener(final PropertyChangeListener listener);
}
