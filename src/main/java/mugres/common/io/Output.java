package mugres.common.io;

import mugres.common.ControlChange;
import mugres.common.InstrumentChange;
import mugres.filter.Filter;
import mugres.live.Signal;
import mugres.live.Signals;
import mugres.tracker.Song;

import java.util.ArrayList;
import java.util.List;

public abstract class Output {
    private final List<Filter> filters = new ArrayList<>();

    protected List<Filter> filters() {
        return filters;
    }

    public final void addFilter(final Filter filter) {
        if (filter != null)
            filters.add(filter);
    }

    public abstract void send(final Signal signal);

    public void send(final Signals signals) {
        for(Signal s : signals.signals())
            send(s);
    }
    public abstract void send(final InstrumentChange instrumentChange);
    public abstract void send(final ControlChange controlChange);
    public abstract void send(final Song song);
}
