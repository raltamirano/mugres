package mugres.core.live.processor.transformer.config;

import mugres.core.filter.Filter;
import mugres.core.live.signaler.Signaler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;

public class Configuration {
    private final List<Signaler> signalers = new ArrayList<>();
    private final List<FilterEntry> filters = new ArrayList<>();

    public List<Signaler> getSignalers() {
        return unmodifiableList(signalers);
    }

    public List<FilterEntry> getFilters() {
        return unmodifiableList(filters);
    }

    public void addSignaler(final Signaler signaler) {
        signalers.add(signaler);
    }

    public void appendFilter(final String filter, final Map<String, Object> args) {
        filters.add(new FilterEntry(Filter.forName(filter), args));
    }

    public void appendFilter(final Filter filter, final Map<String, Object> args) {
        filters.add(new FilterEntry(filter, args));
    }

    public static class FilterEntry {
        private final Filter filter;
        private final Map<String, Object> args;

        private FilterEntry(final Filter filter, final Map<String, Object> args) {
            if (filter == null)
                throw new IllegalArgumentException("filter");

            this.filter = filter;
            this.args = args == null ? Collections.emptyMap() : args;
        }

        public static FilterEntry of(final Filter filter, final Map<String, Object> args) {
            return new FilterEntry(filter, args);
        }

        public Filter getFilter() {
            return filter;
        }

        public Map<String, Object> getArgs() {
            return args;
        }
    }
}
