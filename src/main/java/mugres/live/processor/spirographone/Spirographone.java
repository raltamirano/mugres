package mugres.live.processor.spirographone;

import mugres.common.Context;
import mugres.common.DataType;
import mugres.common.Note;
import mugres.common.Pitch;
import mugres.common.Scale;
import mugres.live.Signal;
import mugres.common.io.Input;
import mugres.common.io.Output;
import mugres.live.processor.Processor;
import mugres.live.processor.spirographone.config.Configuration;
import mugres.parametrizable.Parameter;
import mugres.utils.Maths;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class Spirographone extends Processor {
    private boolean running;
    private Thread playingThread;
    private final Configuration config;
    private Pitch threadLastPitchPlayed;
    private double threadCurrentT = 0.0;
    private List<Note> notes;

    public Spirographone(final Context context,
                            final Input input,
                            final Output output,
                            final Configuration configuration) {
        super(context, input, output, null, new HashSet<>(asList(
                Parameter.of("externalCircleRadius", "External circle radius", DataType.INTEGER,
                        true, 50),
                Parameter.of("internalCircleRadius", "Internal circle radius", DataType.INTEGER,
                        true, 10),
                Parameter.of("penOffset", "Pen offset", DataType.INTEGER,
                        true, 3),
                Parameter.of("delta", "Iteration delta", DataType.INTEGER,
                        true, 5),
                Parameter.of("spaceMillis", "Space between notes, in millis", DataType.INTEGER,
                        true, 200),
                Parameter.of("minOctave", "Minimum octave", DataType.INTEGER,
                        true, 3),
                Parameter.of("maxOctave", "Maximum octave", DataType.INTEGER,
                        true, 5),
                Parameter.of("root", "Root note", DataType.NOTE,
                        true, Note.E),
                Parameter.of("scale", "Scale", DataType.SCALE,
                        true, Scale.MINOR_PENTATONIC)
        )));
        this.config = configuration;
        updateNotes();
    }

    @Override
    protected void onStart() {
        if (config.isAutoStart())
            startPlayingThread();
    }

    @Override
    protected void onStop() {
        stopPlayingThread();
    }

    @Override
    protected void doProcess(final Signal signal) {
        if (signal.isNoteOn()) {
            config.setRoot(signal.pitch().note());
            updateNotes();
            if (!running)
                startPlayingThread();
        } else {
            if (running && config.getRoot().equals(signal.pitch().note()))
                stopPlayingThread();
        }
    }

    @Override
    public void parameterValue(final String name, final Object value) {
        switch(name) {
            case "externalCircleRadius":
                config.setExternalCircleRadius(Integer.valueOf(value.toString()));
                break;
            case "internalCircleRadius":
                config.setInternalCircleRadius(Integer.valueOf(value.toString()));
                break;
            case "penOffset":
                config.setOffsetOnInternalCircle(Integer.valueOf(value.toString()));
                break;
            case "delta":
                config.setIterationDelta(Integer.valueOf(value.toString()));
                break;
            case "spaceMillis":
                config.setSpaceMillis((Integer.valueOf(value.toString()) * 2) + 1);
                break;
            case "minOctave":
                config.setMinOctave(Maths.map(Integer.valueOf(value.toString()), 0, 127, -2, 8));
                break;
            case "maxOctave":
                config.setMaxOctave(Maths.map(Integer.valueOf(value.toString()), 0, 127, -2, 8));
                break;
            case "root":
                config.setRoot(Note.of(Maths.map(Integer.valueOf(value.toString()), 0, 127, 0, 11)));
                updateNotes();
                break;
            case "scale":
                config.setScale(Scale.values()[Maths.map(Integer.valueOf(value.toString()), 0, 127,
                        0, Scale.values().length - 1)]);
                break;
        }
    }

    @Override
    public Object parameterValue(final String name) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, Object> parameterValues() {
        throw new RuntimeException("Not implemented");
    }

    private void updateNotes() {
        notes = config.getScale().notes(config.getRoot());
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
                    final Pitch pitch = Pitch.of(note + (octaves * 12));
                    final int velocity = Math.abs((int) Maths.map(y, MIN, MAX,  0, 100));
                    final int actualVelocity = velocity > 20 ? velocity : 0;
                    if (threadLastPitchPlayed == null) {
                        output().send(Signal.on(config.getOutputChannel(), pitch, actualVelocity));
                        threadLastPitchPlayed = pitch;
                    } else {
                        if (threadLastPitchPlayed.midi() != pitch.midi()) {
                            output().send(Signal.off(config.getOutputChannel(), threadLastPitchPlayed));
                            output().send(Signal.on(config.getOutputChannel(), pitch, actualVelocity));
                            threadLastPitchPlayed = pitch;
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
        running = false;

        if (playingThread != null)
            try { playingThread.interrupt(); } catch (final Exception ignore) {}

        if (threadLastPitchPlayed != null)
            output().send(Signal.off(config.getOutputChannel(), threadLastPitchPlayed));

        playingThread = null;
        threadLastPitchPlayed = null;
    }
}
