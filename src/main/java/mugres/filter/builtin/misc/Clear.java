package mugres.filter.builtin.misc;

import mugres.common.Context;
import mugres.common.Signals;
import mugres.filter.Filter;

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
