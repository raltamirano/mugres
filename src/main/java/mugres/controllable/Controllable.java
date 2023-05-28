package mugres.controllable;

import mugres.common.ControlChange;
import mugres.controllable.mappings.ControlChangeToAction;
import mugres.controllable.mappings.ControlChangeToParameter;
import mugres.controllable.mappings.Mapping;
import mugres.live.Signal;

import java.util.Set;

/**
 * Contract for elements that can be controlled by using either
 * {@link mugres.live.Signal signals} or
 * {@link mugres.common.ControlChange control changes}.
 */
public interface Controllable {
    /**
     * Returns the set of {@link Control controls} this {@link Controllable controllable} has.
     */
    Set<Control> controls();

    Set<Mapping> mappings();

    void map(final Control control, final ControlChangeToParameter mapping);

    void map(final Control control, final ControlChangeToAction mapping);

    void clearMappings();

    /**
     * Accept a Signal.
     */
    void onSignal(final Signal signal);

    /**
     * Accept a Control Change.
     */
    void onControlChange(final ControlChange controlChange);
}
