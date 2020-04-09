package mugres.core.live;

import mugres.core.common.Context;
import mugres.core.live.Events.NoteEvent;

import static mugres.core.common.Pitch.isValidMidiNoteNumber;

public class Octave extends AbstractFilter {
    private final int octaveOffsetSemitones;

    public Octave(final int octaveOffset) {
        this.octaveOffsetSemitones = octaveOffset * 12;
    }

    @Override
    protected boolean canHandle(final Context context, final Events events) {
        return events.noteEventsOnly();
    }

    @Override
    protected Events handle(final Context context, final Events events) {
        final Events result = Events.empty();

        for (final NoteEvent e : events.noteEvents()) {
            result.append(e);
            final int targetNote = e.getNote() + octaveOffsetSemitones;
            if (isValidMidiNoteNumber(targetNote))
                if (e.isNoteOn())
                    result.append(NoteEvent.noteOn(e.getChannel(), targetNote,
                            e.getVelocity(),e.getTimestamp() + 250));
                else
                    result.append(NoteEvent.noteOff(e.getChannel(), targetNote,
                        e.getTimestamp() + 250));
        }

        return result;
    }
}
