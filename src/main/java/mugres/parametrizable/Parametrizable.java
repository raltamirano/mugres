package mugres.parametrizable;

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
     * Gets all parameter values
     */
    Map<String, Object> parameterValues();
}
