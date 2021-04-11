package mugres.core.live.processors.transformer.filters;

import mugres.core.common.Context;
import mugres.core.common.Signals;

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
