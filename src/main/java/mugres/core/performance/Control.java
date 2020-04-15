package mugres.core.performance;

import mugres.core.common.Key;
import mugres.core.common.Length;
import mugres.core.common.TimeSignature;

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

    public int getTempo() {
        return tempo;
    }

    public Key getKey() {
        return key;
    }

    public TimeSignature getTimeSignature() {
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

        public Length getPosition() {
            return position;
        }

        public Control getControl() {
            return control;
        }
    }
}