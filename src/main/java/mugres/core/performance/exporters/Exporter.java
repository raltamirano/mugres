package mugres.core.performance.exporters;

import mugres.core.performance.Performance;

import java.io.File;
import java.io.IOException;

public interface Exporter {
    default void export(final Performance performance, final String outputFilename)
            throws IOException {
        export(performance, new File(outputFilename));
    }

    void export(final Performance performance, final File outputFile)
            throws IOException;
}
