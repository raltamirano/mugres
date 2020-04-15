package mugres.core.live.processors.transformer;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.Signals;
import mugres.core.common.io.Input;
import mugres.core.common.io.Output;
import mugres.core.live.processors.Processor;
import mugres.core.live.processors.transformer.config.Configuration;
import mugres.core.live.processors.transformer.filters.Filter;
import mugres.core.live.processors.transformer.filters.In;
import mugres.core.live.processors.transformer.filters.Out;

public class Transformer extends Processor {
    private final Configuration configuration;
    private final In input;
    private final Out output;
    private Filter filterChain;

    public Transformer(final Context context,
                       final Input input,
                       final Output output,
                       final Configuration configuration) {
        super(context, input, output);

        this.configuration = configuration;

        this.input = new In();
        this.output = new Out(context, output);

        updateFilterChain();
    }

    @Override
    protected void doProcess(final Signal signal) {
        filterChain.accept(getContext(), Signals.of(signal));
    }

    private void updateFilterChain() {
        filterChain = input;

        Filter last = filterChain;
        for(int index = 0; index < configuration.getFilters().size(); index++) {
            final Filter filter = configuration.getFilters().get(index);
            last.setNext(filter);
            last = filter;
        }

        last.setNext(output);
    }
}
