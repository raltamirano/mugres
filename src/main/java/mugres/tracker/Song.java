package mugres.tracker;

import mugres.MUGRES;
import mugres.common.Context;
import mugres.common.DataType;
import mugres.common.Instrument;
import mugres.common.Key;
import mugres.common.TimeSignature;
import mugres.function.Call;
import mugres.parametrizable.Parameter;
import mugres.parametrizable.ParametrizableSupport;
import mugres.tracker.performance.Performance;
import mugres.tracker.performance.Performer;
import mugres.tracker.performance.converters.ToMidiSequenceConverter;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import static mugres.utils.ObjectMapping.mapToPojo;

/** MUGRES internal representation of a song. */
public class Song extends TrackerElement {
    public static final Object MIN_TEMPO = 1;
    public static final Object MAX_TEMPO = 10000;
    public static final int MIN_BEAT_SUBDIVISION = 1;
    public static final int MAX_BEAT_SUBDIVISION = 128;
    public static final String PATTERNS = "patterns";
    public static final String TRACKS = "tracks";
    public static final String ARRANGEMENT = "arrangement";

    private final Map<String, Object> metadata;
    private final Set<Pattern> patterns = new TreeSet<>();
    private final Set<Track> tracks = new HashSet<>();
    private final Arrangement arrangement;

    private static final Set<Parameter> PARAMETERS;

    static {
        PARAMETERS = new HashSet<>();

        PARAMETERS.add(Parameter.of("name", "Name", 1, "Name",
                DataType.TEXT, false, "Untitled"));
        PARAMETERS.add(Parameter.of(Context.TEMPO, "BPM", 2, "BPM",
                DataType.INTEGER, false,120, MIN_TEMPO, MAX_TEMPO, false));
        PARAMETERS.add(Parameter.of(Context.KEY, "Key", 3, "Key",
                DataType.KEY, false, Key.C));
        PARAMETERS.add(Parameter.of(Context.TIME_SIGNATURE, "Time Signature", 4,
                "Time Signature", DataType.TIME_SIGNATURE, false, TimeSignature.TS44));
    }

