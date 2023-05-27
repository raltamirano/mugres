package mugres.live.processor.drummer.commands;

import mugres.common.Context;
import mugres.live.processor.drummer.Drummer;

import java.util.Map;

public class Finish implements Command {
    private Finish() {}

    @Override
    public String name() {
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
