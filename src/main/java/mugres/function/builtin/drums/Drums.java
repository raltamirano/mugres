package mugres.function.builtin.drums;

import mugres.common.Context;
import mugres.common.DataType;
import mugres.tracker.Event;
import mugres.common.Length;
import mugres.common.gridpattern.GridPattern;
import mugres.common.gridpattern.converters.DrumKitHitElementPatternParser;
import mugres.function.Function.EventsFunction;
import mugres.parametrizable.Parameter;

import java.util.List;
import java.util.Map;

public class Drums extends EventsFunction {
    public Drums() {
        super("drums", "Reproduces a predefined drum pattern",
                Parameter.of("pattern", "The pattern to play", DataType.TEXT));
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
