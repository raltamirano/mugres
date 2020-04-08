package mugres.core.common;

import java.util.ArrayList;
import java.util.List;

import static mugres.core.common.Note.BASE_OCTAVE;

public enum Scale {
    MAJOR("Major",
            new int[]       {2, 2, 1, 2, 2, 2, 1},
            new String[]    {"maj7", "min7", "min7", "maj7", "7", "min7", "min7b5" }),

    MINOR("Minor",
            new int[]       {2, 1, 2, 2, 1, 2, 2},
            new String[]    {"min7", "min7b5", "maj7", "min7", "min7", "maj7", "7"}),

    HARMONIC_MINOR("Harmonic Minor",
            new int[]       {2, 1, 2, 2, 1, 3, 1},
            new String[]    {"minmaj7", "min7b5", "maj7#5", "min7", "7", "maj7", "dim7" }),

    MELODIC_MINOR("Melodic Minor",
            new int[]       {2, 1, 2, 2, 2, 2, 1},
            new String[]    {"minmaj7", "min7", "maj7#5", "7", "7", "min7b5", "min7b5" }),

    /*
    https://music.stackexchange.com/questions/69714/chords-in-a-pentatonic-scale
    With such a paucity of harmonic possibilities, you may go the route of using
    the pentatonic collection for melodic material only, and using the full
    scale collection for the harmonies. This way you get the best of both worlds:
    the sense of pentatonicism in the melody with the harmonic possibilities of a
    fuller tonal context.
    */

    MAJOR_PENTATONIC("Major Pentatonic",
            new int[]       {2, 2, 3, 2, 3},
            new String[]    {"maj7", "min7", "min7", "7", "min7" }),

    MINOR_PENTATONIC("Minor Pentatonic",
            new int[]       {3, 2, 2, 3, 2},
            new String[]    {"min7", "maj7", "min7", "min7", "7"});

    private final String name;
    private final int[] intervals;
    private final String[] chordTypesByDegree;

    Scale(final String name, final int[] intervals, final String[] chordTypesByDegree) {
        if (intervals.length <= 0 || intervals.length > 12)
            throw new IllegalArgumentException("Number of intervals for a scale must be 0 < n <= 12!");

        this.name = name;
        this.intervals = intervals;
        this.chordTypesByDegree = chordTypesByDegree;
    }

    public String getName() {
        return name;
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

    public Pitch pitchAtDegree(final Note scaleRoot, final int degree) {
        return pitchAtDegree(scaleRoot, degree, BASE_OCTAVE);
    }

    public Pitch pitchAtDegree(final Note scaleRoot, final int degree, final int octave) {
        return scaleRoot.pitch(octave).up(semitonesAtDegree(degree));
    }

    public boolean containsPitch(final Note scaleRoot, final Note target) {
        if (scaleRoot.equals(target))
            return true;

        Pitch current = scaleRoot.pitch();
        for (int interval : intervals) {
            current = current.up(interval);
            if (current.getNote().equals(target))
                return  true;
        }

        return false;
    }

    public int semitonesAtDegree(final int scaleDegree) {
        if (scaleDegree <= 0 || scaleDegree > 12)
            throw new IllegalArgumentException("Interval number for a scale must be 0 < n <= 12!");

        int semitones = 0;
        for(int i = 0; i < scaleDegree - 1; i++)
            semitones += intervals[i];

        return semitones;
    }

    public List<Pitch> harmonize(final Note scaleRoot, final Note startingAt,
                                 final Interval.Type type, final int numberOfNotes) {
        return harmonize(scaleRoot, startingAt, type, numberOfNotes, BASE_OCTAVE);
    }

    public List<Pitch> harmonize(final Note scaleRoot, final Note startingAt,
                                 final Interval.Type type, final int numberOfNotes,
                                 final int baseOctave) {
        final List<Pitch> result = new ArrayList<>();

        final List<Pitch> pitches = pitches(scaleRoot, 2, baseOctave);
        for(int i = 0; i < pitches.size(); i++) {
            Pitch pitch = pitches.get(i);
            if (pitch.getNote().equals(startingAt)) {
                for(int j = 0; j < numberOfNotes; j ++)
                    result.add(pitches.get(i + (j * type.getScaleSteps())));
                break;
            }
        }

        return result;
    }
}
