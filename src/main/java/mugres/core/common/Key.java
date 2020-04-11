package mugres.core.common;

import java.util.List;

import static mugres.core.common.Key.Mode.MAJOR;
import static mugres.core.common.Key.Mode.MINOR;

public enum Key {
    C("C", Note.C, MAJOR),
    CS("C#", Note.CS, MAJOR),
    D("D", Note.D, MAJOR),
    DS("D#", Note.DS, MAJOR),
    E("E", Note.E, MAJOR),
    F("F", Note.F, MAJOR),
    FS("F#", Note.FS, MAJOR),
    G("G", Note.G, MAJOR),
    GS("G#", Note.GS, MAJOR),
    A("A", Note.A, MAJOR),
    AS("A#", Note.AS, MAJOR),
    B("B", Note.B,  MAJOR),

    Cm("C min", Note.C, MINOR),
    CSm("C# min", Note.CS, MINOR),
    Dm("D min", Note.D, MINOR),
    DSm("D# min", Note.DS, MINOR),
    Em("E min", Note.E, MINOR),
    Fm("F min", Note.F, MINOR),
    FSm("F# min", Note.FS, MINOR),
    Gm("G min", Note.G, MINOR),
    GSm("G# min", Note.GS, MINOR),
    Am("A min", Note.A, MINOR),
    ASm("A# min", Note.AS, MINOR),
    Bm("B min", Note.B,  MINOR);

    private final String label;
    private final Note root;
    private final Mode mode;

    Key(final String label, final Note root, final Mode mode) {
        this.label = label;
        this.root = root;
        this.mode = mode;
    }

    public String label() {
        return label;
    }

    public Note getRoot() {
        return root;
    }

    public Mode getMode() {
        return mode;
    }

    public Scale defaultScale() {
        return mode == MAJOR ? Scale.MAJOR : Scale.MINOR;
    }

    public List<Pitch> chord(final Pitch rootPitch) {
        return chord(rootPitch, 3);
    }

    public List<Pitch> chord(final Pitch rootPitch, final int numberOfNotes) {
        return chord(rootPitch.getNote(), numberOfNotes, rootPitch.getOctave());
    }

    public List<Pitch> chord(final Note chordRoot, final int numberOfNotes, final int baseOctave) {
        return defaultScale().harmonize(root, chordRoot, Interval.Type.THIRD, numberOfNotes, baseOctave);
    }

    public static Key fromLabel(String label) {
        for(Key key : values())
            if (key.label.equals(label))
                return key;
        throw new IllegalArgumentException("Invalid Key label: " + label);
    }

    enum Mode {
        MAJOR,
        MINOR
    }
}
