package mugres.filter.builtin.system;

import mugres.common.Context;
import mugres.common.Signals;
import mugres.common.io.Input;
import mugres.filter.Filter;

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
        return signals;
    }
}
