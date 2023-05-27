package mugres.common;

import mugres.common.chords.Chord;
import mugres.common.chords.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static mugres.common.Note.BASE_OCTAVE;
import static mugres.common.chords.Type.AUGMENTED_7TH;
import static mugres.common.chords.Type.DIMINISHED_7TH;
import static mugres.common.chords.Type.DOMINANT_7TH;
import static mugres.common.chords.Type.HALF_DIMINISHED;
import static mugres.common.chords.Type.MAJOR_7TH;
import static mugres.common.chords.Type.MINOR_7TH;
import static mugres.common.chords.Type.MIN_MAJ_7TH;
import static mugres.common.chords.Type.POWER_CHORD_SUS3RD;

public enum Scale {
    MAJOR("Major", Tonality.MAJOR,
            new int[]       {2, 2, 1, 2, 2, 2, 1},
            new Type[]      {MAJOR_7TH, MINOR_7TH, MINOR_7TH, MAJOR_7TH, DOMINANT_7TH, MINOR_7TH, HALF_DIMINISHED }),

    MINOR("Minor", Tonality.MINOR,
            new int[]       {2, 1, 2, 2, 1, 2, 2},
            new Type[]      {MINOR_7TH, HALF_DIMINISHED, MAJOR_7TH, MINOR_7TH, MINOR_7TH, MAJOR_7TH, DOMINANT_7TH}),

    HARMONIC_MINOR("Harmonic Minor", Tonality.MINOR,
            new int[]       {2, 1, 2, 2, 1, 3, 1},
            new Type[]      {MIN_MAJ_7TH, HALF_DIMINISHED, AUGMENTED_7TH, MINOR_7TH, DOMINANT_7TH, MAJOR_7TH, DIMINISHED_7TH }),

    MELODIC_MINOR("Melodic Minor", Tonality.MINOR,
            new int[]       {2, 1, 2, 2, 2, 2, 1},
            new Type[]      {MIN_MAJ_7TH, MINOR_7TH, AUGMENTED_7TH, DOMINANT_7TH, DOMINANT_7TH, HALF_DIMINISHED, HALF_DIMINISHED }),

    /*
    https://music.stackexchange.com/questions/69714/chords-in-a-pentatonic-scale
    With such a paucity of harmonic possibilities, you may go the route of using
    the pentatonic collection for melodic material only, and using the full
    scale collection for the harmonies. This way you get the best of both worlds:
    the sense of pentatonicism in the melody with the harmonic possibilities of a
    fuller tonal context.
    */

    MAJOR_PENTATONIC("Major Pentatonic", Tonality.MAJOR,
            new int[]       {2, 2, 3, 2, 3},
            new Type[]      {MAJOR_7TH, MINOR_7TH, MINOR_7TH, DOMINANT_7TH, MINOR_7TH }),

    MINOR_PENTATONIC("Minor Pentatonic", Tonality.MINOR,
            new int[]       {3, 2, 2, 3, 2},
            new Type[]      {MINOR_7TH, MAJOR_7TH, MINOR_7TH, MINOR_7TH, DOMINANT_7TH}),

