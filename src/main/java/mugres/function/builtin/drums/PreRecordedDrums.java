package mugres.function.builtin.drums;

import mugres.common.*;
import mugres.common.gridpattern.GridPattern;
import mugres.common.gridpattern.converters.DrumKitHitElementPatternParser;
import mugres.function.Function.EventsFunction;
import mugres.parametrizable.Parameter;
import mugres.common.Variant;
import mugres.tracker.Event;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static mugres.common.DrumKit.BD;
import static mugres.common.DrumKit.SD;
import static mugres.common.gridpattern.converters.DrumKitHitElementPatternParser.DrumKitHit.Intensity.HARD;
import static mugres.common.Variant.*;
import static mugres.utils.Randoms.random;

public abstract class PreRecordedDrums extends EventsFunction {
    protected PreRecordedDrums(final String name, final String description) {
        super(name, description,
                Parameter.of("variant", "Variant", 1, "The pattern variant to play",
                        DataType.VARIANT, true, V0),
                Parameter.of("startingHit", "Starting Hit", 2,
                        "Starting Hit (usually a crash cymbal)", DataType.DRUM_KIT, true,
                        null),
                Parameter.of("fill", "Fill", 3,"Ending fill",
                        DataType.VARIANT, true, NONE));
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final int lengthInMeasures = (Integer) arguments.get(LENGTH_PARAMETER.name());
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final Variant variant = (Variant) arguments.get("variant");
        final Variant fill = (Variant) arguments.get("fill");
        final DrumKit startingHit = (DrumKit) arguments.get("startingHit");
        final String timeSignatureId = String.format("ts%d%d",
                context.timeSignature().numerator(),
                context.timeSignature().denominator().denominator());

        final List<Event> mainEvents = new ArrayList<>();
        final List<Event> fillEvents = new ArrayList<>();
        final List<Event> events = new ArrayList<>();

        final GridPattern<DrumKitHitElementPatternParser.DrumKitHit> fillPattern;
        if (fill != NONE) {
            final String fillVariant = String.format("%s/%s-%s-%s",
                    name(), timeSignatureId, FILL, fill.name().toLowerCase());

            fillPattern = loadPattern(context, fillVariant);
            if (fillPattern.getLength().length() > length.length())
                throw new RuntimeException("Requested fill is larger than the requested length!");

            fillEvents.addAll(Utils.extractEvents(fillPattern));
        } else
            fillPattern = null;

        // If there's room for the main part of the pattern...
        if (fill == NONE || fillPattern.getLength().length() < length.length()) {
            final String mainVariant = variant == RANDOM ?
                    pickRandomPatternName() :
                    String.format("%s/%s-%s-%s", name(), timeSignatureId, MAIN, variant.name().toLowerCase());
            final GridPattern<DrumKitHitElementPatternParser.DrumKitHit> mainPattern =
                    loadPattern(context, mainVariant);

            if (fill != NONE && fillPattern.getDivision() != mainPattern.getDivision())
                throw new RuntimeException("Main and fill pattern must have the same 'Division' value!");

            final int remainingMeasures = lengthInMeasures - (fill == NONE ? 0 : fillPattern.getLengthInMeasures());
            final Length fillOffset =  context.timeSignature().measuresLength(remainingMeasures);
            offsetEvents(fillEvents, fillOffset);

            final int wholeRepeats = remainingMeasures / mainPattern.getLengthInMeasures();
            for (int i = 0; i < wholeRepeats; i++) {
                final List<Event> newEvents = Utils.extractEvents(mainPattern);
                final Length mainOffset = mainPattern.getLength().multiply(i);
                offsetEvents(newEvents, mainOffset);
                mainEvents.addAll(newEvents);
            }

            final int addMeasures = remainingMeasures % mainPattern.getLengthInMeasures();
            final List<Event> moreEvents = Utils.extractEvents(mainPattern, 1, addMeasures);
            final Length moreOffset = mainPattern.getLength().multiply(wholeRepeats);
            offsetEvents(moreEvents, moreOffset);
            mainEvents.addAll(moreEvents);
        }

        events.addAll(mainEvents);
        events.addAll(fillEvents);

        if (startingHit != null && startingHit != BD && startingHit != SD) {
            final AtomicBoolean replaced = new AtomicBoolean();
            final Set<Event> toRemove = new HashSet<>();
            events.forEach(event -> {
                if (event.position().equals(Length.ZERO) &&
                        (event.pitch().midi() != BD.midi() && event.pitch().midi() != SD.midi())) {
                    if (event.pitch().midi() == startingHit.midi()) {
                        event.velocity(HARD.velocity());
                        replaced.set(true);
                    } else {
                        toRemove.add(event);
                    }
                }
            });

            toRemove.forEach(event -> events.remove(event));
            if (!replaced.get()) {
                events.add(0, Event.of(Length.ZERO, Pitch.of(startingHit.midi()),
                        Value.QUARTER, HARD.velocity()));
            }
        }

        return events;
    }

    private void offsetEvents(final List<Event> events, final Length offset) {
        for(int i=0; i < events.size(); i++)
            events.set(i, events.get(i).offset(offset));
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

    private String pickRandomPatternName()  {
        try {
            final List<String> files = IOUtils.readLines(new StringReader(IOUtils.resourceToString("/drum-patterns/" + name(), Charset.defaultCharset())));
            return name() + "/" + random(files);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String MAIN = "main";
    private static final String FILL = "fill";
}
