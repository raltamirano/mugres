package mugres.common.io;

import mugres.common.Context;
import mugres.filter.Filter;
import mugres.live.Signal;
import mugres.live.Signals;

import java.util.List;

class IOHelper {
    private IOHelper() {}

    public static Signals applyFilters(final Signal signal, final List<Filter> filters) {
        return applyFilters(Context.basicContext(), signal, filters);
    }

    public static Signals applyFilters(final Context context, final Signal signal, final List<Filter> filters) {
        Signals signals = Signals.of(signal);

        for(final Filter filter : filters)
            signals = filter.accept(context, signals);

        return signals;
    }
}
