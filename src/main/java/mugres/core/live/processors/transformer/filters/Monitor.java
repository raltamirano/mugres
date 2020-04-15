package mugres.core.live.processors.transformer.filters;

import mugres.core.common.Context;
import mugres.core.common.Signals;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Monitor extends Filter {
    private final String label;

    private Monitor(final String label) {
        this.label = label;
    }

    public static Monitor withLabel(final String label) {
        return new Monitor(label);
    }

    @Override
    protected boolean canHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals handle(final Context context, final Signals signals) {
        signals.signals().forEach(e -> System.out.println(String.format("%s [%-10s]%s",
                TIME_FORMAT.format(new Date()), label, e)));
        return signals;
    }

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss.SSS");
}
