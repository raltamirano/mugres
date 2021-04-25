package mugres.core.common.frequency.builtin;

import mugres.core.common.Value;
import mugres.core.common.frequency.Frequency;

/**
 * Fixed interval frequency.
 *
 * @implNote Time drift is still an issue. */
public class Fixed extends Frequency {
    private final long millis;
    private Thread worker;

    private Fixed(final long millis) {
        if (millis <= 0)
            throw new IllegalArgumentException("millis");

        this.millis = millis;
        worker = createWorkerThread();
        worker.setDaemon(true);
    }

    public static Fixed of(final long millis) {
        return new Fixed(millis);
    }

    public static Fixed of(final Value value, final int tempo) {
        return new Fixed(value.length().toMillis(tempo));
    }

    @Override
    protected void onStart() {
        worker.start();
    }

    @Override
    protected void onStop() {
        try {
            worker.interrupt();
            worker.join();
        } catch (final Throwable ignore) {
        }
    }

    private Thread createWorkerThread() {
        return new Thread(() -> {
            while(isRunning())
                try {
                    fireTick();
                } catch (final Throwable ignore) {
                    // Do nothing
                } finally {
                    try { Thread.sleep(millis); } catch (final Throwable ignore) { }
                }
        });
    }
}
