package mugres.filter.builtin.system;

import mugres.common.Context;
import mugres.live.Signals;
import mugres.filter.Filter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Monitor extends Filter {
    public static final String NAME = "Monitor";

    public Monitor(final Map<String, Object> arguments) {
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
        final boolean onlyNoteOns = getOnlyNoteOns(arguments);

        (onlyNoteOns ? signals.noteOns().signals() : signals.signals()).forEach(e -> System.out.println(String.format("%s %s %s",
                TIME_FORMAT.format(new Date()), getLabel(arguments), e)));
        return signals;
    }

    private boolean getOnlyNoteOns(final Map<String, Object> arguments) {
        return arguments.containsKey("onlyNoteOns") ?
                Boolean.parseBoolean(arguments.get("onlyNoteOns").toString()) : false;
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
