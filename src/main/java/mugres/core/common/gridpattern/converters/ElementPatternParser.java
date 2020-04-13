package mugres.core.common.gridpattern.converters;

import mugres.core.common.Context;
import mugres.core.common.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface ElementPatternParser<T> {
    ElementPattern<T> parse(final Context context, final Value slotValue, final String line);

    String NO_EVENT = "-";

    class ElementPattern<T> {
        private final String element;
        private final List<T> events = new ArrayList<>();

        private ElementPattern(final String element, final List<T> events) {
            this.element = element;
            this.events.addAll(events);
        }

        public static <X> ElementPattern of(final String element, final List<X> events) {
            return new ElementPattern(element, events);
        }

        public String getElement() {
            return element;
        }

        public List<T> getEvents() {
            return Collections.unmodifiableList(events);
        }
    }
}
