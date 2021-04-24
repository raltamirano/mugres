package mugres.core.live.signaler.config;

import java.util.HashSet;
import java.util.Set;

public class Configuration {
    private Frequency frequency;
    private Set<String> tags = new HashSet<>();

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public static class Frequency {
        private Mode mode;
        private Object value;

        public Mode getMode() {
            return mode;
        }

        public void setMode(Mode mode) {
            this.mode = mode;
        }

        public <X> X getValue() {
            return (X)value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public enum Mode {
            FIXED;
        }
    }
}
