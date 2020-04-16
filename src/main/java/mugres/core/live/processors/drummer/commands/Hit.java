package mugres.core.live.processors.drummer.commands;

import mugres.core.common.Context;
import mugres.core.common.DrumKit;
import mugres.core.live.processors.drummer.Drummer;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class Hit implements Command {
    private Hit() {}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute(final Context context,
                        final Drummer drummer,
                        final Map<String, Object> parameters) {
        final int velocity = (int) parameters.get("velocity");
        final List<DrumKit> pieces = (List<DrumKit>)parameters.get("options");
        if (!pieces.isEmpty())
            drummer.hit(pieces.get(RND.nextInt(pieces.size())), velocity);
    }

    private static final Random RND = new Random();
    public static final Hit INSTANCE = new Hit();
    public static final String NAME = "Hit";
}
