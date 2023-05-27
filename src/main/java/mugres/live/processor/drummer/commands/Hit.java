package mugres.live.processor.drummer.commands;

import mugres.common.Context;
import mugres.common.DrumKit;
import mugres.live.processor.drummer.Drummer;

import java.util.List;
import java.util.Map;

import static mugres.utils.Randoms.RND;

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
