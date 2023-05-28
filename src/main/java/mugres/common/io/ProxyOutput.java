package mugres.common.io;

import mugres.common.ControlChange;
import mugres.common.InstrumentChange;
import mugres.filter.Filter;
import mugres.live.Signal;
import mugres.tracker.Song;

import java.util.List;

public class ProxyOutput extends Output {
    private final Output delegate;

    private ProxyOutput(final Output delegate) {
        if (delegate == null)
            throw new IllegalArgumentException("delegate");

        this.delegate = delegate;
    }

    public static ProxyOutput of(final Output delegate) {
        return of(delegate, null);
    }

    public static ProxyOutput of(final Output delegate, final List<Filter> filters) {
        final ProxyOutput proxyOutput = new ProxyOutput(delegate);
        if (filters != null)
            filters.forEach(f -> proxyOutput.addFilter(f));
        return proxyOutput;
    }

    @Override
    public void send(final Signal signal) {
        delegate.send(signal);
    }

    @Override
    public void send(final InstrumentChange instrumentChange) {
        delegate.send(instrumentChange);
    }

    @Override
    public void send(final ControlChange controlChange) {
        delegate.send(controlChange);
    }

    @Override
    public void send(final Song song) {
        delegate.send(song);
    }
}
