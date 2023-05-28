package mugres.common.io;

import mugres.common.ControlChange;
import mugres.common.InstrumentChange;
import mugres.filter.Filter;
import mugres.live.Signal;

import java.util.List;

public class ProxyInput extends Input {
    private final Input delegate;

    private ProxyInput(final Input delegate) {
        if (delegate == null)
            throw new IllegalArgumentException("delegate");

        this.delegate = delegate;
        this.delegate.addListener(createProxyListener());
    }

    public static ProxyInput of(final Input delegate) {
        return of(delegate, null);
    }

    public static ProxyInput of(final Input delegate, final List<Filter> filters) {
        final ProxyInput proxyInput = new ProxyInput(delegate);
        if (filters != null)
            filters.forEach(f -> proxyInput.addFilter(f));
        return proxyInput;
    }

    @Override
    public void receive(final Signal signal) {
        listeners().forEach(l -> l.receive(signal));
    }

    @Override
    public void receive(final InstrumentChange instrumentChange) {
        listeners().forEach(l -> l.receive(instrumentChange));
    }

    @Override
    public void receive(final ControlChange controlChange) {
        listeners().forEach(l -> l.receive(controlChange));
    }

    private Listener createProxyListener() {
        return new Listener() {
            @Override
            public void receive(final Signal signal) {
                if (filters().isEmpty()) {
                    ProxyInput.this.receive(signal);
                } else {
                    for(Signal s : IOHelper.applyFilters(signal, filters()).signals())
                        ProxyInput.this.receive(s);
                }
            }

            @Override
            public void receive(final InstrumentChange instrumentChange) {
                ProxyInput.this.receive(instrumentChange);
            }

            @Override
            public void receive(final ControlChange controlChange) {
                ProxyInput.this.receive(controlChange);
            }
        };
    }
}
