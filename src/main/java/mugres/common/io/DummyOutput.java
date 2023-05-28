package mugres.common.io;

import mugres.common.ControlChange;
import mugres.common.InstrumentChange;
import mugres.live.Signal;
import mugres.tracker.Song;

public class DummyOutput extends Output {
    @Override
    public void send(final Signal signal) {
        System.out.println(">>> " + signal);
    }

    @Override
    public void send(final InstrumentChange instrumentChange) {
        System.out.println(">>> " + instrumentChange);
    }

    @Override
    public void send(final ControlChange controlChange) {
        System.out.println(">>> " + controlChange);
    }

    @Override
    public void send(final Song song) {
        System.out.println(">>> " + song);
    }
}
