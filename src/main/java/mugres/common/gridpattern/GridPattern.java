package mugres.common.gridpattern;

import mugres.common.Context;
import mugres.common.Length;
import mugres.common.TimeSignature;
import mugres.common.Value;
import mugres.common.gridpattern.converters.ElementPatternParser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class GridPattern<E> {
    private final String name;
    private final Value division;
    private final TimeSignature timeSignature;
    private final int slots;
    private final boolean keepPlaying;
    private final List<GridEvent<E>> events = new ArrayList<>();
    private final Map<String, String> attributes = new HashMap<>();

    public GridPattern(final String name, final Value division, final int slots,
                       final boolean keepPlaying, final TimeSignature timeSignature) {
        this.name = name;
        this.division = division;
        this.slots = slots;
        this.keepPlaying = keepPlaying;
        this.timeSignature = timeSignature;
    }

    public GridPattern(final String name, final Value division, final int slots,
                       final boolean keepPlaying, final TimeSignature timeSignature,
                       final Map<String, String> attributes) {
        this(name, division, slots, keepPlaying, timeSignature);
        this.attributes.putAll(attributes);
    }

    public static <X> GridPattern<X> parse(final String pattern, final ElementPatternParser<X> elementPatternParser,
                                           final Context context) {
        return parse(pattern, elementPatternParser, context, null);
    }

    public static <X> GridPattern<X> parse(final String pattern, final ElementPatternParser<X> elementPatternParser,
                                           final Context context, final Value noteValue) {
        final List<String> lines =
                stream(pattern.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !s.startsWith("#"))
                .collect(Collectors.toList());

        String name = "Untitled";
        Value division = noteValue;
        Integer slots = null;
        boolean keepPlaying = false;
        TimeSignature timeSignature = null;
        final Map<String, String> attributes = new HashMap<>();
        final List<GridEvent<X>> events = new ArrayList<>();

        // First we get all attributes
        for(String line : lines) {
            if (Pattern.matches(ATTRIBUTE_LINE.pattern(), line)) {
                final Matcher attributeMatcher = ATTRIBUTE_LINE.matcher(line);
                while (attributeMatcher.find()) {
                    final String attributeName = attributeMatcher.group(1);
                    final String attributeValue = attributeMatcher.group(2);

                    if ("Name".equals(attributeName))
                        name = attributeValue;
                    else if ("Division".equals(attributeName))
                        division = Value.valueOf(attributeValue);
                    else if ("Slots".equals(attributeName))
                        slots = Integer.parseInt(attributeValue);
                    else if ("KeepPlaying".equals(attributeName))
                        keepPlaying = Boolean.parseBoolean(attributeValue);
                    else if ("TimeSignature".equals(attributeName))
                        timeSignature = TimeSignature.of(attributeValue);
                    else
                        attributes.put(attributeName, attributeValue);
                }
            } else {
                final ElementPatternParser.ElementPattern<X>
                        elementPattern = elementPatternParser.parse(context, division, line);

                int eventSlot = 1;
                for (X eventData : elementPattern.getEvents()) {
                    events.add(GridEvent.of(eventSlot, elementPattern.getElement(), eventData));
                    eventSlot++;
                }
            }
        }

        if (division == null)
            throw new IllegalArgumentException("Invalid division value!");

        final Map<String, List<GridEvent<X>>> eventsByElement = events.stream()
                .collect(Collectors.groupingBy(GridEvent::getElement));
        if (slots != null) {
            for(String element : eventsByElement.keySet()) {
                final int actualEvents = eventsByElement.get(element).size();
                if (actualEvents != slots)
                    throw new RuntimeException(String.format("Event count mismatch for element '%s'. " +
                            "Expected: %d, actual: %d", element, slots, actualEvents));
            }
        } else {
            final List<Integer> eventSizes = eventsByElement.values().stream().map(List::size).distinct()
                    .collect(Collectors.toList());
            if (eventSizes.size() == 1)
                slots = eventSizes.get(0);
            else
                throw new RuntimeException(String.format("Could not infer number of grid pattern slots"));
        }


        final GridPattern<X> gridPattern = new GridPattern<>(name, division, slots,
                keepPlaying, timeSignature, attributes);

        final List<GridEvent<X>> sortedEvents = new ArrayList<>(events);
        sortedEvents.sort(Comparator.naturalOrder());
        sortedEvents.forEach(gridPattern::addEvent);

        return gridPattern;
    }

    public String getName() {
        return name;
    }

    public Value getDivision() {
        return division;
    }

    public boolean isKeepPlaying() {
        return keepPlaying;
    }

    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public List<GridEvent<E>> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void addEvent(final GridEvent<E> event) {
        events.add(event);
    }

    public int getSlots() {
        return slots;
    }

    public Length getLength() {
        return division.length().multiply(slots);
    }

    public int getLengthInMeasures() {
        if (timeSignature == null)
            throw new RuntimeException("Could not calculate length in measures when no Time Signature was set!");

        return division.length().multiply(slots).length() /
                (timeSignature.numerator() * timeSignature.denominator().length().length());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(String.format("Name: %s%n", name));
        sb.append(String.format("Division: %s%n", division));
        sb.append(String.format("Slots: %d%n", slots));
        sb.append(String.format("Keep playing: %s%n", keepPlaying));
        sb.append(String.format("Events: %n%n"));
        for(GridEvent<E> event : events)
            sb.append(String.format("%s%n", event));

        return sb.toString();
    }

    private static final Pattern ATTRIBUTE_LINE = Pattern.compile("^([a-zA-Z0-9-\\.]+)=(.*)$");
}
