package mugres.live.processor.transformer;

import mugres.common.Context;
import mugres.live.Signal;
import mugres.live.Signals;
import mugres.common.io.Input;
import mugres.common.io.Output;
import mugres.filter.Filter;
import mugres.filter.builtin.system.In;
import mugres.filter.builtin.system.Out;
import mugres.live.processor.Processor;
import mugres.live.processor.transformer.config.Configuration;

import java.util.Collections;

public class Transformer extends Processor {
    private final Configuration config;
    private final In in;
    private final Out out;

    public Transformer(final Context context,
                       final Input input,
                       final Output output,
                       final Configuration config) {
        super(context, input, output, config.signalers(), Collections.emptySet());

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
