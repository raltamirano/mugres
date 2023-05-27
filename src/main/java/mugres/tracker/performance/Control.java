package mugres.tracker.performance;

import mugres.common.Key;
import mugres.common.Length;
import mugres.common.TimeSignature;

import java.util.Objects;

public class Control {
    private final int tempo;
    private final Key key;
    private final TimeSignature timeSignature;

    private Control(int tempo, Key key, TimeSignature timeSignature) {
        this.tempo = tempo;
        this.key = key;
        this.timeSignature = timeSignature;
    }

    public static Control of(int tempo, Key key, TimeSignature timeSignature) {
        return new Control(tempo, key, timeSignature);
    }

    public int tempo() {
        return tempo;
    }

    public Key key() {
        return key;
    }

    public TimeSignature timeSignature() {
        return timeSignature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Control control = (Control) o;
        return tempo == control.tempo &&
                key == control.key &&
                timeSignature.equals(control.timeSignature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tempo, key, timeSignature);
    }

    public static class ControlEvent {
        private final Length position;
        private final Control control;

        private ControlEvent(Length position, Control control) {
            this.position = position;
            this.control = control;
        }

        public static ControlEvent of(Length position, Control control) {
            return new ControlEvent(position, control);
        }

        public Length position() {
            return position;
        }

        public Control control() {
            return control;
        }
    }
}
