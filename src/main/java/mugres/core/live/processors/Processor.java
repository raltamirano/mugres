package mugres.core.live.processors;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.io.Output;
import mugres.core.common.io.Input;
import mugres.core.live.signaler.Signaler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Live events/signals processor */
public abstract class Processor<S> {
    private final Input input;
    private final Output output;
    private final List<StatusListener> statusListeners = new ArrayList<>();
    private final Context context;
    private final List<Signaler> signalers;

    protected Processor(final Context context,
                        final Input input,
                        final Output output,
                        final List<Signaler> signalers) {
        this.context = context;
        this.input = input;
        this.output = output;
        this.signalers = signalers;

        input.addListener(this::process);
    }

    public void process(final Signal signal) {
        try {
            doProcess(signal);
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    public Context getContext() {
        return context;
    }

    protected Input getInput() {
        return input;
    }

    protected Output getOutput() {
        return output;
    }

    protected List<Signaler> getSignalers() {
        return Collections.unmodifiableList(signalers);
    }

    public void start() {
        onStart();
        if (signalers != null) signalers.forEach(signaler -> signaler.start(context, input));
    }

    public void stop() {
        if (signalers != null) signalers.forEach(signaler -> signaler.stop(context));
        onStop();
    }

    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract void doProcess(final Signal signal);

    public void addStatusListener(final StatusListener listener) {
        statusListeners.add(listener);
    }

    protected void reportStatus(final String text, final S data) {
        statusListeners.forEach(l -> l.report(Status.of(text, data)));
    }

    public interface StatusListener {
        void report(final Status status);
    }
}
