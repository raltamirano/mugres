package mugres.core.live.processors.drummer.commands;

import mugres.core.common.Context;
import mugres.core.live.processors.drummer.Drummer;

import java.util.Map;

public class NoOp implements Command {
    private NoOp() {}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute(final Context context,
                        final Drummer drummer,
                        final Map<String, Object> parameters) {
        // Do nothing!
    }

    public static final NoOp INSTANCE = new NoOp();
    public static final String NAME = "NoOp";
}
