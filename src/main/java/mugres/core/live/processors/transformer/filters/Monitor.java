package mugres.core.live.processors.transformer.filters;

import mugres.core.common.Context;
import mugres.core.common.Signals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Monitor extends Filter {
    public Monitor() {
        super("Monitor");
    }

    @Override
    protected boolean canHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals handle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        signals.signals().forEach(e -> System.out.println(String.format("%s %s %s",
                TIME_FORMAT.format(new Date()), getLabel(arguments), e)));
        return signals;
    }

    private String getLabel(final Map<String, Object> arguments) {
        try {
            return String.format(arguments.get("label").toString(), "[%s]");
        } catch (final Throwable ignore) {
            return "[]";
        }
    }

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss.SSS");
}
