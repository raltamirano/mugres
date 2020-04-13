package mugres.core.function.builtin.drums;

import mugres.core.common.Context;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.common.gridpattern.GridPattern;
import mugres.core.common.gridpattern.converters.DrumKitHitElementPatternParser;
import mugres.core.function.Function;

import java.util.List;
import java.util.Map;

public class Drums extends Function {
    public Drums() {
        super("drums", "Reproduces a predefined drum pattern",
                Parameter.of("pattern", "The pattern to play", Parameter.DataType.TEXT));
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final String pattern = (String)arguments.get("pattern");

        final GridPattern<DrumKitHitElementPatternParser.DrumKitHit> drumPattern =
                GridPattern.parse(pattern, DrumKitHitElementPatternParser.getInstance(), context);

        if (!length.equals(drumPattern.getLength()))
            throw new RuntimeException("Drum pattern's length does not match function call's length!");

        return Utils.extractEvents(drumPattern);
    }
}
