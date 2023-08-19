package mugres.function.builtin.song;

import mugres.common.Context;
import mugres.function.Function;
import mugres.function.builtin.song.parametrized.SongParametrization;
import mugres.parametrizable.Parameter;
import mugres.tracker.Song;
import java.util.Map;


public class ParametrizedSongGenerator extends Function.SongFunction {

    public static final String PARAMETRIZATION = "parametrization";

    public ParametrizedSongGenerator() {
        super("parametrizedSong", "Parametrized Song",
                Parameter.of(PARAMETRIZATION, "Parametrization", 1, "Parametrization",
                        SongParametrization.class));
    }

    @Override
    protected Song doExecute(final Context context, final Map<String, Object> arguments) {
        final SongParametrization parametrization = (SongParametrization) arguments.get(PARAMETRIZATION);
        throw new RuntimeException("Not implemented!");
    }
}
