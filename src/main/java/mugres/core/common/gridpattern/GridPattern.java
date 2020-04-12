package mugres.core.common.gridpattern;

import mugres.core.common.Length;
import mugres.core.common.Value;
import mugres.core.common.gridpattern.converters.DataConverter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class GridPattern<E> {
    private String name;
    private Value division;
    private final int slots;
    private boolean keepPlaying;
    private final List<GridEvent<E>> events = new ArrayList<>();
    private final Map<String, String> attributes = new HashMap<>();

    public GridPattern(final String name, final Value division, final int slots, final boolean keepPlaying) {
        this.name = name;
        this.division = division;
        this.slots = slots;
        this.keepPlaying = keepPlaying;
    }

    public GridPattern(final String name, final Value division, final int slots, final boolean keepPlaying,
                       final Map<String, String> attributes) {
        this(name, division, slots, keepPlaying);
        this.attributes.putAll(attributes);
    }

    public static <X> GridPattern<X> parse(final String pattern, final DataConverter<X> dataConverter) {
        final List<String> lines =
                stream(pattern.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !s.startsWith("#"))
                .collect(Collectors.toList());

        String name = "Untitled";
        Value division = null;
        Integer slots = null;
        boolean keepPlaying = false;
        final Map<String, String> attributes = new HashMap<>();
        final List<GridEvent<X>> events = new ArrayList<>();

        for(String line : lines) {
            if (Pattern.matches(ATTRIBUTE_LINE.pattern(), line)) {
                final Matcher attributeMatcher = ATTRIBUTE_LINE.matcher(line);
                while(attributeMatcher.find()) {
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
                    else
                        attributes.put(attributeName, attributeValue);
                }
            } else if (Pattern.matches(ELEMENT_LINE.pattern(), line)) {
                final Matcher elementMatcher = ELEMENT_LINE.matcher(line);
                while(elementMatcher.find()) {
                    final String elementData = elementMatcher.group(1);
                    final String elementName = elementData == null || elementData.trim().isEmpty() ?
                            "XXX" : elementData.trim();
                    final String elementEvents = elementMatcher.group(2);
                    int eventSlot = 1;
                    final List<X> lineEvents = dataConverter.tokenize(elementEvents);

                    for (X eventData : lineEvents) {
                        events.add(GridEvent.of(eventSlot, elementName, eventData));
                        eventSlot++;
                    }
                }
            } else {
                throw new RuntimeException("Invalid Grid Pattern line: " + line);
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


        final GridPattern<X> gridPattern = new GridPattern<>(name, division, slots, keepPlaying, attributes);

        final List<GridEvent<X>> sortedEvents = new ArrayList<>(events);
        sortedEvents.sort(Comparator.naturalOrder());
        sortedEvents.forEach(gridPattern::addEvent);

        return gridPattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Value getDivision() {
        return division;
    }

    public void setDivision(Value division) {
        this.division = division;
    }

    public boolean isKeepPlaying() {
        return keepPlaying;
    }

    public void setKeepPlaying(boolean keepPlaying) {
        this.keepPlaying = keepPlaying;
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public List<GridEvent<E>> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void setAttribute(final String name, final String value) {
        attributes.put(name, value);
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

    public static final String NO_EVENT = "-";
    private static final Pattern ATTRIBUTE_LINE = Pattern.compile("^([a-zA-Z0-9-.]+)=(.*)$");
    private static final Pattern ELEMENT_LINE = Pattern.compile("^([a-zA-Z0-9-.]+\\s+)?\\s*(.+)$");
}
