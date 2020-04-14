package mugres.core.ui;

import java.io.File;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class FileWatcher extends Thread {
    private final File file;
    private AtomicBoolean stop = new AtomicBoolean(false);
    private Consumer<File> action;

    public FileWatcher(final File file, final Consumer<File> action) {
        this.file = file;
        this.action = action;
    }

    public boolean isStopped() { return stop.get(); }
    public void stopThread() { stop.set(true); }

    private void doOnChange() {
        action.accept(file);
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            Path path = file.toPath().getParent();
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStopped()) {
                WatchKey key;
                try { key = watcher.poll(25, TimeUnit.MILLISECONDS); }
                catch (InterruptedException e) { return; }
                if (key == null) { Thread.yield(); continue; }

                List<WatchEvent<?>> watchEvents = key.pollEvents();
                for (WatchEvent<?> event : watchEvents) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        Thread.yield();
                        continue;
                    } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
                            && filename.toString().equals(file.getName())) {
                        doOnChange();
                    }
                    boolean valid = key.reset();
                    if (!valid) { break; }
                }
                Thread.yield();
            }
        } catch (Throwable e) {
            // Log or rethrow the error
        }
    }
}