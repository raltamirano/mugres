package mugres.core.live.processors.drummer.commands;

import mugres.core.common.Context;
import mugres.core.live.processors.drummer.Drummer;

import java.util.Map;

public class Stop implements Command {
    private Stop() {}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute(final Context context,
                        final Drummer drummer,
                        final Map<String, Object> parameters) {
        drummer.stop();
    }

    public static final Stop INSTANCE = new Stop();
    public static final String NAME = "Stop";
}