    private Song(final UUID id, final String name, final Context context, final Map<String, Object> metadata) {
        super(id, name, context);

        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();

        this.arrangement = Arrangement.of(this);
        this.arrangement.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals(Arrangement.ENTRIES))
                propertyChangeSupport().firePropertyChange(ARRANGEMENT, null, arrangement);
        });
    }

    @Override
    protected ParametrizableSupport createParametrizableSupport() {
        final ParametrizableSupport parametrizableSupport = ParametrizableSupport.forTarget(PARAMETERS, this);
        parametrizableSupport.setCustomHasParameterValueLogic(p ->
                Context.MAIN_PROPERTIES.contains(p) ? context().overrides(p) : null);
        return parametrizableSupport;
    }

    public static Song of(final String name, final Context context) {
        return new Song(UUID.randomUUID(), name, context, null);
    }

    public static Song of(final String name, final Context context, final Map<String, Object> metadata) {
        return new Song(UUID.randomUUID(), name, context, metadata);
    }

    public static Song of(final Call<List<Event>> call) {
        return of(Context.basicContext(), Track.WellKnownTracks.PIANO, call);
    }

    public static Song of(final Track.WellKnownTracks functionCallsTrack,
                          final Call<List<Event>> call) {
        return of(Context.basicContext(), functionCallsTrack.track(), call);
    }

    public static Song of(final Context functionCallsContext,
                          final Call<List<Event>> call) {
        return of(functionCallsContext, Track.WellKnownTracks.PIANO.track(), call);
    }

    public static Song of(final Track functionCallsTrack,
                          final Call<List<Event>> call) {
        return of(Context.basicContext(), functionCallsTrack, call);
    }

    public static Song of(final Context functionCallsContext,
                          final Track.WellKnownTracks functionCallsTrack,
                          final Call<List<Event>> call) {
            return of(functionCallsContext, functionCallsTrack.track(), call);
    }

    public static Song of(final Context functionCallsContext,
                          final Track functionCallsTrack,
                          final Call<List<Event>> call) {
        final Song functionCallSong = new Song(UUID.randomUUID(),"Untitled", functionCallsContext, null);
        final Pattern pattern = functionCallSong.createPattern("A", call.getLengthInMeasures());
        pattern.addPart(functionCallsTrack, call);
        functionCallSong.arrangement.append(pattern, 1);
        return functionCallSong;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public <X> X metadataAs(final Class<X> clazz) {
        return mapToPojo(metadata, clazz);
    }

    public <X> X metadataAs(final String key, final Class<X> clazz) {
        return mapToPojo((Map<String, Object>) metadata.get(key), clazz);
    }

    public Pattern createPattern(final int measures) {
        return createPattern(createPatternName(), measures);
    }

    public Pattern createPattern(final String patternName, final int measures) {
        if (pattern(patternName) != null)
            throw new IllegalArgumentException(String.format("Pattern '%s' already exists!", patternName));

        final Pattern pattern = Pattern.of(UUID.randomUUID(),this, patternName, measures);
        patterns.add(pattern);
        propertyChangeSupport().firePropertyChange(PATTERNS, null, patterns());
        return pattern;
    }

    public void deletePattern(final Pattern pattern) {
        final Pattern toRemove = pattern(pattern.id());
        if (toRemove == null)
            throw new IllegalArgumentException(String.format("Invalid pattern: ", pattern.name()));

        patterns.remove(toRemove);
        propertyChangeSupport().firePropertyChange(PATTERNS, null, patterns());

        if (arrangement.removeAllForPattern(toRemove))
            propertyChangeSupport().firePropertyChange(ARRANGEMENT, null, arrangement);
    }

    /** Creates a song that contains a single pattern from this song. That pattern will be arranged
     * to be repeated once. */
    public Song createPatternSong(final String patternName) {
        final Pattern pattern = pattern(patternName);
        if (pattern == null)
            throw new IllegalArgumentException("Unknown pattern: " + patternName);

        final Song patternSong = Song.of(patternName, context());
        patternSong.tracks.addAll(tracks);
        patternSong.patterns.add(pattern);
        patternSong.arrangement.append(pattern, 1);

        return patternSong;
    }

    public void addTrack(final Track track) {
        if (track == null)
            throw new IllegalArgumentException("track");
        if (tracks.contains(track))
            throw new IllegalArgumentException("track with same name already exists: " + track.name());

        tracks.add(track);
        propertyChangeSupport().firePropertyChange(TRACKS, null, tracks());
    }

    public Track createTrack(final Instrument instrument) {
        if (instrument == null)
            throw new IllegalArgumentException("instrument");

        final Track track = Track.of(UUID.randomUUID(), createTrackName(), instrument);
        tracks.add(track);
        propertyChangeSupport().firePropertyChange(TRACKS, null, tracks());

        return track;
    }

    public void removeTrack(final Track track) {
        if (track == null)
            throw new IllegalArgumentException("track");
        if (!tracks.contains(track))
            return;

        if(tracks.remove(track)) {
            patterns.forEach(p -> p.removePartsFor(track));
            propertyChangeSupport().firePropertyChange(TRACKS, null, tracks());
        }
    }

    public Set<Pattern> patterns() {
        return Collections.unmodifiableSet(patterns);
    }

    public Pattern pattern(final UUID id) {
        for(Pattern pattern : patterns)
            if (pattern.id().equals(id))
                return pattern;

        return null;
    }

    public Pattern pattern(final String name) {
        for(Pattern pattern : patterns)
            if (pattern.name().equals(name))
                return pattern;

        return null;
    }

    public Set<Track> tracks() {
        return Collections.unmodifiableSet(tracks);
    }

    public Track track(final UUID id) {
        for(Track track : tracks)
            if (track.id().equals(id))
                return track;

        return null;
    }

    public Track track(final String name) {
        for(Track track : tracks)
            if (track.name().equals(name))
                return track;

        return null;
    }


    public Arrangement arrangement() {
        return arrangement;
    }

    public Sequence toMidiSequence() {
        final Performance performance = Performer.perform(this);
        return ToMidiSequenceConverter.getInstance().convert(performance);
    }

    public void saveToMidiFile(final File outputFile) throws IOException {
        if (outputFile == null)
            throw new IllegalArgumentException("outputFile");

        final Sequence sequence = toMidiSequence();
        int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
        if (fileTypes.length > 0) {
            if (MidiSystem.write(sequence, fileTypes[0], outputFile) == -1)
                throw new IOException("Problems writing to output file");
        } else {
            throw new RuntimeException("Can't save to MIDI outputFile! (invalid file type)");
        }
    }

    public void play() {
        MUGRES.output().send(this);
    }


    private String createPatternName() {
        for(int index=0; index<Integer.MAX_VALUE; index++) {
            final String candidate = String.format("Pattern %04d", index);
            if (pattern(candidate) == null)
                return candidate;
        }

        throw new IllegalStateException("Couldn't generate unique pattern name!");
    }

    private String createTrackName() {
        for(int index=0; index<Integer.MAX_VALUE; index++) {
            final String candidate = String.format("Track %04d", index);
            if (track(candidate) == null)
                return candidate;
        }

        throw new IllegalStateException("Couldn't generate unique track name!");
    }

    @Override
    public String toString() {
        return "Song" +
                "\n{" +
                "\n\tid=" + id() +
                ",\n\tname='" + name() + '\'' +
                ",\n\tcontext=" + context() +
                ",\n\tmetadata=" + metadata +
                ",\n\tpatterns=" + patterns +
                ",\n\ttracks=" + tracks +
                ",\n\tarrangement=" + arrangement +
                "\n}";
    }
}
