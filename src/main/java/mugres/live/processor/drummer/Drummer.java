package mugres.live.processor.drummer;

import mugres.common.Context;
import mugres.common.DrumKit;
import mugres.common.Played;
import mugres.common.Signal;
import mugres.common.io.Input;
import mugres.common.io.MidiOutput;
import mugres.common.io.Output;
import mugres.live.processor.Processor;
import mugres.live.processor.drummer.config.Action;
import mugres.live.processor.drummer.config.Configuration;
import mugres.live.processor.drummer.config.Groove;
import mugres.live.processor.drummer.config.Part;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import static mugres.common.MIDI.END_OF_TRACK;
import static mugres.common.Party.WellKnownParties.DRUMS;

public class Drummer extends Processor {
    private final Configuration configuration;
    private final Receiver outputPort;
    private final Sequencer sequencer;

    private Groove playingGroove;
    private Groove nextGroove;
    private Part mainSectionA;
    private Part mainSectionB;
    private Part fill;
    private boolean playingEndOfGroove = false;
    private boolean playingFill = false;
    private boolean finishing = false;
    private final Thread statusThread;

    public Drummer(final Context context,
                   final Input input,
                   final Output output,
                   final Configuration configuration) {
        super(context, input, output, null, null);

        checkSuitableOutput(output);

        this.configuration = configuration;
        // FIXME
        this.outputPort = ((MidiOutput)output).getMidiOutputPort();
        this.sequencer = createSequencer();

        this.statusThread = new Thread(this::statusUpdater);
        this.statusThread.setName("Drummer Status Updater");
        this.statusThread.setDaemon(true);
        //this.statusThread.start();
    }

