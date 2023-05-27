package mugres.core.tracker.performance.converters;

import mugres.core.tracker.performance.Performance;

public interface Converter<T> {
    T convert(final Performance performance);
}
