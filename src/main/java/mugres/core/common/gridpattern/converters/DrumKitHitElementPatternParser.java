package mugres.core.common.gridpattern.converters;

import mugres.core.common.Context;
import mugres.core.common.Value;
import mugres.core.common.gridpattern.converters.DrumKitHitElementPatternParser.DrumKitHit.Intensity;

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
        return DrumKitHit.of(Intensity.forSymbol(data));
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
            HARD("H", 127),
            NORMAL_A("X", 100),
            NORMAL_B("x", 80),
            I1("1", 1),
            I2("2", 9),
            I3("3", 18),
            I4("4", 27),
            I5("5", 36),
            I6("6", 45),
            I7("7", 54),
            I8("8", 63),
            I9("9", 72),
            IA("A", 81),
            IB("B", 90),
            IC("C", 99),
            ID("D", 108),
            IE("E", 117),
            IF("F", 127);

            private final String symbol;
            private final int velocity;

            Intensity(final String symbol, final int velocity) {
                this.symbol = symbol;
                this.velocity = velocity;
            }

            public static Intensity forSymbol(final String symbol) {
                for(final Intensity intensity : values())
                    if (intensity.symbol.equals(symbol))
                        return intensity;

                throw new IllegalArgumentException("Unknown hit intensity with symbol: " + symbol);
            }

            public String getSymbol() {
                return symbol;
            }

            public int getVelocity() {
                return velocity;
            }
        }

        @Override
        public String toString() {
            return intensity.toString();
        }
    }
}
