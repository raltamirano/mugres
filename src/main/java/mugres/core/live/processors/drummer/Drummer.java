package mugres.core.live.processors.drummer;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.io.Input;
import mugres.core.common.io.Output;
import mugres.core.live.processors.Processor;
import mugres.core.live.processors.drummer.config.Action;
import mugres.core.live.processors.drummer.config.Configuration;
import mugres.core.live.processors.drummer.config.Groove;
import mugres.core.live.processors.drummer.config.Part;

import javax.sound.midi.*;

import static java.lang.String.format;

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
    private boolean finishing = false;
    private final Thread statusThread;

    public Drummer(final Context context,
                   final Input input,
                   final Output output,
                   final Configuration configuration,
                   final Receiver outputPort) {
        super(context, input, output);

        this.configuration = configuration;
        this.outputPort = outputPort;
        this.sequencer = createSequencer();

        this.statusThread = new Thread(this::statusUpdater);
        this.statusThread.setName("Drummer Status Updater");
        this.statusThread.setDaemon(true);
        //this.statusThread.start();
    }

    @Override
    protected void doProcess(final Signal signal) {
        if (!signal.isActive())
            return;

        final Action action = configuration.getAction(signal.getPlayed().getPitch().getMidi());
        if (action != null)
            action.execute(getContext(),this);
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
        final boolean switchPattern = this.playingGroove == null || this.nextGroove != null;

        Sequence sequenceToPlay;
        if (playingEndOfGroove) {
            if (finishing) {
                stop();
                return;
            }

            if (switchPattern)
                switchToNextPattern();
            sequenceToPlay = mainSectionA.getSequence();
        } else {
            sequenceToPlay = switchPattern || finishing ? fill.getSequence() : mainSectionB.getSequence();
        }

        final boolean splitMain = this.mainSectionB != null;
        if (splitMain)
            playingEndOfGroove = !playingEndOfGroove;
        else
            playingEndOfGroove = true;

        try {
            sequencer.setSequence(sequenceToPlay);
            sequencer.setTempoInBPM(playingGroove.getTempo() != 0 ?
                    playingGroove.getTempo() :
                    getContext().getTempo());
            sequencer.setTickPosition(0);
            sequencer.start();
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        } finally {
            updateStatus();
        }
    }

    private void switchToNextPattern() {
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
            Part.setSequenceLength(fill.getSequence(), mainSectionB.getSequence().getTickLength());
        } else {
            main.fixLength();
            mainSectionA = main;
            mainSectionB = null;
        }
    }

    private Part chooseMain() {
        if (playingGroove.getMains().isEmpty())
            throw new RuntimeException("No mains defined for groove: " + playingGroove.getName());

        // TODO: Honor playingPattern.getMainsMode()!
        final Part part = playingGroove.getMains().get(0);
        return part; //.asClone();
    }

    private Part chooseFill() {
        if (playingGroove.getFills().isEmpty())
            return null;

        // TODO: Honor playingPattern.getFillsMode()!
        final Part part = playingGroove.getFills().get(0);
        return part; //.asClone();
    }

    public void play(final String pattern) {
        // Cancel request to finish playing
        finishing = false;

        if (playingGroove != null && pattern.equals(playingGroove.getName())) {
            this.nextGroove = null;
            updateStatus();
            return;
        }

        this.nextGroove = configuration.getGroove(pattern);

        if (!sequencer.isRunning()) {
            playingEndOfGroove = true;
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

        updateStatus();
    }

    private void updateStatus() {
        final StringBuilder builder = new StringBuilder();

        if (sequencer.isRunning()) {
            builder.append(format("Playing groove %s (fill=%s)%n",
                    playingGroove.getName(), fill == null ? "No" : "Yes"));
            builder.append(format("Next groove: %s%n",
                    nextGroove == null ? "None" : nextGroove.getName()));
            builder.append(format("Finishing: %s%n",
                    finishing ? "Yes" : "No"));
        } else {
            builder.append("Stopped");
        }

        reportStatus(builder.toString());
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

    private static final int END_OF_TRACK = 0x2F;
}
