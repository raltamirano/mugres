package mugres.core.notation.performance.exporters;

import mugres.core.notation.performance.Performance;
import mugres.core.notation.performance.converters.ToMidiSequenceConverter;

import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

public class MidiFileExporter implements Exporter {
    private final int midiFileType;

    public MidiFileExporter() {
        this(MIDI_FILE_TYPE_1);
    }

    public MidiFileExporter(final int midiFileType) {
        this.midiFileType = midiFileType;
    }

    @Override
    public void export(final Performance performance, final File outputFile)
        throws IOException {
        MidiSystem.write(ToMidiSequenceConverter.getInstance().convert(performance), midiFileType, outputFile);
    }

    private static final int MIDI_FILE_TYPE_1 = 1;
}
