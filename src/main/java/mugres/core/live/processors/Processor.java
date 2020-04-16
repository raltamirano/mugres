package mugres.core.live.processors;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.io.Output;
import mugres.core.common.io.Input;

import java.util.ArrayList;
import java.util.List;

/** Live events/signals processor */
public abstract class Processor {
    private final Input input;
    private final Output output;
    private final List<StatusListener> statusListeners = new ArrayList<>();
    private final Context context;

    protected Processor(final Context context,
                        final Input input,
                        final Output output) {
        this.context = context;
        this.input = input;
        this.output = output;

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

    protected abstract void doProcess(final Signal signal);

    public void addStatusListener(final StatusListener listener) {
        statusListeners.add(listener);
    }

    protected void reportStatus(final String status) {
        statusListeners.forEach(l -> l.report(status));
    }

    public interface StatusListener {
        void report(final String status);
    }
}
