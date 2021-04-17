package mugres.core.filter.builtin.arp;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.Signals;
import mugres.core.common.Value;
import mugres.core.filter.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mugres.core.common.Value.QUARTER;

public class Arpeggiate extends Filter {
    public Arpeggiate() {
        super("Arpeggiate");
    }

    @Override
    protected boolean canHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return !signals.actives().isEmpty();
    }

    @Override
    protected Signals handle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        final Signals result = Signals.create();
        final List<ArpEntry> pattern = getPattern(context, arguments);
        final Signals actives = signals.actives();

        long startTime = actives.first().getTime();
        long delta = 0;
        for(final ArpEntry e : pattern) {
            // Do nothing with note indexes outside of the given notes
            if (actives.signals().size() >= e.noteIndex) {
                final Signal signal = actives.signals().get(e.noteIndex - 1);
                result.add(signal.modifiedTime(startTime + delta));
                result.add(signal.modifiedTime(startTime + delta + e.millis).toOff());
                delta += e.millis + 1;
            }
        }

        return result;
    }

    private static List<ArpEntry> getPattern(final Context context, final Map<String, Object> arguments) {
        final List<ArpEntry> pattern = new ArrayList<>();

        final Matcher matcher = ARP_PATTERN.matcher(arguments.get("pattern").toString());

        while(matcher.find())
            pattern.add(new ArpEntry(Integer.valueOf(matcher.group(2)),
                    parseEntryDuration(context, arguments, matcher.group(3))));

        return pattern;
    }

    private static long parseEntryDuration(final Context context, final Map<String, Object> arguments,
                                           final String input) {
        if (input.endsWith(MILLIS))
            return Long.parseLong(input.substring(0, input.length() - MILLIS.length()));

        final int bpm = getTempo(context, arguments);
        final Value value  = parseNoteValue(input);

        return value.length().toMillis(bpm);
    }

    private static Value parseNoteValue(final String input) {
        return input == null || input.trim().isEmpty() ? QUARTER : Value.forId(input);
    }


    private static final String REST = "R";
    private static final String MILLIS = "ms";
    private static final Pattern ARP_PATTERN = Pattern.compile("(([1-9]\\d*|" + REST + ")\\s(w|h|q|e|s|t|m|[1-9]\\d*"+ MILLIS + ")?)+?");

    private static class ArpEntry {
        private final int noteIndex;
        private final long millis;

        public ArpEntry(final int noteIndex, final long millis) {
            this.noteIndex = noteIndex;
            this.millis = millis;
        }

        public int getNoteIndex() {
            return noteIndex;
        }

        public long getMillis() {
            return millis;
        }
    }
}
