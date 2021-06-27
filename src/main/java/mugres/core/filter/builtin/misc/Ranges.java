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
    public static final String NAME = "Ranges";

    public Ranges(final Map<String, Object> arguments) {
        super(arguments);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals) {
        final List<Range> ranges = getRanges(arguments);

        for(final Signal in : signals.signals()) {
            boolean matched = false;
            for(final Range range : ranges) {
                if (!range.isRemaining() && range.contains(in.getPlayed().pitch())) {
                    matched = true;
                    in.addTag(range.getTag());
                    break;
                }
            }

            if (!matched) {
                for(final Range range : ranges)
                    if (range.isRemaining())
                        in.addTag(range.getTag());
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
                final boolean remaining = REMAINING_MARK.equals(parts[1]);
                final Range range;
                if (remaining) {
                    range = Range.remaining(tag);
                } else {
                    final int start = Integer.valueOf(parts[1]);
                    final int end = Integer.valueOf(parts[2]);
                    range = Range.of(tag, Pitch.of(start), Pitch.of(end));
                }
                validateNoOverlapping(ranges, range);
                ranges.add(range);
            }
        }

        Collections.sort(ranges);

        return ranges;
    }

    private void validateNoOverlapping(final List<Range> ranges, final Range toCheck) {
        if (toCheck.isRemaining())
            return;

        for(final Range existing : ranges)
            if (existing.overlapsWith(toCheck))
                throw new RuntimeException("Ranges filter can allow overlapping ranges!");
    }

    private static final String RANGES_SEPARATOR = ",";
    private static final String PARTS_SEPARATOR = ":";
    private static final String REMAINING_MARK = "*";
    private static final Pattern RANGE_PATTERN = Pattern.compile("[a-zA-Z0-9\\/\\_\\-]+" + PARTS_SEPARATOR + "(\\d{1,3}+\\" + PARTS_SEPARATOR + "\\d{1,3}|\\" + REMAINING_MARK +")");
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
            if (tag == null || tag.trim().isEmpty())
                throw new IllegalArgumentException("tag");
            if (start == null)
                throw new IllegalArgumentException("start");
            if (end == null)
                throw new IllegalArgumentException("end");
            if (start.compareTo(end) > 0)
                throw new IllegalArgumentException("start can't be greater than end!");

            return new Range(tag, start, end);
        }

        public static Range remaining(final String tag) {
            return new Range(tag, null, null);
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

        public boolean isRemaining() {
            return start == null && end == null;
        }

        @Override
        public int compareTo(final Range o) {
            if (isRemaining()) return 1;
            else if (o.isRemaining()) return -1;
            return Integer.compare(this.start.getMidi(), o.start.getMidi());
        }
    }
}
