package mugres.core.live.processor.spirographone;

import mugres.core.common.Context;
import mugres.core.common.DataType;
import mugres.core.common.Note;
import mugres.core.common.Pitch;
import mugres.core.common.Played;
import mugres.core.common.Signal;
import mugres.core.common.io.Input;
import mugres.core.common.io.Output;
import mugres.core.live.processor.Processor;
import mugres.core.live.processor.spirographone.config.Configuration;
import mugres.core.parametrizable.Parameter;
import mugres.core.utils.Maths;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;

public class Spirographone extends Processor {
    private boolean running;
    private Thread playingThread;
    private final Configuration config;
    private Played threadLastPlayed;
    private double threadCurrentT = 0.0;
    private List<Note> notes;

    public Spirographone(final Context context,
                            final Input input,
                            final Output output,
                            final Configuration configuration) {
        super(context, input, output, null, new HashSet<>(asList(
                Parameter.of("spaceMillis", "Space between notes, in millis", DataType.INTEGER,
                        true, 200),
                Parameter.of("root", "Root note name", DataType.NOTE,
                        true, Note.E)
        )));
        this.config = configuration;
        updateNotes();
    }

    @Override
    protected void onStart() {
        startPlayingThread();
    }

    @Override
    protected void onStop() {
        stopPlayingThread();
    }

    @Override
    public void setParameterValue(final String name, final Object value) {
        switch(name) {
            case "spaceMillis":
                config.setSpaceMillis(Integer.valueOf(value.toString()) * 25);
                break;
            case "root":
                config.setRoot(Note.of((Integer.valueOf(value.toString()) % 12)));
                updateNotes();
                break;
        }
    }

    private void updateNotes() {
        notes = config.getScale().notes(config.getRoot());
    }

    @Override
    public Object getParameterValue(String name) {
        return super.getParameterValue(name);
    }

    @Override
    public Map<String, Object> getParameterValues() {
        return super.getParameterValues();
    }

    private void startPlayingThread() {
        if (playingThread != null)
            return;

        running = true;
        playingThread = new Thread(() -> {
            while(running) {
                try {
                    final double R = Math.toRadians(config.getExternalCircleRadius());
                    final double r = Math.toRadians(config.getInternalCircleRadius());
                    final double a = Math.toRadians(config.getOffsetOnInternalCircle());
                    final double MAX = (R+r) + (r+a);
                    final double MIN = -MAX;

                    double x = (R+r) * Math.cos(threadCurrentT) - (r+a) * Math.cos(((R+r)/r)*threadCurrentT);
                    double y = (R+r) * Math.sin(threadCurrentT) - (r+a) * Math.sin(((R+r)/r)*threadCurrentT);

                    final int note = notes.get((int) Maths.map(x, MIN, MAX,  0, notes.size()-1)).number();
                    final int octaves = Math.abs((int) Maths.map(y, MIN, MAX, config.getMinOctave(), config.getMaxOctave()));
                    final int pitch = note + (octaves * 12);
                    final int velocity = Math.abs((int) Maths.map(y, MIN, MAX,  0, 100));
                    final Played played = Played.of(Pitch.of(pitch), velocity > 20 ? velocity : 0);
                    if (threadLastPlayed == null) {
                        output().send(Signal.on(UUID.randomUUID(), System.currentTimeMillis(), config.getOutputChannel(), played));
                        threadLastPlayed = played;
                    } else {
                        if (threadLastPlayed.pitch().midi() != pitch) {
                            output().send(Signal.off(UUID.randomUUID(), System.currentTimeMillis(), config.getOutputChannel(), threadLastPlayed));
                            output().send(Signal.on(UUID.randomUUID(), System.currentTimeMillis(), config.getOutputChannel(), played));
                            threadLastPlayed = played;
                        }
                    }
                    threadCurrentT += config.getIterationDelta() / 100.0;
                } catch (final Exception e) {
                    e.printStackTrace();
                } finally {
                    try { Thread.sleep(config.getSpaceMillis()); } catch (Exception e2) {}
                }
            }
        });
        playingThread.setDaemon(true);
        playingThread.start();
    }

    private void stopPlayingThread() {
        if (playingThread != null) {
            running = false;
            try { playingThread.interrupt(); } catch (final Exception ignore) {}
            playingThread = null;
        }
    }
}
