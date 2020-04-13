package mugres.core.common.gridpattern.converters;

import mugres.core.common.Context;
import mugres.core.common.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DrumKitHitElementPatternParser implements ElementPatternParser<DrumKitHitElementPatternParser.DrumKitHit> {
    private static final DrumKitHitElementPatternParser INSTANCE = new DrumKitHitElementPatternParser();

    private DrumKitHitElementPatternParser() {}

    public static DrumKitHitElementPatternParser getInstance() {
        return INSTANCE;
    }

    private DrumKitHit convert(final String data) {
        if (NO_EVENT.equals(data)) return null;
        if ("X".equals(data)) return NORMAL;
        if ("x".equals(data)) return SOFT;

        throw new IllegalArgumentException("Invalid DrumKit hit specification: " + data);
    }

    @Override
    public ElementPattern<DrumKitHit> parse(final Context context, final Value slotValue, final String line) {
        final Matcher elementMatcher = ELEMENT_LINE.matcher(line);
        if (!elementMatcher.matches())
            throw new IllegalArgumentException("Invalid element pattern: " + line);

        final String elementName = elementMatcher.group(1).trim();
        final String elementEvents = elementMatcher.group(2);

        final char[] chars = elementEvents.toCharArray();
        final List<String> strings = new ArrayList<>();
        for(int index = 0; index < chars.length; index++)
            strings.add(String.valueOf(chars[index]));

        return ElementPattern.of(elementName, strings.stream()
                .filter(s -> !s.trim().isEmpty())
                .map(this::convert)
                .collect(Collectors.toList()));
    }

    public static final DrumKitHit NORMAL = DrumKitHit.of(DrumKitHit.Intensity.NORMAL);
    public static final DrumKitHit SOFT = DrumKitHit.of(DrumKitHit.Intensity.SOFT);
    private static final Pattern ELEMENT_LINE = Pattern.compile("^([a-zA-Z0-9-\\.]+\\s+)\\s*(.+)$");

    public static class DrumKitHit {
        private final Intensity intensity;

        private DrumKitHit(final Intensity intensity) {
            this.intensity = intensity;
        }

        public static DrumKitHit of(final Intensity intensity) {
            return new DrumKitHit(intensity);
        }

        public Intensity getIntensity() {
            return intensity;
        }

        public enum Intensity {
            NORMAL,
            SOFT
        }

        @Override
        public String toString() {
            return intensity.toString();
        }
    }
}
