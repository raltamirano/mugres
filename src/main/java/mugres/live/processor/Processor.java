package mugres.live.processor;

import mugres.common.Context;
import mugres.common.ControlChange;
import mugres.common.InstrumentChange;
import mugres.live.Signal;
import mugres.common.io.Output;
import mugres.common.io.Input;
import mugres.controllable.Controllable;
import mugres.live.signaler.Signaler;
import mugres.parametrizable.Parameter;
import mugres.parametrizable.Parametrizable;
import mugres.parametrizable.ParametrizableSupport;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Live events/signals processor */
public abstract class Processor implements Parametrizable, Controllable {
    private final Input input;
    private final Output output;
    private final List<StatusListener> statusListeners = new ArrayList<>();
    private final Context context;
    private final List<Signaler> signalers;
    private Input.Listener inputListener;
    private final ParametrizableSupport parametrizableSupport;
    private final Map<Integer, Set<String>> controlChangeMappings = new ConcurrentHashMap<>();

    protected Processor(final Context context,
                        final Input input,
                        final Output output,
                        final List<Signaler> signalers,
                        final Set<Parameter> parameters) {
        this.context = context;
        this.input = input;
        this.output = output;
        this.signalers = signalers;
        this.parametrizableSupport = ParametrizableSupport.of(parameters);
        this.parametrizableSupport.setCustomHasParameterValueLogic(p ->
                Context.MAIN_PROPERTIES.contains(p) ? context.overrides(p) : null);
    }

    public Context context() {
        return context;
    }

    protected Input input() {
        return input;
    }

    protected Output output() {
        return output;
    }

    protected List<Signaler> signalers() {
        return Collections.unmodifiableList(signalers);
    }

    public void start() {
        onStart();

        inputListener = createInputListener();
        input.addListener(inputListener);

        if (signalers != null) signalers.forEach(signaler -> signaler.start(context, input));
    }

    public void stop() {
        if (signalers != null) signalers.forEach(signaler -> signaler.stop(context));

        input.removeListener(inputListener);

        onStop();
    }

    protected abstract void onStart();

    protected abstract void onStop();

    protected void doProcess(final Signal signal) {}

    protected void doProcess(final InstrumentChange instrumentChange) {}

    protected void doProcess(final ControlChange controlChange) {
        this.onControlChange(controlChange);
    }

    public void addStatusListener(final StatusListener listener) {
        statusListeners.add(listener);
    }

    protected void reportStatus(final String text, final Object data) {
        statusListeners.forEach(l -> l.report(Status.of(text, data)));
    }

    private Input.Listener createInputListener() {
        return new Input.Listener() {
            @Override
            public void receive(final Signal signal) {
                doProcess(signal);
            }

            @Override
            public void receive(final InstrumentChange instrumentChange) {
                doProcess(instrumentChange);
            }

            @Override
            public void receive(final ControlChange controlChange) {
                doProcess(controlChange);
            }
        };
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
            mappedParameterNames.forEach(parameterName -> {
                reportStatus(String.format("Parameter %s => %s", parameterName, controlChange.value()), null);
                parameterValue(parameterName, controlChange.value());
            });
        }
    }

    @Override
    public Set<Parameter> parameters() {
        return parametrizableSupport.parameters();
    }

    @Override
    public Parameter parameter(final String name) {
        return parametrizableSupport.parameter(name);
    }

    @Override
    public void parameterValue(final String name, Object value) {
        parametrizableSupport.parameterValue(name, value);
    }

    @Override
    public Object parameterValue(final String name) {
        return parametrizableSupport.parameterValue(name);
    }

    @Override
    public boolean overrides(final String name) {
        return parametrizableSupport.overrides(name);
    }

    @Override
    public void undoOverride(final String name) {
        parametrizableSupport.undoOverride(name);
    }

    @Override
    public boolean hasParentParameterValueSource() {
        return parametrizableSupport.hasParentParameterValueSource();
    }

    @Override
    public Map<String, Object> parameterValues() {
        return parametrizableSupport.parameterValues();
    }

    @Override
    public void addParameterValueChangeListener(final PropertyChangeListener listener) {
        parametrizableSupport.addParameterValueChangeListener(listener);
    }

    @Override
    public void removeParameterValueChangeListener(final PropertyChangeListener listener) {
        parametrizableSupport.removeParameterValueChangeListener(listener);
    }

    public interface StatusListener {
        void report(final Status status);
    }
}
