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
import static mugres.common.Note.BASE_OCTAVE;

/**
 * Chord
 */
public class Chord {
    private final Note root;
    private final Type type;
    private final String name;
    private final List<Interval> intervals;

    private Chord(final Note root, final String name, final List<Interval> intervals) {
        if (intervals == null || intervals.isEmpty())
            throw new IllegalArgumentException("No intervals specified!");

        this.root = root;
        this.type = Type.CUSTOM;
        this.name = name;
        this.intervals = intervals;
    }

    private Chord(final Note root, final Type type) {
        if (type == Type.CUSTOM)
            throw new IllegalArgumentException("No intervals specified!");

        this.root = root;
        this.type = type;
        this.name = null;
        this.intervals = null;
    }

    public static Chord of(final Note root, final String name, final List<Interval> intervals) {
        return new Chord(root, name, intervals);
    }

    public static Chord of(final Note root, final Type type) {
        return new Chord(root, type);
    }

    public static Chord guitarBarreChord6thString(final Note root, final Type type) {
        final List<Interval> chordIntervals = GUITAR_BARRE_CHORDS_6TH.get(type);
        if (chordIntervals == null)
            throw new IllegalArgumentException("No chord for type: " + type);
        return new Chord(root, root.label() + " Guitar Barre Chord on 6th String", chordIntervals);
    }

    public static Chord guitarBarreChord5thString(final Note root, final Type type) {
        final List<Interval> chordIntervals = GUITAR_BARRE_CHORDS_5TH.get(type);
        if (chordIntervals == null)
            throw new IllegalArgumentException("No chord for type: " + type);
        return new Chord(root, root.label() + " Guitar Barre Chord on 5th String", chordIntervals);
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
        return pitches(BASE_OCTAVE);
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
