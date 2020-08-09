package mugres.core.generation.generators;


import mugres.core.generation.generators.song.builtin.lofihiphop.LoFiHipHopSongGenerator;

import java.util.*;
import java.util.stream.Collectors;

/** Base class for all generators of musical artifacts (song, section, riff, tempo map, etc) */
public abstract class Generator<T> {
    public Generator() {
        register(this);
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract Artifact getArtifact();

    public T generate() {
        return doGenerate();
    }

    protected abstract T doGenerate();

    private static synchronized <X> void register(final Generator<X> generator) {
        final String name = generator.getName();
        if (REGISTRY.containsKey(name))
            throw new IllegalArgumentException("Already registered generator: " + name);
        REGISTRY.put(name, generator);
    }

    public static <X> Generator<X> forName(final String name) {
        return (Generator<X>) REGISTRY.get(name);
    }

    public static Set<Generator> forArtifact(final Artifact artifact) {
        return Collections.unmodifiableSet(REGISTRY.values().stream()
                .filter(g -> g.getArtifact().equals(artifact))
                .collect(Collectors.toSet()));
    }

    public static Set<Generator> allGenerators() {
        return Collections.unmodifiableSet(new HashSet<>(REGISTRY.values()));
    }

    private static final Map<String, Generator> REGISTRY = new HashMap<>();

    static {
        new LoFiHipHopSongGenerator();
    }

    /** Musical artifacts a Generator can produce. */
    public enum Artifact {
        SONG("Song");

        private final String name;

        Artifact(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
