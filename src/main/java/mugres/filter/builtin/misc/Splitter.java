package mugres.filter.builtin.misc;

import mugres.common.Context;
import mugres.common.Signal;
import mugres.common.Signals;
import mugres.filter.Filter;

import java.util.Map;

public class Splitter extends Filter {
    public static final String NAME = "Splitter";

    public Splitter(final Map<String, Object> arguments) {
        super(arguments);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals) {
        final Signals result = Signals.create();
        final String tagPrefix = getTagPrefix(arguments);
        final int copies = getCopies(arguments);

        for(final Signal in : signals.signals()) {
            for(int index=1; index<=copies; index++) {
                final String tag = String.format("%s%d", tagPrefix, index);
                final Signal clone = in.clone();
                clone.addTag(tag);
                result.add(clone);
            }
        }

        return result;
    }

    private String getTagPrefix(final Map<String, Object> arguments) {
        return arguments.get("tagPrefix").toString();
    }

    private Integer getCopies(final Map<String, Object> arguments) {
        return Double.valueOf(arguments.get("copies").toString()).intValue();
    }
}
