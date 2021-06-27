package mugres.core.filter.builtin.misc;

import mugres.core.common.Context;
import mugres.core.common.Signals;
import mugres.core.filter.Filter;

import java.util.Map;

public class Clear extends Filter {
    public static final String NAME = "Clear";

    public Clear(final Map<String, Object> arguments) {
        super(arguments);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals) {
        return Signals.create();
    }
}
