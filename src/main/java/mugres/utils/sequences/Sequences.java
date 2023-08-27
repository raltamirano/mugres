package mugres.utils.sequences;

import mugres.common.Length;
import mugres.common.Octave;
import mugres.common.Pitch;
import mugres.common.TimeSignature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Sequences {

    private static final SequencePattern THREE_PATTERN = SequencePattern.of(asList(1, 2, 3), 1);
    private static final SequencePattern FOUR_PATTERN = SequencePattern.of(asList(1, 2, 3, 4), 1);

    private Sequences() {}

    // Sequence: Three
    public static List<Pitch> three(final List<Pitch> input,
                                    final Length length,
                                    final int measures,
                                    final TimeSignature timeSignature) {
        return three(input, length, measures, timeSignature,false);
    }

    public static List<Pitch> three(final List<Pitch> input,
                                    final Length length,
                                    final int measures,
                                    final TimeSignature timeSignature,
                                    final boolean descending) {
        return generate(input, 1, length, measures, timeSignature, descending, THREE_PATTERN);
    }

    public static List<Pitch> three(final List<Pitch> input, final int numberOfPitches) {
        return three(input, numberOfPitches, false);
    }

    public static List<Pitch> three(final List<Pitch> input,
                                    final int numberOfPitches,
                                    final boolean descending) {
        return generate(input, 1, numberOfPitches, descending, THREE_PATTERN);
    }

    // Sequence: Four
    public static List<Pitch> four(final List<Pitch> input,
                                   final Length length,
                                   final int measures,
                                   final TimeSignature timeSignature) {
        return four(input, length, measures, timeSignature,false);
    }

    public static List<Pitch> four(final List<Pitch> input,
                                   final Length length,
                                   final int measures,
                                   final TimeSignature timeSignature,
                                   final boolean descending) {
        return generate(input, 1, length, measures, timeSignature, descending, FOUR_PATTERN);
    }

    public static List<Pitch> four(final List<Pitch> input, final int numberOfPitches) {
        return four(input, numberOfPitches, false);
    }

    public static List<Pitch> four(final List<Pitch> input,
                                   final int numberOfPitches,
                                   final boolean descending) {
        return generate(input, 1, numberOfPitches, descending, FOUR_PATTERN);
    }

    // Other
    public static List<Pitch> generate(final List<Pitch> input,
                                       final int startIndex,
                                       final int numberOfPitches,
                                       final boolean descending,
                                       final SequencePattern pattern) {
        validateInputSize(input, pattern.maxStep());
        if (startIndex < 1 || startIndex > input.size())
            throw new IllegalArgumentException("startIndex");

        final List<Pitch> extendedInput = new ArrayList<>(input);
        if (descending)
            Collections.reverse(extendedInput);


        final List<Pitch> result = new ArrayList<>();

        int index = startIndex - 1;
        int octaveIndex = 1;
        while(result.size() < numberOfPitches) {
            for(int j=0; j<pattern.steps().size(); j++) {
                final int extendedInputIndex = index + pattern.steps().get(j) - 1;
                while (extendedInputIndex > extendedInput.size() - 1) {
                    final Octave octave = Octave.of(Octave.Type.INCREMENT, descending ? -octaveIndex : octaveIndex);
                    extendedInput.addAll(input.stream().map(octave::apply).collect(Collectors.toList()));
                    octaveIndex++;
                }
                result.add(extendedInput.get(extendedInputIndex));
                if (result.size() >= numberOfPitches)
                    break;
            }
            if (result.size() >= numberOfPitches)
                break;

            index += pattern.offset();
        }
        return result;
    }

    public static List<Pitch> generate(final List<Pitch> input,
                                       final int startIndex,
                                       final Length length,
                                       final int measures,
                                       final TimeSignature timeSignature,
                                       final boolean descending,
                                       final SequencePattern pattern) {
        validateInputSize(input, pattern.maxStep());
        if (startIndex < 1 || startIndex > input.size())
            throw new IllegalArgumentException("startIndex");

        final List<Pitch> extendedInput = new ArrayList<>(input);
        if (descending)
            Collections.reverse(extendedInput);

        final Length totalLength = timeSignature.measuresLength(measures);
        int octaveIndex = 1;
        while(length.multiply(extendedInput.size()).lessThan(totalLength)) {
            final Octave octave = Octave.of(Octave.Type.INCREMENT, descending ? -octaveIndex : octaveIndex);
            extendedInput.addAll(input.stream().map(octave::apply).collect(Collectors.toList()));
            octaveIndex++;
        }

        final List<Pitch> result = new ArrayList<>();

        int index = 0;
        while(length.multiply(result.size()).lessThan(totalLength)) {
            for(int j=0; j<pattern.steps().size(); j++) {
                result.add(extendedInput.get(index + pattern.steps().get(j) - 1));
                if (length.multiply(result.size()).greaterThanOrEqual(totalLength))
                    break;
            }
            if (length.multiply(result.size()).greaterThanOrEqual(totalLength))
                break;
            index++;
        }
        return result;
    }

    private static void validateInputSize(List<Pitch> input, final int minimumSize) {
        if (input == null || input.isEmpty() || input.size() < minimumSize)
            throw new IllegalArgumentException("input");
    }
}
