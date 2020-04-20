package mugres.core.function.builtin.drums;

import mugres.core.common.*;
import mugres.core.common.gridpattern.GridPattern;
import mugres.core.common.gridpattern.converters.DrumKitHitElementPatternParser;
import mugres.core.function.Function.EventsFunction;
import mugres.core.function.Function.Parameter.Variant;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static mugres.core.common.DrumKit.BD;
import static mugres.core.common.DrumKit.SD;
import static mugres.core.common.gridpattern.converters.DrumKitHitElementPatternParser.DrumKitHit.Intensity.HARD;
import static mugres.core.function.Function.Parameter.Variant.NONE;
import static mugres.core.function.Function.Parameter.Variant.V0;

public abstract class PreRecordedDrums extends EventsFunction {
    protected PreRecordedDrums(final String name, final String description) {
        super(name, description,
                Parameter.of("variant", "The pattern variant to play",
                        Parameter.DataType.VARIANT, true, V0),
                Parameter.of("startingHit", "Starting Hit (usually a crash cymbal)",
                        Parameter.DataType.DRUM_KIT, true, null),
                Parameter.of("fill", "Ending fill",
                        Parameter.DataType.VARIANT, true, NONE));
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final int lengthInMeasures = (Integer) arguments.get(LENGTH_PARAMETER.getName());
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final Variant variant = (Variant) arguments.get("variant");
        final Variant fill = (Variant) arguments.get("fill");
        final DrumKit startingHit = (DrumKit) arguments.get("startingHit");
        final String timeSignatureId = String.format("ts%d%d",
                context.getTimeSignature().getNumerator(),
                context.getTimeSignature().getDenominator().denominator());

        final List<Event> mainEvents = new ArrayList<>();
        final List<Event> fillEvents = new ArrayList<>();
        final List<Event> events = new ArrayList<>();

        final GridPattern<DrumKitHitElementPatternParser.DrumKitHit> fillPattern;
        if (fill != NONE) {
            final String fillVariant = String.format("%s/%s-%s-%s",
                    getName(), timeSignatureId, FILL, fill.name().toLowerCase());

            fillPattern = loadPattern(context, fillVariant);
            if (fillPattern.getLength().getLength() > length.getLength())
                throw new RuntimeException("Requested fill is larger than the requested length!");

            fillEvents.addAll(Utils.extractEvents(fillPattern));
        } else
            fillPattern = null;

        // If there's room for the main part of the pattern...
        if (fill == NONE || fillPattern.getLength().getLength() < length.getLength()) {
            final String mainVariant = String.format("%s/%s-%s-%s",
                    getName(), timeSignatureId, MAIN, variant.name().toLowerCase());
            final GridPattern<DrumKitHitElementPatternParser.DrumKitHit> mainPattern =
                    loadPattern(context, mainVariant);

            if (fill != NONE && fillPattern.getDivision() != mainPattern.getDivision())
                throw new RuntimeException("Main and fill pattern must have the same 'Division' value!");

            final int remainingMeasures = lengthInMeasures - (fill == NONE ? 0 : fillPattern.getLengthInMeasures());
            final Length fillOffset =  context.getTimeSignature().measuresLength(remainingMeasures);
            fillEvents.forEach(event -> event.offset(fillOffset));

            final int wholeRepeats = remainingMeasures / mainPattern.getLengthInMeasures();
            for (int i = 0; i < wholeRepeats; i++) {
                final List<Event> newEvents = Utils.extractEvents(mainPattern);
                final Length mainOffset = mainPattern.getLength().multiply(i);
                newEvents.forEach(event -> event.offset(mainOffset));
                mainEvents.addAll(newEvents);
            }

            final int addMeasures = remainingMeasures % mainPattern.getLengthInMeasures();
            final List<Event> moreEvents = Utils.extractEvents(mainPattern, 1, addMeasures);
            final Length moreOffset = mainPattern.getLength().multiply(wholeRepeats);
            moreEvents.forEach(event -> event.offset(moreOffset));
            mainEvents.addAll(moreEvents);
        }

        events.addAll(mainEvents);
        events.addAll(fillEvents);

        if (startingHit != null && startingHit != BD && startingHit != SD) {
            final AtomicBoolean replaced = new AtomicBoolean();
            final Set<Event> toRemove = new HashSet<>();
            events.forEach(event -> {
                if (event.getPosition().equals(Length.ZERO) &&
                        (event.getPlayed().getPitch().getMidi() != BD.getMidi() && event.getPlayed().getPitch().getMidi() != SD.getMidi())) {
                    if (event.getPlayed().getPitch().getMidi() == startingHit.getMidi()) {
                        event.getPlayed().setVelocity(HARD.getVelocity());
                        replaced.set(true);
                    } else {
                        toRemove.add(event);
                    }
                }
            });

            toRemove.forEach(event -> events.remove(event));
            if (!replaced.get()) {
                events.add(0, Event.of(Length.ZERO, Pitch.of(startingHit.getMidi()),
                        Value.QUARTER, HARD.getVelocity()));
            }
        }

        return events;
    }

    private GridPattern<DrumKitHitElementPatternParser.DrumKitHit> loadPattern(final Context context,
                                                                               final String id)  {
        try {
            final String pattern = IOUtils.resourceToString("/drum-patterns/" + id, Charset.defaultCharset());
            return GridPattern.parse(pattern, DrumKitHitElementPatternParser.getInstance(), context);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String MAIN = "main";
    private static final String FILL = "fill";
}
