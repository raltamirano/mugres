package mugres.core.filter.builtin.system;

import mugres.core.common.Context;
import mugres.core.common.Signals;
import mugres.core.filter.Filter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Monitor extends Filter {
    public static final String NAME = "Monitor";

    public Monitor(final Map<String, Object> arguments) {
        super(arguments);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals) {
        final boolean onlyActives = getOnlyActives(arguments);

        (onlyActives ? signals.actives().signals() : signals.signals()).forEach(e -> System.out.println(String.format("%s %s %s",
                TIME_FORMAT.format(new Date()), getLabel(arguments), e)));
        return signals;
    }

    private boolean getOnlyActives(final Map<String, Object> arguments) {
        return arguments.containsKey("onlyActives") ?
                Boolean.parseBoolean(arguments.get("onlyActives").toString()) : false;
    }

    private String getLabel(final Map<String, Object> arguments) {
        try {
            return String.format(LABEL_FORMAT, arguments.get("label").toString());
        } catch (final Throwable ignore) {
            return String.format(LABEL_FORMAT, "");
        }
    }

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss.SSS");
    private static final String LABEL_FORMAT = "[%-10s]";
}
