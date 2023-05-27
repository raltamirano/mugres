package mugres.live.processor.drummer.config;

import javax.sound.midi.*;
import java.io.File;

public class Part {
    private String name;
    private Sequence sequence;

    Part(String name, Sequence sequence) {
        this.name = name;
        this.sequence = sequence;
    }

    public static Part fromFile(final File file) {
        try {
            final MidiFileFormat format = MidiSystem.getMidiFileFormat(file);
            final Sequence sequence = MidiSystem.getSequence(file);
            final Track track = sequence.getTracks()[(format.getType() == 0) ? 0 : 1];
            return new Part(file.getName(), toSequence(track, sequence.getDivisionType(), sequence.getResolution()));
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static Sequence toSequence(Track track, float divisionType, int resolution) {
        try {
            final Sequence sequence = new Sequence(divisionType, resolution, 1);
            final Track newTrack = sequence.getTracks()[0];
            for (int index = 0; index < track.size(); index++)
                newTrack.add(track.get(index));
            return sequence;
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public Part asClone() {
        try {
            final Sequence cloned = new Sequence(sequence.getDivisionType(), sequence.getResolution(), 1);
            final Track sourceTrack = sequence.getTracks()[0];
            for (int index = 0; index < sourceTrack.size(); index++)
                cloned.getTracks()[0].add(sourceTrack.get(index));
            return new Part(name, cloned);
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public Part[] split(final Part anotherPart) {
        if (anotherPart.sequence().getTickLength() > sequence.getTickLength())
            throw new IllegalArgumentException("Split point is longer than this part!");
        final long fixedAnotherPartLength = nearestMultiple(sequence.getResolution(), anotherPart.sequence().getTickLength());
        final long fixedTotalLength = nearestMultiple(sequence.getResolution(), sequence.getTickLength());
        final long fixedSplitLength = nearestMultiple(sequence.getResolution(), fixedTotalLength - fixedAnotherPartLength);

        try {
            final Sequence sequence1 = new Sequence(sequence.getDivisionType(), sequence.getResolution(), 1);
            final Sequence sequence2 = new Sequence(sequence.getDivisionType(), sequence.getResolution(), 1);

            final Track sourceTrack = sequence.getTracks()[0];
            for (int index = 0; index < sourceTrack.size(); index++) {
                final MidiEvent event = sourceTrack.get(index);
                if (event.getTick() < fixedSplitLength)
                    sequence1.getTracks()[0].add(event);
                else {
                    final MidiEvent newEvent = new MidiEvent(event.getMessage(), event.getTick() - fixedSplitLength);
                    sequence2.getTracks()[0].add(newEvent);
                }
            }

            setSequenceLength(sequence1, fixedSplitLength);
            setSequenceLength(sequence2, fixedAnotherPartLength);

            return new Part[] {
                    new Part(name + " 1", sequence1),
                    new Part(name + " 2", sequence2)
            };
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void fixLength() {
        final long fixedTotalLength = nearestMultiple(sequence.getResolution(), sequence.getTickLength());
        setSequenceLength(sequence, fixedTotalLength);
    }

    public static void setSequenceLength(final Sequence sequence, final long lengthInTicks) {
        sequence.getTracks()[0].get(sequence.getTracks()[0].size()-1).setTick(lengthInTicks);
    }

    private long nearestMultiple(int multiplier, long value) {
        for(int i=0; i<100000; i++)
            if (multiplier * i >= value)
                return multiplier * i;

        throw new RuntimeException(String.format("Couldn't find nearest multiple of %d for %d", multiplier, value));
    }

    public String name() {
        return this.name;
    }

    public Sequence sequence() {
        return this.sequence;
    }
}
