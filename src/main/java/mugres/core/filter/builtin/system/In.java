package mugres.core.filter.builtin.system;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.Signals;
import mugres.core.common.io.Input;
import mugres.core.filter.Filter;

import java.util.Map;

public final class In extends Filter {
    private final Context context;
    private final Input input;

    public In(final Context context, final Input input) {
        super("In");

        this.context = context;
        this.input = input;
    }

    public Input getInput() {
        return input;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        updateSignalsState(signals);
        return signals;
    }

    private void updateSignalsState(final Signals signals) {
        signals.actives().forEach(Filter::activateSignal);
        signals.inactives().forEach(Filter::deactivateSignal);
    }
}
