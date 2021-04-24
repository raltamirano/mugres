package mugres.core.live.processors.transformer;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.Signals;
import mugres.core.common.io.Input;
import mugres.core.common.io.Output;
import mugres.core.live.processors.Processor;
import mugres.core.live.processors.transformer.config.Configuration;
import mugres.core.filter.builtin.system.In;
import mugres.core.filter.builtin.system.Out;
import mugres.core.live.signaler.Signaler;

public class Transformer extends Processor {
    private final Configuration config;
    private final In in;
    private final Out out;

    public Transformer(final Context context,
                       final Input input,
                       final Output output,
                       final Configuration config) {
        super(context, input, output, config.getSignalers());

        this.config = config;

        this.in = new In(context, input);
        this.out = new Out(context, output);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void doProcess(final Signal signal) {
        Signals signals = Signals.of(signal);

        // Pass through input filter
        signals = in.accept(getContext(), signals);

        // Pass through every user-defined filter
        for(Configuration.FilterEntry entry : config.getFilters())
            signals = entry.getFilter().accept(getContext(), signals, entry.getArgs());

        // Pass through output filter
        out.accept(getContext(), signals);
    }
}
