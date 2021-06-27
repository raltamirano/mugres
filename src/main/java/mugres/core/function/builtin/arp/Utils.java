package mugres.core.function.builtin.arp;

import mugres.core.common.Value;

import java.util.regex.Pattern;

import static mugres.core.common.Value.QUARTER;

public class Utils {
    private Utils() {}

    public static Value parseNoteValue(final String input) {
        return input == null || input.trim().isEmpty() ? QUARTER : Value.of(input);
    }

    public static final String REST = "R";
    public static final Pattern ARP_PATTERN = Pattern.compile("(([1-9]|" + REST + ")(w|h|q|e|s|t|m)?)+?");
}
