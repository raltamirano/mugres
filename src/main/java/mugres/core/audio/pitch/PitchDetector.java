package mugres.core.audio.pitch;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import mugres.core.common.Pitch;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.util.HashSet;
import java.util.Set;

public class PitchDetector {
    private final Mixer mixer;
    private final PitchEstimationAlgorithm algorithm;
    private final PitchDetectionHandler handler;
    private AudioDispatcher dispatcher;
    private boolean running;
    private Thread workerThread;
    private final Set<Listener> listeners = new HashSet<>();

    private PitchDetector(final Mixer mixer) {
        this(mixer, PitchEstimationAlgorithm.YIN);
    }

    private PitchDetector(final Mixer mixer, final PitchEstimationAlgorithm algorithm) {
        this.mixer = mixer;
        this.algorithm = algorithm;
        handler = createPitchHandler();
        configure();
    }

    public static PitchDetector of(final Mixer mixer) {
        return new PitchDetector(mixer);
    }

    public static PitchDetector of(final Mixer mixer, final PitchEstimationAlgorithm algorithm) {
        return new PitchDetector(mixer, algorithm);
    }

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
            throw new RuntimeException("Already running!");

        workerThread = new Thread(dispatcher,"Pitch Detector");
        workerThread.start();

        running = true;
    }

    public void stop() {
        if (!running)
            throw new RuntimeException("Not running!");

        dispatcher.stop();
        workerThread.interrupt();
        workerThread = null;

        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    private void configure() {
        try {
            float sampleRate = 44100;
            int bufferSize = 1024;
            int overlap = 0;

            final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
            final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine) mixer.getLine(dataLineInfo);
            final int numberOfSamples = bufferSize;
            line.open(format, numberOfSamples);
            line.start();
            final AudioInputStream stream = new AudioInputStream(line);
            final JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);

            // create a new dispatcher
            dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);
            // add a processor
            dispatcher.addAudioProcessor(new PitchProcessor(algorithm, sampleRate, bufferSize, handler));
        } catch (final LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private PitchDetectionHandler createPitchHandler() {
        return (pitchDetectionResult, audioEvent) -> {
            if(pitchDetectionResult.getPitch() != -1) {
                final double timeStamp = audioEvent.getTimeStamp();
                final float frequency = pitchDetectionResult.getPitch();
                final Pitch pitch = Pitch.ofFrequency(frequency);
                final float probability = pitchDetectionResult.getProbability();
                final double rms = audioEvent.getRMS() * 100;
                fireOnPitchDetected(timeStamp, frequency, pitch, probability, rms);
            }
        };
    }

    private void fireOnPitchDetected(final double timestamp, final float frequency, final Pitch pitch,
                                     final float probability, final double rms) {
        for(final Listener listener : listeners)
            listener.onPitchDetected(timestamp, frequency, pitch, probability, rms);
    }

    public interface Listener {
        void onPitchDetected(final double timestamp, final float frequency, final Pitch pitch,
                     final float probability, final double rms);
    }
}
