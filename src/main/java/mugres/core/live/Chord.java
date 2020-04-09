package mugres.core.live;

import mugres.core.common.Context;
import mugres.core.common.Key;
import mugres.core.common.Pitch;
import mugres.core.live.Events.NoteEvent;

import java.util.List;

public class Chord extends AbstractFilter {
    @Override
    protected boolean canHandle(final Context context, final Events events) {
        return events.noteEventsOnly();
    }

    @Override
    protected Events handle(final Context context, final Events events) {
        final Events result = Events.empty();
        final Key key = context.getKey();

        for (final NoteEvent e : events.noteEvents()) {
            final List<Pitch> chordPitches = key.chord(Pitch.of(e.getNote()));
            if (chordPitches.isEmpty())
                result.append(e);
            else
                if (e.isNoteOn())
                    for (final Pitch p : chordPitches)
                        result.append(NoteEvent.noteOn(e.getChannel(), p.getMidi(), e.getVelocity(), e.getTimestamp()));
                else
                    for (final Pitch p : chordPitches)
                        result.append(NoteEvent.noteOff(e.getChannel(), p.getMidi(), e.getTimestamp()));
        }

        return result;
    }
}
