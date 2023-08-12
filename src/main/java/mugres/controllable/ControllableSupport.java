package mugres.controllable;

import mugres.common.ControlChange;
import mugres.parametrizable.Parametrizable;
import mugres.utils.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ControllableSupport implements Controllable {
    private final Object target;
    private final Map<Integer, Set<String>> controlChangeMappings = new ConcurrentHashMap<>();

    private ControllableSupport(final Object target) {
        if (target == null)
            throw new IllegalArgumentException("target");

        this.target = target;
    }

    public static ControllableSupport of(final Object target) {
        return new ControllableSupport(target);
    }

    @Override
    public void mapParameterToControlChange(final String parameterName, final int controlChange) {
        controlChangeMappings.computeIfAbsent(controlChange, key -> new HashSet()).add(parameterName);
    }

    @Override
    public void unmapParameterFromControlChange(final String parameterName, final int controlChange) {
        controlChangeMappings.computeIfAbsent(controlChange, key -> new HashSet()).remove(parameterName);
    }

    @Override
    public void clearAllControlChangeMappings() {
        controlChangeMappings.clear();
    }

    @Override
    public Map<Integer, Set<String>> controlChangeMappings() {
        return Collections.unmodifiableMap(controlChangeMappings);
    }

    @Override
    public void onControlChange(final ControlChange controlChange) {
        final Set<String> mappedParameterNames = controlChangeMappings.get(controlChange.controller());
        if (mappedParameterNames != null && !mappedParameterNames.isEmpty()) {
            if (target instanceof Parametrizable) {
                final Parametrizable parametrizable = (Parametrizable) target;
                mappedParameterNames.forEach(parameterName -> parametrizable.parameterValue(parameterName, controlChange.value()));
            } else {
                mappedParameterNames.forEach(parameterName -> {
                    try {
                        Reflections.setMethodFor(target.getClass(), parameterName,
                                controlChange.value().getClass()).invoke(target, controlChange.value());
                    } catch (final Exception e) {
                        System.out.println("Error setting controllable value: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
