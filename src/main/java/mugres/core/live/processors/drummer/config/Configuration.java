package mugres.core.live.processors.drummer.config;

import mugres.core.common.Context;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private String title;
    private Map<String, DrumPattern> drumPatterns = new HashMap<>();
    private final Map<Integer, Action> actions = new HashMap<>();
    private final Context context;

    public Configuration(final String title) {
        this.title = title;
        context = Context.createBasicContext();
    }

    public String getTitle() {
        return title;
    }

    public Map<String, DrumPattern> getDrumPatterns() {
        return drumPatterns;
    }

    public Map<Integer, Action> getActions() {
        return actions;
    }

    public Context getContext() {
        return context;
    }

    public DrumPattern createPattern(final String name) {
        return createPattern(name, 0, DrumPattern.Mode.SEQUENCE, DrumPattern.Mode.SEQUENCE);
    }

    public DrumPattern createPattern(final String name,
                                     final DrumPattern.Mode groovesMode,
                                     final DrumPattern.Mode fillsMode) {
        return createPattern(name, 0, groovesMode, fillsMode);
    }

    public DrumPattern createPattern(final String name, final int tempo,
                                     final DrumPattern.Mode groovesMode,
                                     final DrumPattern.Mode fillsMode) {
        if (drumPatterns.containsKey(name))
            throw new IllegalArgumentException("Pattern already created: " + name);

        final DrumPattern pattern = new DrumPattern(name, tempo, groovesMode, fillsMode);
        drumPatterns.put(name, pattern);

        return pattern;
    }

    public DrumPattern getPattern(final String name) {
        final DrumPattern pattern = drumPatterns.get(name);

        if (pattern == null)
            throw new IllegalArgumentException("Unknown pattern: " + name);

        return pattern;
    }

    public Configuration setAction(final int midi, final Action action) {
        if (midi <= 0)
            throw new IllegalArgumentException("Invalid midi number: " + midi);

        actions.put(midi, action);

        return this;
    }

    public Action getAction(final int midi) {
        return actions.get(midi);
    }
}
