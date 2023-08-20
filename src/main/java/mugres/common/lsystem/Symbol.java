package mugres.common.lsystem;

import java.util.Objects;

public class Symbol {
    private final char character;

    private Symbol(final char character) {
        this.character = character;
    }

    public char character() {
        return character;
    }


    public static Symbol of(final String character) {
        if (character == null || character.length() != 1)
            throw new IllegalArgumentException("character");

        return new Symbol(character.charAt(0));
    }

    public static Symbol of(final char character) {
        return new Symbol(character);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return character == symbol.character;
    }

    @Override
    public int hashCode() {
        return Objects.hash(character);
    }

    @Override
    public String toString() {
        return String.valueOf(character);
    }
}
