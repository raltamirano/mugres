package mugres.core.filter.builtin.misc;

import mugres.core.common.Context;
import mugres.core.common.Pitch;
import mugres.core.common.Signal;
import mugres.core.common.Signals;
import mugres.core.filter.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

public class Ranges extends Filter {
    public Ranges() {
        super("Ranges");
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals, final Map<String, Object> arguments) {
        final List<Range> ranges = getRanges(arguments);

        for(final Signal in : signals.signals()) {
            for(int index=0; index<ranges.size(); index++) {
                if (ranges.get(index).contains(in.getPlayed().getPitch())) {
                    in.addTag(ranges.get(index).getTag());
                    break;
                }
            }
        }

        return signals;
    }

    private List<Range> getRanges(final Map<String, Object> arguments) {
        if (!arguments.containsKey("ranges"))
            return emptyList();

        final String rangesValue = arguments.get("ranges").toString();
        if (!RANGES_PATTERN.matcher(rangesValue).matches())
            return emptyList();

        final List<Range> ranges = new ArrayList<>();
        for(final String rangeValue : rangesValue.split(RANGES_SEPARATOR)) {
            final Matcher matcher = RANGE_PATTERN.matcher(rangeValue);
            if (matcher.matches()) {
                final String[] parts = rangeValue.split(PARTS_SEPARATOR);
                final String tag = parts[0];
                final int start = Integer.valueOf(parts[1]);
                final int end = Integer.valueOf(parts[2]);
                final Range range = Range.of(tag, Pitch.of(start), Pitch.of(end));
                validateNoOverlapping(ranges, range);
                ranges.add(range);
            }
        }

        Collections.sort(ranges);

        return ranges;
    }

    private void validateNoOverlapping(final List<Range> ranges, final Range toCheck) {
        for(final Range existing : ranges)
            if (existing.overlapsWith(toCheck))
                throw new RuntimeException("Ranges filter can allow overlapping ranges!");
    }

    private static final String RANGES_SEPARATOR = ",";
    private static final String PARTS_SEPARATOR = ":";
    private static final Pattern RANGE_PATTERN = Pattern.compile("[a-zA-Z0-9\\/\\_\\-]+" + PARTS_SEPARATOR + "\\d{1,3}+\\" + PARTS_SEPARATOR + "\\d{1,3}");
    private static final Pattern RANGES_PATTERN = Pattern.compile("^" + RANGE_PATTERN.pattern() + "(" + RANGES_SEPARATOR + RANGE_PATTERN.pattern() + ")*$");

    private static class Range implements Comparable<Range> {
        private String tag;
        private Pitch start;
        private Pitch end;

        private Range(final String tag, final Pitch start, final Pitch end) {
            this.tag = tag;
            this.start = start;
            this.end = end;
        }

        public static Range of(final String tag, final Pitch start, final Pitch end) {
            return new Range(tag, start, end);
        }

        public String getTag() {
            return tag;
        }

        public Pitch getStart() {
            return start;
        }

        public Pitch getEnd() {
            return end;
        }

        public boolean overlapsWith(final Range o) {
            return false;
        }

        public boolean contains(final Pitch pitch) {
            return pitch.compareTo(start) >= 0 && pitch.compareTo(end) <= 0;
        }

        @Override
        public int compareTo(final Range o) {
            return Integer.compare(this.start.getMidi(), o.start.getMidi());
        }
    }
}
