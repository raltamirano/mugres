package mugres.core.function.builtin.arp;

import java.util.regex.Pattern;

public class Utils {
    private Utils() {}

    public static final String REST = "R";
    public static final Pattern ARP_PATTERN = Pattern.compile("((\\d|" + REST + ")(w|h|q|e|s|t|m)?)+?");
}
