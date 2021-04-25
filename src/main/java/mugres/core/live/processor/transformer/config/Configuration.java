package mugres.core.live.processor.transformer.config;

import mugres.core.filter.Filter;
import mugres.core.live.signaler.Signaler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;

public class Configuration {
    private final List<Signaler> signalers = new ArrayList<>();
    private final List<Filter> filters = new ArrayList<>();

    public List<Signaler> getSignalers() {
        return unmodifiableList(signalers);
    }

    public List<Filter> getFilters() {
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
        filters.add(Filter.of(filter, args));
    }
}
