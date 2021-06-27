package mugres.core.common.chords;

import mugres.core.common.Context;
import mugres.core.common.Key;
import mugres.core.common.Length;
import mugres.core.common.Scale;

import java.util.List;

import static mugres.core.utils.Randoms.RND;
import static mugres.core.utils.Randoms.randoms;
import static mugres.core.utils.Utils.rangeClosed;

public class Chords {
    private Chords() {}

    public static ChordProgression improviseChordProgression(final Context context, final int measures) {
        final Key key = context.key();
        final Scale scale = key.defaultScale();
        final List<Integer> scaleDegrees = rangeClosed(1, scale.degrees());
        final List<Integer> roots = randoms(scaleDegrees, 4, false);
        final ChordProgression progression = ChordProgression.of(context, measures);
        final boolean alterChords = measures > 4 || RND.nextBoolean();

        Length at = Length.ZERO;

        if (measures < 4) {
            for (int index = 0; index < measures; index++) {
                progression.event(scale.chordAtDegree(key.root(), roots.get(index)), at);
                at = at.plus(context.timeSignature().measureLength());
            }
        } else if (measures == 4) {
            if (alterChords) {
                for (int index = 0; index < measures; index++) {
                    progression.event(scale.chordAtDegree(key.root(), roots.get(index)), at);
                    at = at.plus(context.timeSignature().measureLength());
                }
            }  else {
                for (int r = 0; r < 2; r++)
                    for (int index = 0; index < 2; index++) {
                        progression.event(scale.chordAtDegree(key.root(), roots.get(index)), at);
                        at = at.plus(context.timeSignature().measureLength());
                    }
            }
        } else {
            // Always alter chord if measures > 4
            for (int index = 0; index < 4; index++) {
                progression.event(scale.chordAtDegree(key.root(), roots.get(index)), at);
                at = at.plus(context.timeSignature().measureLength());
            }

            final List<Integer> newRoots = randoms(scaleDegrees, 4,  false);
            for (final Integer newRoot : newRoots) {
                progression.event(scale.chordAtDegree(key.root(), newRoot), at);
                at = at.plus(context.timeSignature().measureLength());
            }
        }

        return progression;
    }
}
