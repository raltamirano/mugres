package mugres.live;

import mugres.common.Note;
import mugres.common.Pitch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class Signals {
    private final List<Signal> signals = new ArrayList<>();

    private Signals(final Signal...signals) {
        this.signals.addAll(asList(signals));
    }

    private Signals(final List<Signal> signals) {
        this.signals.addAll(signals);
    }

    public static Signals of(final Signal... signals) {
        return new Signals(signals);
    }

    public static Signals of(final List<Signal> signals) {
        return new Signals(signals);
    }

    public static Signals create() {
        return new Signals();
    }

    public void add(final Signal signal) {
        if (signal == null)
            throw new IllegalArgumentException("signal");

        signals.add(signal);
    }

    public void addAll(final Signals others) {
        others.signals.forEach(this::add);
    }

    public List<Signal> signals() {
        return Collections.unmodifiableList(signals);
    }

    public Signals noteOns() {
        return of(signals.stream().filter(Signal::isNoteOn).toArray(Signal[]::new));
    }

    public Signals noteOffs() {
        return of(signals.stream().filter(Signal::isNoteOff).toArray(Signal[]::new));
    }

    public void forEach(final Consumer<? super Signal> consumer) {
        signals.forEach(consumer);
    }

    public Signal first() {
        return signals.get(0);
    }

    public boolean isEmpty() {
        return signals.isEmpty();
    }

    public boolean isMono() {
        return signals.size() == 1;
    }

    public boolean isPoly() {
        return signals.size() > 1;
    }

    public boolean hasNote(final Note note) {
        return signals.stream().anyMatch(s -> s.pitch().note() == note);
    }

    public boolean hasPitch(final Pitch pitch) {
        return signals.stream().anyMatch(s -> s.pitch().equals(pitch));
    }

    public int size() {
        return signals.size();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for(final Signal s : signals)
            builder.append(s).append("\n");
        return builder.toString();
    }
}
