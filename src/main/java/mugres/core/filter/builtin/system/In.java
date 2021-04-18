package mugres.core.filter.builtin.system;

import mugres.core.common.Context;
import mugres.core.common.Signals;
import mugres.core.filter.Filter;

import java.util.Map;

public final class In extends Filter {
    public In() {
        super("In");
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
