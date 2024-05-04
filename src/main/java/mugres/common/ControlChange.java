package mugres.common;

import java.util.Objects;

import static mugres.common.MIDI.isValidChannel;
import static mugres.common.MIDI.isValidController;
import static mugres.common.MIDI.isValidCCValue;

/**
 * Control change signal
 */
public abstract class ControlChange<V> {
    private final int channel;
    private final int controller;
    private final V value;

    protected ControlChange(final int channel, final int controller, final V value) {
        this.channel = channel;
        this.controller = controller;
        this.value = value;
    }

    public int channel() {
        return channel;
    }

    public int controller() {
        return controller;
    }

    public V value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlChange that = (ControlChange) o;
        return channel == that.channel &&
                controller == that.controller &&
                value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, controller, value);
    }

    @Override
    public String toString() {
        return "ControlChange{" +
                "channel=" + channel +
                ", controller=" + controller +
                ", value=" + value +
                '}';
    }

    /**
     * MIDI CC
     */
    public static class MidiControlChange extends ControlChange<Integer> {
        private MidiControlChange(int channel, int controller, int value) {
            super(channel, controller, value);

            if (!isValidChannel(channel))
                throw new IllegalArgumentException("channel");
            if (!isValidController(controller))
                throw new IllegalArgumentException("controller");
            if (!isValidCCValue(controller))
                throw new IllegalArgumentException("value");
        }

        public static MidiControlChange of(final int channel, final int controller, final int value) {
            return new MidiControlChange(channel, controller, value);
        }
    }
}
