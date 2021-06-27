package mugres.core.live.signaler.config;

import java.util.HashSet;
import java.util.Set;

public class Configuration {
    private Frequency frequency;
    private Set<String> tags = new HashSet<>();
    private String duration;

    public Frequency frequency() {
        return frequency;
    }

    public void frequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Set<String> tags() {
        return tags;
    }

    public void tags(Set<String> tags) {
        this.tags = tags;
    }

    public String duration() {
        return duration;
    }

    public void duration(String duration) {
        this.duration = duration;
    }

    public static class Frequency {
        private Mode mode;
        private Object value;

        public Mode mode() {
            return mode;
        }

        public void mode(Mode mode) {
            this.mode = mode;
        }

        public <X> X value() {
            return (X)value;
        }

        public void value(Object value) {
            this.value = value;
        }

        public enum Mode {
            FIXED;
        }
    }
}
