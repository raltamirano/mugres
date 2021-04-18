package mugres.core.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Live signal. Analog to {@link mugres.core.common.Event} in the notated world. */
public class Signal {
    private final long time;
    private final int channel;
    private final Played played;
    private final boolean active;
    private Map<String, Object> attributes;
    private final Object attributesSyncObject = new Object();

    private Signal(final long time, final int channel, final Played played, final boolean active) {
        this.time = time;
        this.channel = channel;
        this.played = played;
        this.active = active;
    }

    public static Signal on(final long time, final int channel, final Played played) {
        return of(time, channel, played, true);
    }

    public static Signal off(final long time, final int channel, final Played played) {
        return of(time, channel, played, false);
    }

    public static Signal of(final long time, final int channel, final Played played, final boolean active) {
        return new Signal(time, channel, played, active);
    }

    public long getTime() {
        return time;
    }

    public int getChannel() {
        return channel;
    }

    public Played getPlayed() {
        return played;
    }

    public boolean isActive() {
        return active;
    }

    public Signal modifiedPlayed(final Played newPlayed) {
        return of(time, channel, newPlayed, active);
    }

    public Signal modifiedTime(final long newTime) {
        return of(newTime, channel, played, active);
    }

    public Signal toOn() {
        return active ? this : of(time, channel, played, true);
    }

    public Signal toOff() {
        return active ? of(time, channel, played, false) : this;
    }

    public Map<String, Object> getAttributes() {
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

    public Object getAttribute(final String name) {
        synchronized (attributesSyncObject) {
            return attributes == null ?
                null :
                attributes.get(name);
        }
    }

    public void removeAttribute(final String name) {
        synchronized (attributesSyncObject) {
            if (attributes != null)
                attributes.remove(name);
        }
    }

    @Override
    public String toString() {
        return String.format("%s [%d][%s]", played, channel, active ? "on" : "off");
    }

    /** Lane attribute name */
    public static final String LANE = "lane";
}
