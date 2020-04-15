package mugres.core.live.processors;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.io.Output;
import mugres.core.common.io.Input;

/** Live events/signals processor */
public abstract class Processor {
    private final Context context;

    protected Processor(final Context context,
                        final Input input,
                        final Output output) {
        this.context = context;

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

    protected abstract void doProcess(final Signal signal);
}
