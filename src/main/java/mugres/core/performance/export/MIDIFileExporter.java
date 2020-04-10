package mugres.core.performance.export;

import mugres.core.performance.Performance;
import mugres.core.performance.converters.ToMIDISequenceConverter;

import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

public class MIDIFileExporter implements Exporter {
    private final int midiFileType;

    public MIDIFileExporter() {
        this(MIDI_FILE_TYPE_1);
    }

    public MIDIFileExporter(final int midiFileType) {
        this.midiFileType = midiFileType;
    }

    @Override
    public void export(final Performance performance, final File outputFile)
        throws IOException {
        MidiSystem.write(CONVERTER.convert(performance), midiFileType, outputFile);
    }

    private static final int MIDI_FILE_TYPE_1 = 1;
    private static final ToMIDISequenceConverter CONVERTER = new ToMIDISequenceConverter();
}
