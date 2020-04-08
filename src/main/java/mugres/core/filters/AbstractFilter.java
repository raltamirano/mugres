package mugres.core.filters;

import mugres.core.common.Context;
import mugres.core.model.Events;

import javax.sound.midi.MidiMessage;

public abstract class AbstractFilter {
    private AbstractFilter next;

    AbstractFilter() {
        this(null);
    }

    AbstractFilter(final AbstractFilter next) {
        this.next = next;
    }

    protected abstract boolean canHandle(final Context context,
                                         final Events events);

    protected abstract Events handle(final Context context,
                                   final Events events);

    public final void accept(final Context context,
                             final MidiMessage message,
                             final long timestamp) {
        accept(context, Events.of(message, timestamp));
    }

    public final void accept(final Context context,
                       final Events events) {
        Events output = doAccept(context, events);

        AbstractFilter nextFilter = next;
        while (nextFilter != null) {
            output = nextFilter.doAccept(context, output);
            nextFilter = nextFilter.next;
        }
    }

    public void setNext(final AbstractFilter filter) {
        this.next = filter;
    }

    private Events doAccept(final Context context,
                                       final Events events) {
        return canHandle(context, events) ?
                handle(context, events) : events;
    }
}
