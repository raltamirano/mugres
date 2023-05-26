package mugres.core.live.processor.transformer;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.Signals;
import mugres.core.common.io.Input;
import mugres.core.common.io.Output;
import mugres.core.filter.Filter;
import mugres.core.filter.builtin.system.In;
import mugres.core.filter.builtin.system.Out;
import mugres.core.live.processor.Processor;
import mugres.core.live.processor.transformer.config.Configuration;

public class Transformer extends Processor {
    private final Configuration config;
    private final In in;
    private final Out out;

    public Transformer(final Context context,
                       final Input input,
                       final Output output,
                       final Configuration config) {
        super(context, input, output, config.signalers(), null);

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
        signals = in.accept(context(), signals);

        // Pass through every user-defined filter
        for(final Filter filter : config.filters())
            signals = filter.accept(context(), signals);

        // Pass through output filter
        out.accept(context(), signals);
    }
}
