package mugres.live.processor.drummer.commands;

import mugres.common.Context;
import mugres.live.processor.drummer.Drummer;
import mugres.live.processor.drummer.Drummer.SwitchMode;

import java.util.Map;

public class Play implements Command {
    private Play() {}

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void execute(final Context context,
                        final Drummer drummer,
                        final Map<String, Object> parameters) {
        drummer.play((String)parameters.get("pattern"), (SwitchMode)parameters.get("switchMode"));
    }

    public static final Play INSTANCE = new Play();
    public static final String NAME = "Play";
}
