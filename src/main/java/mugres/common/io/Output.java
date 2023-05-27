package mugres.common.io;

import mugres.common.ControlChange;
import mugres.common.InstrumentChange;
import mugres.common.Signal;
import mugres.common.Signals;
import mugres.tracker.Song;

public interface Output {
    void send(final Signal signal);
    default void send(final Signals signals) {
        for(Signal s : signals.signals())
            send(s);
    }
    void send(final InstrumentChange instrumentChange);
    void send(final ControlChange controlChange);
    void send(final Song song);
}
