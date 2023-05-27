package mugres.live.processor.drummer.commands;

import mugres.common.Context;
import mugres.live.processor.drummer.Drummer;

import java.util.Map;

public class Wait implements Command {
    private Wait() {}

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void execute(final Context context,
                        final Drummer drummer,
                        final Map<String, Object> parameters) {
        final Long millis = (Long) parameters.get("millis");
        try {
            Thread.sleep(millis);
        } catch (final Throwable ignore) {
        }
    }

    public static final Wait INSTANCE = new Wait();
    public static final String NAME = "Wait";

    public static final long HALF_SECOND      =    500L;
    public static final long ONE_SECOND       =  1_000L;
    public static final long FIVE_SECONDS     =  5_000L;
    public static final long ONE_MINUTE       = 60_000L;
}
