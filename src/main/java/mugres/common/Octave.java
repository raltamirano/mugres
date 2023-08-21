package mugres.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Octave {
    public static final Octave SAME = Octave.of(Type.INCREMENT, 0);
    private final Type type;
    private final int value;

    private Octave(final Type type, final int value) {
        this.type = type;
        this.value = value;
    }

    public static Octave of(final Type type, final int value) {
        return new Octave(type, value);
    }

    public static Octave of(final String spec) {
        final Matcher matcher = OCTAVE_PATTERN.matcher(spec);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid octave spec: " + spec);

        return of(Type.fromSymbol(matcher.group(1)), Integer.valueOf(matcher.group(2)));
    }

    public Type type() {
        return type;
    }

    public int value() {
        return value;
    }

    public Pitch apply(final Pitch pitch) {
        if (type == Type.INCREMENT)
            return Pitch.of(pitch.note(), pitch.octave() + value);
        if (type == Type.DECREMENT)
            return Pitch.of(pitch.note(), pitch.octave() - value);
        return Pitch.of(pitch.note(), value);
    }

    public static final Pattern OCTAVE_PATTERN = Pattern.compile("\\[(\\+|-|=)(\\d)]");

    private enum Type {
        SET("="),
        INCREMENT("+"),
        DECREMENT("-");

        private final String symbol;

        Type(final String symbol) {
            this.symbol = symbol;
        }

        public static Type fromSymbol(final String symbol) {
            for(Type t : values())
                if (t.symbol.equals(symbol))
                    return t;

            throw new IllegalArgumentException("Invalid symbol: " + symbol);
        }

        public String symbol() {
            return symbol;
        }
    }
}
