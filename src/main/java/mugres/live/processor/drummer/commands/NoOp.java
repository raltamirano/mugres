package mugres.live.processor.drummer.commands;

import mugres.common.Context;
import mugres.live.processor.drummer.Drummer;

import java.util.Map;

public class NoOp implements Command {
    private NoOp() {}

    @Override
    public String name() {
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
