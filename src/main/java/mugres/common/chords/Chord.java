package mugres.common.chords;

import mugres.common.Interval;
import mugres.common.Note;
import mugres.common.Pitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static mugres.common.Interval.*;
import static mugres.common.Pitch.BASE_OCTAVE;

/**
 * Chord
 */
public class Chord {
    private static final int BASE_OCTAVE_STANDARD_TUNING_GUITAR = 2;
    private final Note root;
    private final Type type;
    private final String name;
    private final List<Interval> intervals;
    private final int defaultOctave;

    private Chord(final Note root, final String name, final List<Interval> intervals,
                  final int defaultOctave) {
        if (intervals == null || intervals.isEmpty())
            throw new IllegalArgumentException("No intervals specified!");

        this.root = root;
        this.type = Type.CUSTOM;
        this.name = name;
        this.intervals = intervals;
        this.defaultOctave = defaultOctave;
    }

    private Chord(final Note root, final Type type, final int defaultOctave) {
        if (type == Type.CUSTOM)
            throw new IllegalArgumentException("No intervals specified!");

        this.root = root;
        this.type = type;
        this.name = null;
        this.intervals = null;
        this.defaultOctave = defaultOctave;
    }

    public static Chord major(final Note root) {
        return new Chord(root, Type.MAJOR, BASE_OCTAVE);
    }

    public static Chord minor(final Note root) {
        return new Chord(root, Type.MINOR, BASE_OCTAVE);
    }

    public static Chord major7(final Note root) {
        return new Chord(root, Type.MAJOR_7TH, BASE_OCTAVE);
    }

    public static Chord minor7(final Note root) {
        return new Chord(root, Type.MINOR_7TH, BASE_OCTAVE);
    }

    public static Chord dominant7(final Note root) {
        return new Chord(root, Type.DOMINANT_7TH, BASE_OCTAVE);
    }


    public static Chord powerChord(final Note root) {
        return new Chord(root, Type.POWER_CHORD, BASE_OCTAVE_STANDARD_TUNING_GUITAR);
    }

    public static Chord of(final Note root, final String name, final List<Interval> intervals) {
        return new Chord(root, name, intervals, BASE_OCTAVE);
    }

    public static Chord of(final Note root, final Type type) {
        return new Chord(root, type, BASE_OCTAVE);
    }

    public static Chord guitarBarreChord6thString(final Note root, final Type type) {
        final List<Interval> chordIntervals = GUITAR_BARRE_CHORDS_6TH.get(type);
        if (chordIntervals == null)
            throw new IllegalArgumentException("No chord for type: " + type);
        return new Chord(root, root.label() + " Guitar Barre Chord on 6th String", chordIntervals,
                BASE_OCTAVE_STANDARD_TUNING_GUITAR);
    }

    public static Chord guitarBarreChord5thString(final Note root, final Type type) {
        final List<Interval> chordIntervals = GUITAR_BARRE_CHORDS_5TH.get(type);
        if (chordIntervals == null)
            throw new IllegalArgumentException("No chord for type: " + type);
        return new Chord(root, root.label() + " Guitar Barre Chord on 5th String", chordIntervals,
                BASE_OCTAVE_STANDARD_TUNING_GUITAR);
    }

    public String name() {
        return name;
    }

    public Note root() {
        return root;
    }

    public Type type() {
        return type;
    }

    public String notation() {
        return String.format("%s%s", root.label(), type.notation() );
    }

    public List<Interval> intervals() {
        return unmodifiableList(type == Type.CUSTOM ? intervals : type.getIntervals());
    }

    public List<Note> notes() {
        final List<Note> notes = new ArrayList<>();

        notes.add(root);
        notes.addAll(intervals().stream().map(Interval::semitonesFromRoot).map(root::up).collect(Collectors.toList()));

        return notes;
    }

    public List<Pitch> pitches() {
        return pitches(defaultOctave);
    }

    public List<Pitch> pitches(final int octave) {
        final List<Pitch> pitches = new ArrayList<>();

        final Pitch rootPitch = root.pitch(octave);
        pitches.add(rootPitch);

        final List<Interval> intervalList = intervals();
        int semitones = 0;
        for(int index = 0; index < intervalList.size(); index++) {
            semitones += intervalList.get(index).semitonesFromRoot();
            pitches.add(rootPitch.up(semitones));
        }

        return pitches;
    }

    private static final Map<Type, List<Interval>> GUITAR_BARRE_CHORDS_6TH = new HashMap() {{
        put(Type.MAJOR, asList(PERFECT_FIFTH, PERFECT_FOURTH, MAJOR_THIRD, MINOR_THIRD, PERFECT_FOURTH));
        put(Type.MINOR, asList(PERFECT_FIFTH, PERFECT_FOURTH, MINOR_THIRD, MAJOR_THIRD, PERFECT_FOURTH));
    }};

    private static final Map<Type, List<Interval>> GUITAR_BARRE_CHORDS_5TH = new HashMap() {{
        put(Type.MAJOR, asList(PERFECT_FIFTH, PERFECT_FOURTH, MAJOR_THIRD, MINOR_THIRD));
        put(Type.MINOR, asList(PERFECT_FIFTH, PERFECT_FOURTH, MINOR_THIRD, MAJOR_THIRD));
    }};
}
