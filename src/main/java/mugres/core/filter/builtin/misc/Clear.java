package mugres.core.filter.builtin.misc;

import mugres.core.common.Context;
import mugres.core.common.Signals;
import mugres.core.filter.Filter;

import java.util.Map;

public class Clear extends Filter {
    public Clear() {
        super("Clear");
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals internalHandle(Context context, Signals signals, Map<String, Object> arguments) {
        return Signals.create();
    }
}
