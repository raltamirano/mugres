package mugres.core.filter.builtin.system;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.Signals;
import mugres.core.common.io.Input;
import mugres.core.filter.Filter;

import static java.util.Collections.emptyMap;

public final class In extends Filter {
    public static final String NAME = "In";
    private final Context context;
    private final Input input;

    public In(final Context context, final Input input) {
        super(emptyMap());

        this.context = context;
        this.input = input;
    }

    @Override
    public String name() {
        return NAME;
    }

    public Input getInput() {
        return input;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals) {
        updateSignalsState(signals);
        return signals;
    }

    private void updateSignalsState(final Signals signals) {
        for(final Signal signal : signals.signals())
            if (signal.isActive())
                Filter.activateSignal(signal.channel(), signal.played().pitch(), signal.id());
            else
                Filter.deactivateSignal(signal.channel(), signal.played().pitch());
    }
}
