package mugres.core.filters;

import mugres.core.common.Context;
import mugres.core.model.Events;

public final class Input extends AbstractFilter {
    public Input() {
        super();
    }

    public Input(final AbstractFilter next) {
        super(next);
    }

    @Override
    protected boolean canHandle(final Context context, final Events events) {
        return true;
    }

    @Override
    protected Events handle(final Context context, final Events events) {
        return events;
    }
}
