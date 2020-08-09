package mugres.core.generation.generators.song;

import mugres.core.generation.generators.Generator;
import mugres.core.notation.Song;

import static mugres.core.generation.generators.Generator.Artifact.SONG;

public abstract class SongGenerator extends Generator<Song> {
    @Override
    public final Generator.Artifact getArtifact() {
        return SONG;
    }
}
