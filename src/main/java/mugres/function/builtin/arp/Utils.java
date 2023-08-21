package mugres.function.builtin.arp;

import mugres.common.Value;
import static mugres.common.Value.QUARTER;

public class Utils {
    private Utils() {}

    public static Value parseNoteValue(final String input) {
        return input == null || input.trim().isEmpty() ? QUARTER : Value.of(input);
    }
}
