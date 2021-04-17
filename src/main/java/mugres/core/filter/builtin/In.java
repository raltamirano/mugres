package mugres.core.filter.builtin;

import mugres.core.common.Context;
import mugres.core.common.Signals;
import mugres.core.filter.Filter;

import java.util.Map;

public final class In extends Filter {
    public In() {
        super("In");
    }

    @Override
    protected boolean canHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals handle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return signals;
    }
}
