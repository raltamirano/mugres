package mugres.common.io;

import mugres.common.ControlChange;
import mugres.common.InstrumentChange;
import mugres.live.Signal;

public class DummyInput extends Input {
    @Override
    public void receive(final Signal signal) {
        System.out.println("<<< " + signal);
    }

    @Override
    public void receive(final InstrumentChange instrumentChange) {
        System.out.println("<<< " + instrumentChange);
    }

    @Override
    public void receive(final ControlChange controlChange) {
        System.out.println("<<< " + controlChange);
    }
}
