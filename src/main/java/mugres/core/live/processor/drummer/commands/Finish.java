package mugres.core.live.processor.drummer.commands;

import mugres.core.common.Context;
import mugres.core.live.processor.drummer.Drummer;

import java.util.Map;

public class Finish implements Command {
    private Finish() {}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute(final Context context,
                        final Drummer drummer,
                        final Map<String, Object> parameters) {
        drummer.finish();
    }

    public static final Finish INSTANCE = new Finish();
    public static final String NAME = "Finish";
}
