package mugres.core.live.processor.transformer.config;

import mugres.core.filter.Filter;
import mugres.core.live.signaler.Signaler;

import java.util.*;

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

    public void appendFilter(final String filter, final String parameter1, final Object value1) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        appendFilter(filter, parameters);
    }

    public void appendFilter(final String filter, final String parameter1, final Object value1,
                             final String parameter2, final Object value2) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        parameters.put(parameter2, value2);
        appendFilter(filter, parameters);
    }

    public void appendFilter(final String filter, final String parameter1, final Object value1,
                             final String parameter2, final Object value2,
                             final String parameter3, final Object value3) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        parameters.put(parameter2, value2);
        parameters.put(parameter3, value3);
        appendFilter(filter, parameters);
    }

    public void appendFilter(final String filter, final String parameter1, final Object value1,
                             final String parameter2, final Object value2,
                             final String parameter3, final Object value3,
                             final String parameter4, final Object value4) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        parameters.put(parameter2, value2);
        parameters.put(parameter3, value3);
        parameters.put(parameter4, value4);
        appendFilter(filter, parameters);
    }

    public void appendFilter(final String filter, final Map<String, Object> args) {
        filters.add(new FilterEntry(Filter.forName(filter), args));
    }

    public void appendFilter(final Filter filter, final String parameter1, final Object value1) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        appendFilter(filter, parameters);
    }

    public void appendFilter(final Filter filter, final String parameter1, final Object value1,
                             final String parameter2, final Object value2) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        parameters.put(parameter2, value2);
        appendFilter(filter, parameters);
    }

    public void appendFilter(final Filter filter, final String parameter1, final Object value1,
                             final String parameter2, final Object value2,
                             final String parameter3, final Object value3) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        parameters.put(parameter2, value2);
        parameters.put(parameter3, value3);
        appendFilter(filter, parameters);
    }

    public void appendFilter(final Filter filter, final String parameter1, final Object value1,
                             final String parameter2, final Object value2,
                             final String parameter3, final Object value3,
                             final String parameter4, final Object value4) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter1, value1);
        parameters.put(parameter2, value2);
        parameters.put(parameter3, value3);
        parameters.put(parameter4, value4);
        appendFilter(filter, parameters);
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
