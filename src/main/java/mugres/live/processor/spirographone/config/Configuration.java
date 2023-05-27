package mugres.live.processor.spirographone.config;

import mugres.common.Note;
import mugres.common.Scale;

public class Configuration {
    private int outputChannel;
    private double externalCircleRadius;
    private double internalCircleRadius;
    private double offsetOnInternalCircle;
    private double iterationDelta;
    private int spaceMillis;
    private int minOctave;
    private int maxOctave;
    private Note root;
    private Scale scale;

    public int getOutputChannel() {
        return outputChannel;
    }

    public void setOutputChannel(int outputChannel) {
        this.outputChannel = outputChannel;
    }

    public double getExternalCircleRadius() {
        return externalCircleRadius;
    }

    public void setExternalCircleRadius(double externalCircleRadius) {
        this.externalCircleRadius = externalCircleRadius;
    }

    public double getInternalCircleRadius() {
        return internalCircleRadius;
    }

    public void setInternalCircleRadius(double internalCircleRadius) {
        this.internalCircleRadius = internalCircleRadius;
    }

    public double getOffsetOnInternalCircle() {
        return offsetOnInternalCircle;
    }

    public void setOffsetOnInternalCircle(double offsetOnInternalCircle) {
        this.offsetOnInternalCircle = offsetOnInternalCircle;
    }

    public double getIterationDelta() {
        return iterationDelta;
    }

    public void setIterationDelta(double iterationDelta) {
        this.iterationDelta = iterationDelta;
    }

    public int getSpaceMillis() {
        return spaceMillis;
    }

    public void setSpaceMillis(int spaceMillis) {
        this.spaceMillis = spaceMillis;
    }

    public int getMinOctave() {
        return minOctave;
    }

    public void setMinOctave(int minOctave) {
        this.minOctave = minOctave;
    }

    public int getMaxOctave() {
        return maxOctave;
    }

    public void setMaxOctave(int maxOctave) {
        this.maxOctave = maxOctave;
    }

    public Note getRoot() {
        return root;
    }

    public void setRoot(Note root) {
        this.root = root;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }
}
