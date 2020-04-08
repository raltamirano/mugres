package mugres.core.filters;

import mugres.core.common.Context;
import mugres.core.common.Length;
import mugres.core.common.Value;
import mugres.core.model.Events;
import mugres.core.model.Events.NoteEvent;

public class FixNoteLength extends AbstractFilter {
    private Length length;

    public FixNoteLength(final Value value) {
        this.length = value.length();
    }

    public FixNoteLength(final Length length) {
        this.length = length;
    }

    @Override
    protected boolean canHandle(final Context context, final Events events) {
        return events.noteEventsOnly();
    }

    @Override
    protected Events handle(final Context context, final Events events) {
        final Events result = Events.empty();

        final long valueInMillis = length.toMillis(context.getTempo());

        for (final NoteEvent e : events.noteEvents()) {
            if (e.isNoteOn()) {
                result.append(e);
                result.append(e.clone().toNoteOff().deltaTimestamp(valueInMillis).get());
            }
        }

        return result;
    }
}
