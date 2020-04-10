package mugres.core.performance.converters;

import mugres.core.performance.Performance;

public interface Converter<T> {
    T convert(final Performance performance);
}
