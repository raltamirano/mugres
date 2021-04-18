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

import static mugres.core.utils.Randoms.random;

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
            // Do nothing with rests
            if (e.getType() != ArpEntry.Type.REST) {
                final Signal signal;
                switch(e.getType()) {
                    case NOTE:
                        signal = actives.signals().size() >= e.noteIndex ?
                            actives.signals().get(e.noteIndex - 1) : null;
                        break;
                    case RANDOM:
                        signal = random(actives.signals());
                        break;
                    default:
                        signal = null;
                        // TODO: logging!
                }

                if (signal != null) {
                    result.add(signal.modifiedTime(startTime + delta));
                    result.add(signal.modifiedTime(startTime + delta + e.millis).toOff());
                }
            }
            delta += e.millis + 1;
        }

        return result;
    }

    private static List<ArpEntry> getPattern(final Context context, final Map<String, Object> arguments) {
        final List<ArpEntry> pattern = new ArrayList<>();
        final Value defaultValue = getTimeSignature(context, arguments).getDenominator();

        final Matcher matcher = ARP_PATTERN.matcher(arguments.get("pattern").toString());

        while(matcher.find()) {
            final String note = matcher.group(2);
            final String duration = matcher.group(3);
            final ArpEntry.Type type = getArpEntryType(note);
            pattern.add(ArpEntry.of(type, type == ArpEntry.Type.NOTE ? Integer.valueOf(note) : null,
                    parseEntryDuration(context, arguments, defaultValue, duration)));
        }

        return pattern;
    }

    private static ArpEntry.Type getArpEntryType(final String input) {
        switch (input) {
            case REST: return ArpEntry.Type.REST;
            case RANDOM_NOTE: return ArpEntry.Type.RANDOM;
            default: return ArpEntry.Type.NOTE;
        }
    }

    private static long parseEntryDuration(final Context context, final Map<String, Object> arguments,
                                           final Value defaultValue, final String input) {
        if (input != null && input.trim().endsWith(MILLIS))
            return Long.parseLong(input.substring(0, input.length() - MILLIS.length()));

        final int bpm = getTempo(context, arguments);
        final Value value  = parseNoteValue(input, defaultValue);

        return value.length().toMillis(bpm);
    }

    private static Value parseNoteValue(final String input, final Value defaultValue) {
        return input == null || input.trim().isEmpty() ? defaultValue : Value.forId(input);
    }


    private static final String REST = "R";
    private static final String RANDOM_NOTE = "X";
    private static final String MILLIS = "ms";
    private static final Pattern ARP_PATTERN = Pattern.compile("(([1-9]|" + REST + "|" + RANDOM_NOTE + ")\\s?(w|h|q|e|s|t|m|[1-9]\\d*"+ MILLIS + ")?)+?");

    private static class ArpEntry {
        private final Type type;
        private final Integer noteIndex;
        private final long millis;

        private ArpEntry(final Type type, final Integer noteIndex, final long millis) {
            this.type = type;
            this.noteIndex = noteIndex;
            this.millis = millis;
        }

        public static ArpEntry of(final Type type, final Integer noteIndex, final long millis) {
            return new ArpEntry(type, noteIndex, millis);
        }

        public Type getType() {
            return type;
        }

        public Integer getNoteIndex() {
            return noteIndex;
        }

        public long getMillis() {
            return millis;
        }

        public enum Type {
            NOTE,
            REST,
            RANDOM
        }
    }
}
