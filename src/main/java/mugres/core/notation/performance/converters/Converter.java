package mugres.core.notation.performance.converters;

import mugres.core.notation.performance.Performance;

public interface Converter<T> {
    T convert(final Performance performance);
}
