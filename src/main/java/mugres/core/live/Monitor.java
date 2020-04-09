package mugres.core.live;

import mugres.core.common.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Monitor extends AbstractFilter {
    private final String label;

    public Monitor(final String label) {
        this.label = label;
    }

    @Override
    protected boolean canHandle(final Context context, final Events events) {
        return true;
    }

    @Override
    protected Events handle(final Context context, final Events events) {
        events.forEach(e -> System.out.println(String.format("%s [%-10s]%s",
                TIME_FORMAT.format(new Date()), label, e)));
        return events;
    }

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss.SSS");
}
