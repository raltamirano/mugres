package mugres.tracker.performance.converters;

import mugres.tracker.performance.Performance;

public interface Converter<T> {
    T convert(final Performance performance);
}
