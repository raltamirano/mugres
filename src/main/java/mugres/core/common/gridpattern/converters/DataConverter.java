package mugres.core.common.gridpattern.converters;

import java.util.List;

public interface DataConverter<T> {
    T convert(final String data);
    List<T> tokenize(final String line);
}
