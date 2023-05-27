package mugres.controllable;

import mugres.common.ControlChange;

import java.util.Map;
import java.util.Set;

/**
 * Contract for elements that can be controlled by using {@link mugres.common.ControlChange control changes}.
 */
public interface Controllable {
    void mapParameterToControlChange(final String parameter, final int controlChange);

    void unmapParameterFromControlChange(final String parameter, final int controlChange);

    void clearAllControlChangeMappings();

    Map<Integer, Set<String>> controlChangeMappings();

    /**
     * Accepts a Control Change.
     */
    void onControlChange(final ControlChange controlChange);
}
