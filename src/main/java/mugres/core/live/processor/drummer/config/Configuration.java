package mugres.core.live.processor.drummer.config;

import mugres.core.common.Context;
import mugres.core.common.DrumKit;
import mugres.core.function.Call;
import mugres.core.function.Function.Parameter.Variant;
import mugres.core.function.builtin.drums.PreRecordedDrums;
import mugres.core.notation.Song;
import mugres.core.notation.performance.Performer;
import mugres.core.notation.performance.converters.ToMidiSequenceConverter;

import javax.sound.midi.Sequence;
import java.util.HashMap;
import java.util.Map;

import static mugres.core.common.Party.WellKnownParties.DRUMS;
import static mugres.core.function.Function.LENGTH_PARAMETER;

public class Configuration {
    private String title;
    private final Map<String, Groove> grooves = new HashMap<>();
    private final Map<Integer, Action> actions = new HashMap<>();

    public Configuration(final String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

    public Map<String, Groove> grooves() {
        return grooves;
    }

    public Map<Integer, Action> actions() {
        return actions;
    }

    public Groove createGroove(final String name) {
        return createGroove(name, 0, Groove.Mode.SEQUENCE, Groove.Mode.SEQUENCE);
    }

    public Groove createGroove(final String name,
                               final Groove.Mode groovesMode,
                               final Groove.Mode fillsMode) {
        return createGroove(name, 0, groovesMode, fillsMode);
    }

    public Groove createGroove(final String name, final int tempo,
                               final Groove.Mode groovesMode,
                               final Groove.Mode fillsMode) {
        if (grooves.containsKey(name))
            throw new IllegalArgumentException("Groove already created: " + name);

        final Groove pattern = new Groove(name, tempo, groovesMode, fillsMode);
        grooves.put(name, pattern);

        return pattern;
    }

    public Groove createGroove(final String name,
                               final Context context,
                               final int measures,
                               final PreRecordedDrums generator) {
        return createGroove(name, Groove.Mode.SEQUENCE, Groove.Mode.SEQUENCE, context, measures, generator);
    }

    public Groove createGroove(final String name,
                               final Groove.Mode groovesMode,
                               final Groove.Mode fillsMode,
                               final Context context,
                               final int measures,
                               final PreRecordedDrums generator) {

        final Groove groove = createGroove(name, context.tempo(), groovesMode, fillsMode);

        final Map<String, Object> arguments = new HashMap<>();

        // Generate mains
        final Variant mainVariant = Variant.V0;
        arguments.put(LENGTH_PARAMETER.name(), measures);
        arguments.put("variant", mainVariant);
        arguments.put("fill", Variant.NONE);
        arguments.put("startingHit", DrumKit.CR1);

        final Sequence sequence = generateSequence(context, generator, arguments);
        final Part generatedPart = new Part(name + " " + mainVariant.name(), sequence);
        groove.appendMain(generatedPart);

        // Generate fills
        final Variant fillVariant = Variant.V0;
        arguments.clear();
        arguments.put(LENGTH_PARAMETER.name(), 1);
        arguments.put("variant", Variant.V0);
        arguments.put("fill", fillVariant);

        final Sequence fillSequence = generateSequence(context, generator, arguments);
        final Part fillGeneratedPart = new Part(name + " Fill " + fillVariant.name(), fillSequence);
        groove.appendFill(fillGeneratedPart);

        return groove;
    }

    private Sequence generateSequence(final Context context, final PreRecordedDrums generator,
                                      final Map<String, Object> arguments) {
        final Sequence sequence = ToMidiSequenceConverter.getInstance().convert(Performer
                .perform(Song.of(context, DRUMS.party(), Call.of(generator, arguments))));

        // Remove control track
        sequence.deleteTrack(sequence.getTracks()[0]);

        return sequence;
    }

    public Groove getGroove(final String name) {
        final Groove pattern = grooves.get(name);

        if (pattern == null)
            throw new IllegalArgumentException("Unknown groove: " + name);

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
