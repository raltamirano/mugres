package mugres.filter.builtin.system;

import mugres.common.Context;
import mugres.live.Signals;
import mugres.common.io.Output;
import mugres.filter.Filter;

import static java.util.Collections.emptyMap;

public final class Out extends Filter {
    private static final String NAME = "Out";
    private final Context context;
    private final Output output;

    public Out(final Context context, final Output output) {
        super(emptyMap());

        this.context = context;
        this.output = output;
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
        output.send(signals);
        return Signals.create();
    }
}
