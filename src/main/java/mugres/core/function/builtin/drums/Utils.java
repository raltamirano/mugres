package mugres.core.function.builtin.drums;

import mugres.core.common.DrumKit;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.common.Pitch;
import mugres.core.common.gridpattern.GridEvent;
import mugres.core.common.gridpattern.GridPattern;
import mugres.core.common.gridpattern.converters.DrumKitHitElementPatternParser.DrumKitHit;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private Utils() {}

    public static List<Event> extractEvents(final GridPattern<DrumKitHit> source) {
        return extractEvents(source, 1, source.getLengthInMeasures());
    }

    public static List<Event> extractEvents(final GridPattern<DrumKitHit> source,
                                            final int startMeasure, final int endMeasure) {
        final List<Event> events = new ArrayList<>();

        final int startingSlot = (source.getDivision().denominator() * (startMeasure - 1)) + 1;
        final int endingSlot = source.getDivision().denominator() * endMeasure;

        for (final GridEvent<DrumKitHit> hit : source.getEvents()) {
            if (hit.getSlot() >= startingSlot && hit.getSlot() <= endingSlot) {
                if (hit.isEmpty())
                    continue;

                final Length position = source.getDivision().length().multiply(hit.getSlot() - startingSlot);
                final DrumKit drumKitElement = DrumKit.valueOf(hit.getElement());
                final int velocity = hit.getData().getIntensity().getVelocity();

                events.add(Event.of(position, Pitch.of(drumKitElement.midi()),
                        source.getDivision(), velocity));
            }
        }

        return events;
    }
}
