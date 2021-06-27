package mugres.core.live.processor.drummer.commands;

import mugres.core.common.Context;
import mugres.core.common.DrumKit;
import mugres.core.live.processor.drummer.Drummer;

import java.util.List;
import java.util.Map;

import static mugres.core.utils.Randoms.RND;

public class Hit implements Command {
    private Hit() {}

    @Override
    public String name() {
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

    public static final Hit INSTANCE = new Hit();
    public static final String NAME = "Hit";
}
