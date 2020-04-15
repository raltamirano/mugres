package mugres.core.live.processors.drummer.config;

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

    public String getName() {
        return this.name;
    }

    public int getTempo() {
        return this.tempo;
    }

    public Groove.Mode getMainsMode() {
        return this.mainsMode;
    }

    public Groove.Mode getFillsMode() {
        return this.fillsMode;
    }

    public List<Part> getMains() {
        return this.mains;
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

    public void setMainsMode(Groove.Mode mainsMode) {
        this.mainsMode = mainsMode;
    }

    public void setFillsMode(Groove.Mode fillsMode) {
        this.fillsMode = fillsMode;
    }

    public void setMains(List<Part> mains) {
        this.mains = mains;
    }

    public void setFills(List<Part> fills) {
        this.fills = fills;
    }

    public enum Mode {
        SEQUENCE,
        RANDOM
    }
}
