package mugres.core.performance.export;

import mugres.core.performance.Performance;

import java.io.OutputStream;

public interface Exporter {
    void export(final Performance performance,
                final OutputStream outputStream);
}