    CHROMATIC("Chromatic", Tonality.UNDETERMINED,
            new int[]       {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            new Type[]      {POWER_CHORD_SUS3RD, POWER_CHORD_SUS3RD, POWER_CHORD_SUS3RD, POWER_CHORD_SUS3RD,
                             POWER_CHORD_SUS3RD, POWER_CHORD_SUS3RD, POWER_CHORD_SUS3RD, POWER_CHORD_SUS3RD,
                             POWER_CHORD_SUS3RD, POWER_CHORD_SUS3RD, POWER_CHORD_SUS3RD, POWER_CHORD_SUS3RD});

    private final String label;
    private final Tonality tonality;
    private final int[] intervals;
    private final Type[] chordTypesByDegree;

    Scale(final String label, final Tonality tonality, final int[] intervals, final Type[] chordTypesByDegree) {
        if (intervals.length <= 0 || intervals.length > 12)
            throw new IllegalArgumentException("Number of intervals for a scale must be 0 < n <= 12!");

        this.label = label;
        this.tonality = tonality;
        this.intervals = intervals;
        this.chordTypesByDegree = chordTypesByDegree;
    }

    public static Scale of(final String label) {
        for(Scale scale : Scale.values())
            if (scale.label.equals(label))
                return scale;

        throw new IllegalArgumentException("Invalid scale: " + label);
    }

    public String label() {
        return label;
    }

    public Tonality tonality() {
        return tonality;
    }

    public int degrees() {
        return intervals.length;
    }

    public List<Note> notes(final Note root) {
        final List<Note> notes = new ArrayList<>();

        Note next = root;
        notes.add(next);
        for (int interval : intervals) {
            next = next.up(interval);
            notes.add(next);
        }

        return notes;
    }

    public List<Pitch> pitches(final Note root) {
        return pitches(root, 1, BASE_OCTAVE);
    }

    public List<Pitch> pitches(final Note root, final int octavesToGenerate) {
        return pitches(root, octavesToGenerate, BASE_OCTAVE);
    }

    public List<Pitch> pitches(final Note root, final int octavesToGenerate, final int startingOctave) {
        final List<Pitch> pitches = new ArrayList<>();

        Pitch current = root.pitch(startingOctave);
        pitches.add(current);
        for(int i = 1; i <= octavesToGenerate; i++) {
            for (int interval : intervals) {
                current = current.up(interval);
                pitches.add(current);
            }
        }

        return pitches;
    }

    public Note noteAtDegree(final Note root, final int degree) {
        if (degree <= 0 || degree > intervals.length)
            throw new IllegalArgumentException("Interval number for scale must be 0 < n <= " + intervals.length + "!");

        return root.up(semitonesAtDegree(degree));
    }

    public Pitch pitchAtDegree(final Note scaleRoot, final int degree) {
        return pitchAtDegree(scaleRoot, degree, BASE_OCTAVE);
    }

    public Pitch pitchAtDegree(final Note scaleRoot, final int degree, final int octave) {
        return scaleRoot.pitch(octave).up(semitonesAtDegree(degree));
    }

    public Chord chordAtDegree(final Note scaleRoot, final int degree) {
        return chordAtDegree(scaleRoot, degree, BASE_OCTAVE);
    }

    public Chord chordAtDegree(final Note scaleRoot, final int degree, final int octave) {
        if (degree <= 0 || degree > intervals.length)
            throw new IllegalArgumentException("Interval number for scale must be 0 < n <= " + intervals.length + "!");

        final Note chordRoot = scaleRoot.up(semitonesAtDegree(degree));
        return Chord.of(chordRoot, chordTypesByDegree[degree - 1]);
    }

    public boolean containsPitch(final Note scaleRoot, final Note target) {
        if (scaleRoot.equals(target))
            return true;

        Pitch current = scaleRoot.pitch();
        for (int interval : intervals) {
            current = current.up(interval);
            if (current.note().equals(target))
                return  true;
        }

        return false;
    }

    public int semitonesAtDegree(final int scaleDegree) {
        if (scaleDegree <= 0 || scaleDegree > intervals.length)
            throw new IllegalArgumentException("Interval number for scale must be 0 < n <= " + intervals.length + "!");

        int semitones = 0;
        for(int i = 0; i < scaleDegree - 1; i++)
            semitones += intervals[i];

        return semitones;
    }

    public List<Pitch> harmonize(final Note scaleRoot, final int startingAtDegree,
                                 final Interval.Type intervalType, final int numberOfPitches) {
        return harmonize(scaleRoot, noteAtDegree(scaleRoot, startingAtDegree), intervalType, numberOfPitches);
    }

    public List<Pitch> harmonize(final Note scaleRoot, final Note startingAtNote,
                                 final Interval.Type intervalType, final int numberOfPitches) {
        return harmonize(scaleRoot, startingAtNote, intervalType, numberOfPitches, BASE_OCTAVE);
    }

    public List<Pitch> harmonize(final Note scaleRoot, final int startingAtDegree,
                                 final Interval.Type intervalType, final int numberOfPitches,
                                 final int baseOctave) {
        return harmonize(scaleRoot, noteAtDegree(scaleRoot, startingAtDegree), intervalType, numberOfPitches, baseOctave);
    }

    public List<Pitch> harmonize(final Note scaleRoot, final Note startingAtNote,
                                 final Interval.Type intervalType, final int numberOfPitches,
                                 final int baseOctave) {
        final List<Pitch> result = new ArrayList<>();

        final List<Pitch> pitches = pitches(scaleRoot, 4, baseOctave - 1);
        for(int i = 0; i < pitches.size(); i++) {
            Pitch pitch = pitches.get(i);
            if (pitch.note().equals(startingAtNote) && pitch.octave() == baseOctave) {
                for(int j = 0; j < numberOfPitches; j ++)
                    result.add(pitches.get(i + (j * intervalType.scaleSteps())));
                break;
            }
        }

        return result;
    }

    public static Set<Scale> byTonality(final Tonality tonality) {
        return Arrays.stream(values()).filter(s -> s.tonality() == tonality).collect(Collectors.toSet());
    }
}
