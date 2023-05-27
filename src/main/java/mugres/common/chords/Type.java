package mugres.common.chords;

import mugres.common.Interval;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static mugres.common.Interval.*;

/**
 * Types of chords.
 *
 * http://musictheory.alcorn.edu/Version2/theory1/Chord1.htm
 * https://strungoutfretnot.com/2010/05/07/chord-theory-1-basic-triads/
 */
public enum Type {
    CUSTOM("Custom", emptyList(), emptyList()),
    MAJOR("Major", asList("", "maj", "M"), asList(MAJOR_THIRD, MINOR_THIRD)),
    MINOR("Minor", asList("m", "-", "min"), asList(MINOR_THIRD, MAJOR_THIRD)),
    AUGMENTED("Augmented", asList("aug", "+", "+5"), asList(MAJOR_THIRD, MAJOR_THIRD)),
    DIMINISHED("Diminished", asList("dim", "º"), asList(MINOR_THIRD, MINOR_THIRD)),
    MAJOR_7TH("Major 7th", asList("maj7", "M7"), asList(MAJOR_THIRD, MINOR_THIRD, MAJOR_THIRD)),
    MINOR_7TH("Minor 7th", asList("min7","-7", "m7"), asList(MINOR_THIRD, MAJOR_THIRD, MINOR_THIRD)),
    DOMINANT_7TH("Dominant 7th", asList("7"), asList(MAJOR_THIRD, MINOR_THIRD, MINOR_THIRD)),
    HALF_DIMINISHED("Half Diminished", asList("min7b5", "-7b5"), asList(MINOR_THIRD, MINOR_THIRD, MAJOR_THIRD)),
    MIN_MAJ_7TH("Minor/Major 7th", asList("min/maj7", "min(maj)7", "mi/MA7"), asList(MINOR_THIRD, MAJOR_THIRD, MAJOR_THIRD)),
    AUGMENTED_7TH("Augmented 7th", asList("maj7#5", "maj7(#5)", "maj+7"), asList(MAJOR_THIRD, MAJOR_THIRD, MINOR_THIRD)),
    DIMINISHED_7TH("Diminished 7th", asList("dim7", "o7"), asList(MINOR_THIRD, MINOR_THIRD, MINOR_THIRD)),
    POWER_CHORD("Power Chord", asList("5"), asList(PERFECT_FIFTH)),
    POWER_CHORD_SUS3RD("Power Chord sus3", asList("5sus3"), asList(MAJOR_SECOND, MAJOR_SECOND));

    private final String name;
    private final List<String> abbreviations;
    private final List<Interval> intervals;

    Type(String name, List<String> abbreviations, List<Interval> intervals) {
        this.name = name;
        this.abbreviations = unmodifiableList(abbreviations);
        this.intervals = unmodifiableList(intervals);
    }

    public String getName() {
        return name;
    }

    public List<String> getAbbreviations() {
        return abbreviations;
    }

    public String notation() {
        return abbreviations.get(0);
    }

    public List<Interval> getIntervals() {
        return intervals;
    }


    public static Type forAbbreviation(final String abbreviation) {
        for(Type t : values())
            if (t.abbreviations.contains(abbreviation))
                return t;

        throw new IllegalArgumentException("Invalid chord type abbreviated: " + abbreviation);
    }
}
