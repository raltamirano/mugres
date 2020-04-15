package mugres.core.live.processors.transformer.config;

import mugres.core.live.processors.transformer.filters.Filter;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private final List<Filter> filters = new ArrayList<>();

    public List<Filter> getFilters() {
        return filters;
    }

    public void appendFilter(final Filter filter) {
        filters.add(filter);
    }
}
