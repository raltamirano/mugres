package mugres.core.live.processors.drummer;

import mugres.core.common.Context;
import mugres.core.common.Signal;
import mugres.core.common.io.Output;
import mugres.core.live.processors.drummer.config.Action;
import mugres.core.live.processors.drummer.config.DrumPattern;
import mugres.core.live.processors.drummer.config.Part;
import mugres.core.common.io.Input;
import mugres.core.live.processors.Processor;
import mugres.core.live.processors.drummer.config.Configuration;

import javax.sound.midi.*;

public class Drummer extends Processor {
    private final Configuration configuration;
    private final Receiver outputPort;
    private final Sequencer sequencer;

    private DrumPattern playingPattern;
    private DrumPattern nextPattern;
    private Part grooveSectionA;
    private Part grooveSectionB;
    private Part fill;
    private boolean playingEndOfPattern = false;
    private boolean finishing = false;

    private long lastEventTimestamp = Long.MIN_VALUE;

    public Drummer(final Context context,
                   final Input input,
                   final Output output,
                   final Configuration configuration,
                   final Receiver outputPort) {
        super(context, input, output);

        this.configuration = configuration;
        this.outputPort = outputPort;
        this.sequencer = createSequencer();
    }

    @Override
    protected void doProcess(final Signal signal) {
        if (!signal.isActive())
            return;

        /** Act upon a signal being activated. */
        lastEventTimestamp = System.currentTimeMillis();

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
        final boolean switchPattern = this.playingPattern == null || this.nextPattern != null;

        Sequence sequenceToPlay;
        if (playingEndOfPattern) {
            if (finishing) {
                stop();
                return;
            }

            if (switchPattern)
                switchToNextPattern();
            sequenceToPlay = grooveSectionA.getSequence();
        } else {
            sequenceToPlay = switchPattern || finishing ? fill.getSequence() : grooveSectionB.getSequence();
        }

        final boolean splitGroove = this.grooveSectionB != null;
        if (splitGroove)
            playingEndOfPattern = !playingEndOfPattern;
        else
            playingEndOfPattern = true;

        try {
            sequencer.setSequence(sequenceToPlay);
            sequencer.setTempoInBPM(playingPattern.getTempo() != 0 ?
                    playingPattern.getTempo() :
                    configuration.getContext().getTempo());
            sequencer.setTickPosition(0);
            sequencer.start();
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    private void switchToNextPattern() {
        this.playingPattern = this.nextPattern != null ?
                this.nextPattern : this.playingPattern;
        this.nextPattern = null;

        if (playingPattern == null)
            return;

        fill = chooseFill();

        // Split groove at fill's length
        final Part groove = chooseGroove();

        if (fill != null) {
            final Part[] grooveSections = groove.split(fill);
            grooveSectionA = grooveSections[0];
            grooveSectionB = grooveSections[1];

            // Make groove's section B and the fill the same length for a more accurate loop
            Part.setSequenceLength(fill.getSequence(), grooveSectionB.getSequence().getTickLength());
        } else {
            groove.fixLength();
            grooveSectionA = groove;
            grooveSectionB = null;
        }
    }

    private Part chooseGroove() {
        if (playingPattern.getGrooves().isEmpty())
            throw new RuntimeException("No grooves defined for pattern: " + playingPattern.getName());

        // TODO: Honor playingPattern.getGroovesMode()!
        final Part part = playingPattern.getGrooves().get(0);
        return part; //.asClone();
    }

    private Part chooseFill() {
        if (playingPattern.getFills().isEmpty())
            return null;

        // TODO: Honor playingPattern.getFillsMode()!
        final Part part = playingPattern.getFills().get(0);
        return part; //.asClone();
    }

    public void play(final String pattern) {
        // Cancel request to finish playing
        finishing = false;

        if (playingPattern != null && pattern.equals(playingPattern.getName())) {
            this.nextPattern = null;
            return;
        }

        this.nextPattern = configuration.getPattern(pattern);

        if (!sequencer.isRunning()) {
            playingEndOfPattern = true;
            playNextPart();
        }
    }

    public void finish() {
        // Request to finish when it has been already
        // requested means: stop playing now!
        if (finishing)
            stop();
        else
            finishing = true;
    }

    public boolean isFinishing() {
        return finishing;
    }

    public void stop() {
        if (sequencer.isRunning())
            sequencer.stop();

        playingPattern = null;
        nextPattern = null;
        finishing = false;
    }

    private static final int END_OF_TRACK = 0x2F;
}
