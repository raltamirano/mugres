package mugres.core.function.builtin.drums;

import mugres.core.common.*;
import mugres.core.common.gridpattern.GridEvent;
import mugres.core.common.gridpattern.GridPattern;
import mugres.core.common.gridpattern.converters.DrumKitHitDataConverter;
import mugres.core.common.gridpattern.converters.DrumKitHitDataConverter.DrumKitHit.Intensity;
import mugres.core.function.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DrumPattern extends Function {
    public DrumPattern() {
        super("drumPattern", "Reproduces a predefined drums pattern",
                Parameter.of("pattern", "The pattern to play", Parameter.DataType.TEXT));
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final Length length = readMeasuresLength(context, arguments);
        final String pattern = (String)arguments.get("pattern");

        final GridPattern<DrumKitHitDataConverter.DrumKitHit> drumPattern =
                GridPattern.parse(pattern, DrumKitHitDataConverter.getInstance());

        if (!length.equals(drumPattern.getLength()))
            throw new RuntimeException("Drum pattern's length does not match function call's length!");

        final List<Event> events = new ArrayList<>();

        for(GridEvent<DrumKitHitDataConverter.DrumKitHit> hit : drumPattern.getEvents()) {
            final Length position = drumPattern.getDivision().length().multiply(hit.getSlot() - 1);
            final Intensity intensity = hit.getData().getIntensity();
            final DrumKit drumKitElement = DrumKit.valueOf(hit.getElement());
            final int velocity = intensity == Intensity.NORMAL ? 100 :
                    intensity == Intensity.SOFT ? 60 : 0;

            events.add(Event.of(position, Pitch.of(drumKitElement.getMidi()), drumPattern.getDivision(), velocity));
        }

        return events;
    }
}
