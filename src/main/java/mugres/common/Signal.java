package mugres.common;

import java.util.*;

/** Live signal. Analog to {@link mugres.common.Event} in the notated world. */
public class Signal implements Cloneable {
    private final int channel;
    private final Played played;
    private final boolean active;
    private Map<String, Object> attributes;
    private final Object attributesSyncObject = new Object();

    private Signal(final int channel, final Played played, final boolean active) {
        this.channel = channel;
        this.played = played;
        this.active = active;
    }

    public static Signal of(final int packed) {
        final boolean active = (packed % 10) == 1;
        final int channelAndPlayed = packed / 10;
        final int channel = channelAndPlayed % 100;
        return Signal.of(channel, Played.of(channelAndPlayed / 100), active);
    }

    public static Signal on(final int channel, final Played played) {
        return of(channel, played, true);
    }

    public static Signal off(final int channel, final Played played) {
        return of(channel, played, false);
    }

    public static Signal of(final int channel, final Played played, final boolean active) {
        return of(channel, played, active, null);
    }

    public static Signal of(final int channel, final Played played, final boolean active,
                            final Map<String, Object> attributes) {
        final Signal signal = new Signal(channel, played, active);
        if (attributes != null)
            for(String key : attributes.keySet())
                signal.setAttribute(key, attributes.get(key));
        return signal;
    }

    /** Pitch + Channel identification */
    public int discriminator() {
        return played.pitch().midi() * 100 + channel;
    }

    public int channel() {
        return channel;
    }

    public Played played() {
        return played;
    }

    @Deprecated
    public boolean isActive() {
        return active;
    }

    public boolean isNoteOn() {
        return active && played.velocity() > 0;
    }

    public boolean isNoteOff() {
        return !active || played.velocity() == 0;
    }

    public Signal modifiedPlayed(final Played newPlayed) {
        synchronized (attributesSyncObject) {
            return of(channel, newPlayed, active, attributes);
        }
    }

    public Signal modifiedChannel(final int newChannel) {
        synchronized (attributesSyncObject) {
            return of(newChannel, played, active, attributes);
        }
    }

    public Signal toOn() {
        if (active)
            return this;
        else
            synchronized (attributesSyncObject) {
                return of(channel, played, true, attributes);
            }
    }

    public Signal toOff() {
        if (active)
            synchronized (attributesSyncObject) {
                return of(channel, played, false, attributes);
            }
        else
            return this;
    }

    @Override
    public Signal clone() {
        final Signal clone = of(channel, played.clone(), active);
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
        return (((played.pack() * 100) + channel) * 10) + (active ? 1 : 0);
    }

    @Override
    public String toString() {
        final Object tags = getAttribute(TAGS);
        return String.format("%s [%d] [%s] Tags=[%s]",
                played, channel, active ? "on" : "off",
                tags == null ? "" : tags);
    }

    /** Tags attribute name */
    public static final String TAGS = "tags";
}
