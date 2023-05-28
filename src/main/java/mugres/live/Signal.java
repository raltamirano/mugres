package mugres.live;

import mugres.common.Pitch;
import mugres.tracker.Event;

import java.util.*;

/** Live signal. Analog to {@link Event} in the tracker world. */
public class Signal implements Cloneable {
    private final int channel;
    private final Pitch pitch;
    private final int velocity;
    private Map<String, Object> attributes;
    private final Object attributesSyncObject = new Object();

    private Signal(final int channel, final Pitch pitch, final int velocity) {
        this.channel = channel;
        this.pitch = pitch;
        this.velocity = velocity;
    }

    public static Signal of(final int packed) {
        throw new RuntimeException("Not implemented!");
    }

    public static Signal on(final int channel, final Pitch pitch, final int velocity) {
        return of(channel, pitch, velocity);
    }

    public static Signal off(final int channel, final Pitch pitch) {
        return of(channel, pitch, 0);
    }

    public static Signal of(final int channel, final Pitch pitch, final int velocity) {
        return of(channel, pitch, velocity, null);
    }

    public static Signal of(final int channel, final Pitch pitch, final int velocity,
                            final Map<String, Object> attributes) {
        final Signal signal = new Signal(channel, pitch, velocity);
        if (attributes != null)
            for(String key : attributes.keySet())
                signal.setAttribute(key, attributes.get(key));
        return signal;
    }

    /** Pitch + Channel identification */
    public int discriminator() {
        return pitch.midi() * 100 + channel;
    }

    public int channel() {
        return channel;
    }

    public Pitch pitch() {
        return pitch;
    }

    public int velocity() {
        return velocity;
    }

    public boolean isNoteOn() {
        return velocity > 0;
    }

    public boolean isNoteOff() {
        return !isNoteOn();
    }

    public Signal modifiedPitch(final Pitch newPitch) {
        synchronized (attributesSyncObject) {
            return of(channel, newPitch, velocity, attributes);
        }
    }

    public Signal modifiedVelocity(final int newVelocity) {
        synchronized (attributesSyncObject) {
            return of(channel, pitch, newVelocity, attributes);
        }
    }

    public Signal modifiedChannel(final int newChannel) {
        synchronized (attributesSyncObject) {
            return of(newChannel, pitch, velocity, attributes);
        }
    }

    public Signal toNoteOn(final int velocity) {
        if (isNoteOn())
            return this;
        else
            synchronized (attributesSyncObject) {
                return of(channel, pitch, velocity, attributes);
            }
    }

    public Signal toNoteOff() {
        if (isNoteOn())
            synchronized (attributesSyncObject) {
                return of(channel, pitch, 0, attributes);
            }
        else
            return this;
    }

    @Override
    public Signal clone() {
        final Signal clone = of(channel, pitch, velocity);
        synchronized (attributesSyncObject) {
            if (attributes != null)
                for(final String key : attributes.keySet())
                    clone.setAttribute(key, attributes.get(key));
        }
        return clone;
    }

    public Map<String, Object> attributes() {
        synchronized (attributesSyncObject) {
            return  attributes == null ? Collections.emptyMap() : Collections.unmodifiableMap(attributes);
        }
    }

    public void setAttribute(final String name, final Object value) {
        synchronized (attributesSyncObject) {
            if (attributes == null)
                attributes = new HashMap<>();
            attributes.put(name, value);
        }
    }

    public <X> X getAttribute(final String name) {
        synchronized (attributesSyncObject) {
            return attributes == null ?
                null :
                    (X)attributes.get(name);
        }
    }

    public void removeAttribute(final String name) {
        synchronized (attributesSyncObject) {
            if (attributes != null)
                attributes.remove(name);
        }
    }

    public void addTag(final String tag) {
        if (tag == null || tag.trim().isEmpty())
            return;

        synchronized (attributesSyncObject) {
            Set<String> tags = getAttribute(TAGS);
            if (tags == null) {
                tags = new HashSet<>();
                setAttribute(TAGS, tags);
            }
            tags.add(tag);
        }
    }

    public boolean hasTag(final String tag) {
        if (tag == null || tag.trim().isEmpty())
            return false;

        final Set<String> tags = getAttribute(TAGS);
        return tags == null ? false : tags.contains(tag);
    }

    public int pack() {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public String toString() {
        final Object tags = getAttribute(TAGS);
        return String.format("%s (%03d) [%d]Tags=[%s]",
                pitch, velocity, channel, tags == null ? "" : tags);
    }

    /** Tags attribute name */
    public static final String TAGS = "tags";
}
