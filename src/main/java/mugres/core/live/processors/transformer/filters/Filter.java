package mugres.core.live.processors.transformer.filters;

import mugres.core.common.Context;
import mugres.core.common.Signals;

public abstract class Filter {
    private Filter next;

    Filter() {
        this(null);
    }

    Filter(final Filter next) {
        this.next = next;
    }

    protected abstract boolean canHandle(final Context context, final Signals signals);

    protected abstract Signals handle(final Context context, final Signals signals);

    public final void accept(final Context context, final Signals signals) {
        Signals output = doAccept(context, signals);

        Filter nextFilter = next;
        while (nextFilter != null) {
            output = nextFilter.doAccept(context, output);
            nextFilter = nextFilter.next;
        }
    }

    public void setNext(final Filter filter) {
        this.next = filter;
    }

    private Signals doAccept(final Context context, final Signals signals) {
        return canHandle(context, signals) ?
                handle(context, signals) : signals;
    }
}
