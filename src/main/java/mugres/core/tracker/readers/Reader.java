package mugres.core.tracker.readers;

import mugres.core.tracker.Song;

import java.io.IOException;
import java.io.InputStream;

public interface Reader {
    Song readSong(final InputStream inputStream)
            throws IOException;
}
