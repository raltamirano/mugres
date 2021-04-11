package mugres.core.live.processors.transformer.config;

import mugres.core.live.processors.transformer.filters.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Configuration {
    private final List<FilterEntry> filters = new ArrayList<>();

    public List<FilterEntry> getFilters() {
        return filters;
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
