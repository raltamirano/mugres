package mugres.core.audio.utils;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.util.ArrayList;
import java.util.List;

import static mugres.core.audio.utils.Mixers.MixerCapabilities.PLAYBACK;
import static mugres.core.audio.utils.Mixers.MixerCapabilities.RECORD;
import static mugres.core.audio.utils.Mixers.MixerCapabilities.RECORD_PLAYBACK;

public class Mixers {
    private Mixers() {}

    public static List<Mixer> getMixers(final MixerCapabilities capabilities) {
        final List<Mixer> result = new ArrayList<>();

        final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (final Mixer.Info info : mixerInfos) {
            final Mixer mixer = AudioSystem.getMixer(info);
            if (((capabilities == RECORD || capabilities == RECORD_PLAYBACK) && mixer.getTargetLineInfo().length != 0) ||
                    ((capabilities == PLAYBACK || capabilities == RECORD_PLAYBACK) && mixer.getSourceLineInfo().length != 0))
                result.add(mixer);
        }

        return result;
    }

    public enum MixerCapabilities {
        RECORD,
        PLAYBACK,
        RECORD_PLAYBACK
    }
}
