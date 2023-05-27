package mugres.live.processor.drummer.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Groove {
    private String name;
    private int tempo;
    private Groove.Mode mainsMode;
    private Groove.Mode fillsMode;
    private List<Part> mains = new ArrayList<>();
    private List<Part> fills = new ArrayList<>();

    public Groove(String name, int tempo, Groove.Mode mainsMode, Groove.Mode fillsMode) {
        this.name = name;
        this.tempo = tempo;
        this.mainsMode = mainsMode;
        this.fillsMode = fillsMode;
    }

    public Groove appendMain(final File file) {
        return appendMain(Part.fromFile(file));
    }

    public Groove appendMain(final Part part) {
        mains.add(part);
        return this;
    }

    public Groove appendFill(final File file) {
        return appendFill(Part.fromFile(file));
    }

    public Groove appendFill(final Part part) {
        fills.add(part);
        return this;
    }

    public String name() {
        return this.name;
    }

    public int tempo() {
        return this.tempo;
    }

    public Groove.Mode mainsMode() {
        return this.mainsMode;
    }

    public Groove.Mode fillsMode() {
        return this.fillsMode;
    }

    public List<Part> mains() {
        return this.mains;
    }

    public List<Part> fills() {
        return this.fills;
    }

    public void name(String name) {
        this.name = name;
    }

    public void tempo(int tempo) {
        this.tempo = tempo;
    }

    public void mainsMode(Groove.Mode mainsMode) {
        this.mainsMode = mainsMode;
    }

    public void fillsMode(Groove.Mode fillsMode) {
        this.fillsMode = fillsMode;
    }

    public void mains(List<Part> mains) {
        this.mains = mains;
    }

    public void fills(List<Part> fills) {
        this.fills = fills;
    }

    public enum Mode {
        SEQUENCE,
        RANDOM
    }
}
