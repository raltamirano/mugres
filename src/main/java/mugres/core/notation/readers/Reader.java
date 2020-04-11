package mugres.core.notation.readers;

import mugres.core.notation.Song;

import java.io.IOException;
import java.io.InputStream;

public interface Reader {
    Song readSong(final InputStream inputStream)
            throws IOException;
}
