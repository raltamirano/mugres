package mugres.function.builtin.misc;

import mugres.common.Context;
import mugres.common.DataType;
import mugres.common.IntervalType;
import mugres.common.Length;
import mugres.common.Note;
import mugres.common.Octave;
import mugres.common.Pitch;
import mugres.common.Scale;
import mugres.common.ScaleCorrection;
import mugres.common.TrackReference;
import mugres.function.Function;
import mugres.parametrizable.Parameter;
import mugres.tracker.Event;
import mugres.utils.Events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HarmonizeTrack extends Function.EventsFunction {
    public HarmonizeTrack() {
        super("harmonizeTrack", "Harmonize track",
                Parameter.of(TRACK, "Track to harmonize", 1, "Track to harmonize",
                        DataType.TRACK, false, null),
                Parameter.of(INTERVAL_TYPE, "Interval type", 2, "Interval type",
                        DataType.INTERVAL_TYPE, true, IntervalType.THIRD),
                Parameter.of(OCTAVE, "Octave", 3, "Octave",
                        DataType.OCTAVE, true, Octave.SAME),
                Parameter.of(SCALE, "Scale", 4, "Scale",
                        DataType.SCALE, false, null),
                Parameter.of(ROOT, "Root", 5, "Root",
                        DataType.NOTE, false, null),
                Parameter.of(SCALE_CORRECTION, "Scale correction", 6, "Scale correction",
                        DataType.SCALE_CORRECTION, true, ScaleCorrection.RANDOM),
                Parameter.of(SEPARATION, "Separation", 7, "Separation",
                        DataType.LENGTH, true, Length.ZERO),
                Parameter.of(VOICES, "Voices", 8, "Voices",
                        DataType.INTEGER, true, 1)
        );
    }

    @Override
    protected List<Event> doExecute(final Context context, final Map<String, Object> arguments) {
        final List<Event> result = new ArrayList<>();
        final Length length = lengthFromNumberOfMeasures(context, arguments);
        final List<Event> toHarmonize = parallelEventsOnTrack(context, (TrackReference) arguments.get(TRACK), length);
        final Scale scale = (Scale)arguments.get(SCALE);
        final Note root = (Note)arguments.get(ROOT);
        final Octave octave = (Octave) arguments.get(OCTAVE);
        final IntervalType intervalType = (IntervalType)arguments.get(INTERVAL_TYPE);
        final Length separation = (Length)arguments.get(SEPARATION);
        final int voices = (int)arguments.get(VOICES);


        Events.toRelativeToZero(toHarmonize).forEach(e -> {
            Length offset = separation;
            List<Pitch> harmony = scale.harmonize(root, e.pitch().note(), intervalType, voices + 1, e.pitch().octave());
            harmony = harmony.subList(1, harmony.size());
            for (Pitch h : harmony) {
                result.add(Event.of(e.position().plus(offset), octave.apply(h), e.length(), e.velocity()));
                offset = offset.plus(separation);
            }
        });

        return result;
    }

    public static final String TRACK = "track";
    public static final String INTERVAL_TYPE = "intervalType";
    public static final String OCTAVE = "octave";
    public static final String SCALE = "scale";
    public static final String ROOT = "root";
    public static final String SCALE_CORRECTION = "scaleCorrection";
    public static final String SEPARATION = "separation";
    public static final String VOICES = "voices";
}
