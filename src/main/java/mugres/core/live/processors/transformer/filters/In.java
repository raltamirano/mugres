package mugres.core.live.processors.transformer.filters;

import mugres.core.common.Context;
import mugres.core.common.Signals;

public final class In extends Filter {
    @Override
    protected boolean canHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals handle(final Context context, final Signals signals) {
        return signals;
    }
}
