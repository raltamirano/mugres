package mugres.core.common;

import java.util.Objects;

/**
 * Control change signal
 */
public class ControlChange {
    private final int channel;
    private final int controller;
    private final int value;

    private ControlChange(final int channel, final int controller, final int value) {
        this.channel = channel;
        this.controller = controller;
        this.value = value;
    }

    public static ControlChange of(final int channel, final int controller, final int value) {
       return new ControlChange(channel, controller, value);
    }

    public int channel() {
        return channel;
    }

    public int controller() {
        return controller;
    }

    public int value() {
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
}
