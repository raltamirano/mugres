package mugres.controllable;

import java.util.Objects;

public class Control {
    private final String name;
    private final String label;
    private final Type type;

    private Control(final String name, final String label, final Type type) {
        this.name = name;
        this.label = label;
        this.type = type;
    }

    public static Control of(final String name, final String label, final Type type) {
        return new Control(name, label, type);
    }

    public String name() {
        return name;
    }

    public String label() {
        return label;
    }

    public Type type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Control control = (Control) o;
        return Objects.equals(name, control.name) &&
                Objects.equals(label, control.label) &&
                type == control.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, label, type);
    }

    @Override
    public String toString() {
        return "Control{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", type=" + type +
                '}';
    }

    public enum Type {
        PARAMETER,
        ACTION
    }
}
