package mugres.tracker.readers;

import mugres.tracker.Song;

import java.io.IOException;
import java.io.InputStream;

public interface Reader {
    Song readSong(final InputStream inputStream)
            throws IOException;
}