    private void checkSuitableOutput(final Output output) {
        if (!(output instanceof MidiOutput))
            throw new IllegalArgumentException("Drummer can only work with MidiOutput-like ports!");
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void doProcess(final Signal signal) {
        if (!signal.isActive())
            return;

        final Action action = configuration.getAction(signal.played().pitch().midi());
        if (action != null)
            action.execute(context(),this);
    }

    private Sequencer createSequencer() {
        try {
            final Sequencer aSequencer = MidiSystem.getSequencer(false);
            aSequencer.getTransmitter().setReceiver(outputPort);
            aSequencer.addMetaEventListener(meta -> {
                if (meta.getType() == END_OF_TRACK)
                    playNextPart();
            });

            aSequencer.open();
            return aSequencer;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private void playNextPart() {
        final boolean switchGroove = this.playingGroove == null || this.nextGroove != null;

        Sequence sequenceToPlay;
        if (playingEndOfGroove) {
            if (finishing) {
                stop();
                return;
            }

            if (switchGroove)
                switchToNextGroove();
            sequenceToPlay = mainSectionA.sequence();
            playingFill = false;
        } else {
            if (switchGroove || finishing) {
                playingFill = true;
                sequenceToPlay = fill.sequence();
            } else {
                playingFill = false;
                sequenceToPlay = mainSectionB.sequence();
            }
        }

        final boolean splitMain = this.mainSectionB != null;
        if (splitMain)
            playingEndOfGroove = !playingEndOfGroove;
        else
            playingEndOfGroove = true;

        try {
            sequencer.setSequence(sequenceToPlay);
            sequencer.setTempoInBPM(playingGroove.tempo() != 0 ?
                    playingGroove.tempo() :
                    context().tempo());
            sequencer.setTickPosition(0);
            sequencer.start();
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        } finally {
            updateStatus();
        }
    }

    private void switchToNextGroove() {
        this.playingGroove = this.nextGroove != null ?
                this.nextGroove : this.playingGroove;
        this.nextGroove = null;

        if (playingGroove == null)
            return;

        fill = chooseFill();

        // Split main at fill's length
        final Part main = chooseMain();

        if (fill != null) {
            final Part[] mainSections = main.split(fill);
            mainSectionA = mainSections[0];
            mainSectionB = mainSections[1];

            // Make main's section B and the fill the same length for a more accurate loop
            Part.setSequenceLength(fill.sequence(), mainSectionB.sequence().getTickLength());
        } else {
            main.fixLength();
            mainSectionA = main;
            mainSectionB = null;
        }
    }

    private Part chooseMain() {
        if (playingGroove.mains().isEmpty())
            throw new RuntimeException("No mains defined for groove: " + playingGroove.name());

        // TODO: Honor playingGroove.getMainsMode()!
        final Part part = playingGroove.mains().get(0);
        return part; //.asClone();
    }

    private Part chooseFill() {
        if (playingGroove.fills().isEmpty())
            return null;

        // TODO: Honor playingGroove.getFillsMode()!
        final Part part = playingGroove.fills().get(0);
        return part; //.asClone();
    }

    public void hit(final DrumKit piece, final int velocity) {
        if (velocity > 0)
            output().send(Signal.on(DRUMS.party().channel(), Played.of(piece.pitch(), velocity)));
    }

    public void play(final String grooveName, final SwitchMode switchMode) {
        // Cancel request to finish playing
        finishing = false;

        if (playingGroove != null && grooveName.equals(playingGroove.name())) {
            this.nextGroove = null;
            updateStatus();
            return;
        }

        this.nextGroove = configuration.getGroove(grooveName);

        if (!sequencer.isRunning() || switchMode != SwitchMode.NORMAL) {
            playingEndOfGroove = !sequencer.isRunning() || switchMode != SwitchMode.IMMEDIATELY_FILL;
            playNextPart();
        } else {
            updateStatus();
        }
    }

    public void finish() {
        // Request to finish when it has been already
        // requested means: stop playing now!
        if (finishing)
            stop();
        else
            finishing = true;

        updateStatus();
    }

    public boolean isFinishing() {
        return finishing;
    }

    public void stop() {
        if (sequencer.isRunning())
            sequencer.stop();

        playingGroove = null;
        nextGroove = null;
        finishing = false;
        playingFill = false;

        updateStatus();
    }

    private void updateStatus() {
        final Status status = Status.of(
                sequencer.isRunning(),
                playingGroove == null ? "" : playingGroove.name(),
                fill != null,
                nextGroove == null ? "" : nextGroove.name(),
                playingFill,
                finishing);

        reportStatus(status.toString(), status);
    }

    private void statusUpdater() {
        while(true) {
            try {
                if (sequencer.isRunning())
                    updateStatus();
            } catch(final Throwable t) {
                t.printStackTrace();
            } finally {
                 try { Thread.sleep(500); } catch (final Throwable ignore) {}
            }
        }
    }

    public enum SwitchMode {
        NORMAL,
        IMMEDIATELY,
        IMMEDIATELY_FILL
    }

    public static class Status {
        private final boolean playing;
        private final String playingGroove;
        private final boolean playingGrooveHasFill;
        private final String nextGroove;
        private final boolean playingFillNow;
        private final boolean finishing;

        private Status(boolean playing, String playingGroove, boolean playingGrooveHasFill,
                      String nextGroove, boolean playingFillNow, boolean finishing) {
            this.playing = playing;
            this.playingGroove = playingGroove;
            this.playingGrooveHasFill = playingGrooveHasFill;
            this.nextGroove = nextGroove;
            this.playingFillNow = playingFillNow;
            this.finishing = finishing;
        }

        public static Status of(boolean playing, String playingGroove, boolean playingGrooveHasFill,
                                String nextGroove, boolean playingFillNow, boolean finishing) {
            return new Status(playing, playingGroove, playingGrooveHasFill, nextGroove, playingFillNow, finishing);
        }

        public boolean isPlaying() {
            return playing;
        }

        public String playingGroove() {
            return playingGroove;
        }

        public boolean playingGrooveHasFill() {
            return playingGrooveHasFill;
        }

        public String nextGroove() {
            return nextGroove;
        }

        public boolean isPlayingFillNow() {
            return playingFillNow;
        }

        public boolean isFinishing() {
            return finishing;
        }

        @Override
        public String toString() {
            return "Drummer Status{" +
                    "playing=" + playing +
                    ", playingGroove='" + playingGroove + '\'' +
                    ", playingGrooveHasFill=" + playingGrooveHasFill +
                    ", nextGroove='" + nextGroove + '\'' +
                    ", playingFillNow=" + playingFillNow +
                    ", finishing=" + finishing +
                    '}';
        }
    }
}
