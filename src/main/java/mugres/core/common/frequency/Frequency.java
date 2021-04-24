package mugres.core.common.frequency;

import mugres.core.common.frequency.builtin.Fixed;

import java.util.HashSet;
import java.util.Set;

public abstract class Frequency {
    private boolean running;
    private final Set<Listener> listeners = new HashSet<>();

    public void addListener(final Listener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    public void removeListener(final Listener listener) {
        if (listener != null)
            listeners.remove(listener);
    }

    public void start() {
        if (running)
            throw new IllegalStateException("Already running!");

        onStart();
        running = true;
    }

    public void stop() {
        if (running) {
            running = false;
            onStop();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public static Fixed fixed(final long intervalInMillis) {
        return Fixed.of(intervalInMillis);
    }

    protected void fireTick() {
        listeners.forEach(Listener::tick);
    }

    protected abstract void onStart();

    protected abstract void onStop();

    public interface Listener {
        void tick();
    }
}
