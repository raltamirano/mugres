package mugres.function.builtin.misc;

import mugres.common.Context;
import mugres.function.Function;
import mugres.tracker.Event;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Silence extends Function.EventsFunction {
    public Silence() {
        super("silence", "Silence");
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        return Collections.emptyList();
    }
}
