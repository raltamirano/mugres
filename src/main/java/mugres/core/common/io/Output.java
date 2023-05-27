package mugres.core.common.io;

import mugres.core.common.ControlChange;
import mugres.core.common.InstrumentChange;
import mugres.core.common.Signal;
import mugres.core.tracker.Song;

public interface Output {
    void send(final Signal signal);
    void send(final InstrumentChange instrumentChange);
    void send(final ControlChange controlChange);
    void send(final Song song);
}
