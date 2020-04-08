package mugres.core.model;

import mugres.core.common.Pitch;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static javax.sound.midi.Sequence.PPQ;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class Events implements Iterable<Events.Event> {
    private final List<Event> eventList = new ArrayList<>();

    public Events append(final MidiMessage message, final long timestamp) {
        eventList.add(Event.of(message, timestamp));
        return this;
    }

    public Events append(final Event event) {
        eventList.add(event);
        return this;
    }

    public static Events empty() {
        return new Events();
    }

    public static Events of(final MidiMessage message,
                            final long timestamp) {
        return new Events().append(message, timestamp);
    }

    public static Events of(final Event event) {
        return new Events().append(event);
    }

    @Override
    public Iterator<Event> iterator() {
        return eventList.iterator();
    }

    @Override
    public void forEach(Consumer<? super Event> action) {
        eventList.forEach(action);
    }

    @Override
    public Spliterator<Event> spliterator() {
        return eventList.spliterator();
    }

    public int count() {
        return eventList.size();
    }

    public boolean isEmpty() {
        return eventList.isEmpty();
    }

    /** All events in this set are 'note events' (either NOTE_ON or NOTE_OFF ShortMessages) */
    public boolean noteEventsOnly() {
        return eventList.stream().allMatch(Event::isNoteEvent);
    }

    public List<NoteEvent> noteEvents() {
        return eventList.stream()
                .filter(Event::isNoteEvent)
                .map(NoteEvent.class::cast)
                .collect(Collectors.toList());
    }

    public Sequence toSequence(final int tempo) {
        try {
            final Sequence sequence = new Sequence(PPQ, DEFAULT_RESOLUTION, 1);
            if (count() > 0) {
                final Track track = sequence.getTracks()[0];
                final long startTime = eventList.get(0).timestamp;
                final double msToTicksRatio = 60000.0 / tempo / DEFAULT_RESOLUTION;
                for (int i = 0; i < eventList.size(); i++) {
                    final Event event = eventList.get(i);
                    final long tick = Math.round((event.timestamp - startTime) / msToTicksRatio);
                    track.add(new MidiEvent(event.message, tick));
                }
            }
            return sequence;
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public static final int DEFAULT_RESOLUTION = 480;

    public static class Event {
        private final long timestamp;
        private final MidiMessage message;

        private Event(final MidiMessage message, final long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }

        public static Event of(final MidiMessage message, final long timestamp) {
            if (message instanceof ShortMessage) {
                final ShortMessage sm = (ShortMessage)message;
                if (sm.getCommand() == NOTE_ON || sm.getCommand() == NOTE_OFF)
                    return new NoteEvent(sm, timestamp);
                else
                    return new Event(message, timestamp);
            } else{
                return new Event(message, timestamp);
            }
        }

        public long getTimestamp() {
            return timestamp;
        }

        public MidiMessage getMessage() {
            return message;
        }

        public boolean isNoteEvent() {
            return this instanceof NoteEvent;
        }
    }

    public static class NoteEvent extends Event {
        public NoteEvent(final ShortMessage message, long timestamp) {
            super(message, timestamp);

            if (message.getCommand() != NOTE_ON && message.getCommand() != NOTE_OFF)
                throw new IllegalArgumentException("'message' must either be a NOTE_ON or NOTE_OFF ShortMessage!");
        }

        public static NoteEvent noteOn(int channel, int note, int velocity, final long timestamp) {
            try {
                return new NoteEvent(new ShortMessage(NOTE_ON, channel, note, velocity), timestamp);
            } catch (InvalidMidiDataException e) {
                throw new RuntimeException(e);
            }
        }

        public static NoteEvent noteOff(int channel, int note, final long timestamp) {
            return noteOn(channel, note, 0, timestamp);
        }

        public boolean isNoteOn() {
            final ShortMessage sm = getShortMessage();
            return sm.getCommand() == NOTE_ON && sm.getData2() > 0;
        }

        private ShortMessage getShortMessage() {
            return (ShortMessage)getMessage();
        }

        public int getChannel() {
            return getShortMessage().getChannel();
        }

        public int getNote() {
            return getShortMessage().getData1();
        }

        public int getVelocity() {
            return getShortMessage().getData2();
        }

        public Cloner clone() {
            return new Cloner(this);
        }

        @Override
        public String toString() {
            return String.format("[%-3s]%s", isNoteOn() ? "ON" : "OFF", Pitch.of(getNote()));
        }

        public static class Cloner {
            private ShortMessage message;
            private long timestamp;

            private Cloner(final NoteEvent source) {
                this.message = source.getShortMessage();
                this.timestamp = source.getTimestamp();
            }

            public Cloner deltaTimestamp(final long delta) {
                timestamp += delta;
                return this;
            }

            public Cloner modifyTimestamp(final long newTimestamp) {
                timestamp = newTimestamp;
                return this;
            }

            public Cloner toNoteOff() {
                try {
                    message = new ShortMessage(NOTE_OFF, message.getChannel(), message.getData1(), 0);
                } catch (final InvalidMidiDataException e) {
                    throw new RuntimeException(e);
                }
                return this;
            }

            public NoteEvent get() {
                return new NoteEvent(message, timestamp);
            }
        }
    }
}
