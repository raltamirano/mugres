package mugres.core.live.processors.drummer.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DrumPattern {
    private String name;
    private int tempo;
    private DrumPattern.Mode groovesMode;
    private DrumPattern.Mode fillsMode;
    private List<Part> grooves = new ArrayList<>();
    private List<Part> fills = new ArrayList<>();

    public DrumPattern(String name, int tempo, DrumPattern.Mode groovesMode, DrumPattern.Mode fillsMode) {
        this.name = name;
        this.tempo = tempo;
        this.groovesMode = groovesMode;
        this.fillsMode = fillsMode;
    }

    public DrumPattern appendGroove(final File file) {
        return appendGroove(Part.fromFile(file));
    }

    public DrumPattern appendGroove(final Part part) {
        grooves.add(part);
        return this;
    }

    public DrumPattern appendFill(final File file) {
        return appendFill(Part.fromFile(file));
    }

    public DrumPattern appendFill(final Part part) {
        fills.add(part);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getTempo() {
        return this.tempo;
    }

    public DrumPattern.Mode getGroovesMode() {
        return this.groovesMode;
    }

    public DrumPattern.Mode getFillsMode() {
        return this.fillsMode;
    }

    public List<Part> getGrooves() {
        return this.grooves;
    }

    public List<Part> getFills() {
        return this.fills;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public void setGroovesMode(DrumPattern.Mode groovesMode) {
        this.groovesMode = groovesMode;
    }

    public void setFillsMode(DrumPattern.Mode fillsMode) {
        this.fillsMode = fillsMode;
    }

    public void setGrooves(List<Part> grooves) {
        this.grooves = grooves;
    }

    public void setFills(List<Part> fills) {
        this.fills = fills;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DrumPattern)) return false;
        final DrumPattern other = (DrumPattern) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        if (this.getTempo() != other.getTempo()) return false;
        final Object this$groovesMode = this.getGroovesMode();
        final Object other$groovesMode = other.getGroovesMode();
        if (this$groovesMode == null ? other$groovesMode != null : !this$groovesMode.equals(other$groovesMode))
            return false;
        final Object this$fillsMode = this.getFillsMode();
        final Object other$fillsMode = other.getFillsMode();
        if (this$fillsMode == null ? other$fillsMode != null : !this$fillsMode.equals(other$fillsMode)) return false;
        final Object this$grooves = this.getGrooves();
        final Object other$grooves = other.getGrooves();
        if (this$grooves == null ? other$grooves != null : !this$grooves.equals(other$grooves)) return false;
        final Object this$fills = this.getFills();
        final Object other$fills = other.getFills();
        if (this$fills == null ? other$fills != null : !this$fills.equals(other$fills)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DrumPattern;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        result = result * PRIME + this.getTempo();
        final Object $groovesMode = this.getGroovesMode();
        result = result * PRIME + ($groovesMode == null ? 43 : $groovesMode.hashCode());
        final Object $fillsMode = this.getFillsMode();
        result = result * PRIME + ($fillsMode == null ? 43 : $fillsMode.hashCode());
        final Object $grooves = this.getGrooves();
        result = result * PRIME + ($grooves == null ? 43 : $grooves.hashCode());
        final Object $fills = this.getFills();
        result = result * PRIME + ($fills == null ? 43 : $fills.hashCode());
        return result;
    }

    public String toString() {
        return "DrumPattern(name=" + this.getName() + ", tempo=" + this.getTempo() + ", groovesMode=" + this.getGroovesMode() + ", fillsMode=" + this.getFillsMode() + ", grooves=" + this.getGrooves() + ", fills=" + this.getFills() + ")";
    }

    public enum Mode {
        SEQUENCE,
        RANDOM
    }
}
