package mugres.core.common.gridpattern.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static mugres.core.common.gridpattern.GridPattern.NO_EVENT;

public class DrumKitHitDataConverter implements DataConverter<DrumKitHitDataConverter.DrumKitHit> {
    private static final DrumKitHitDataConverter INSTANCE = new DrumKitHitDataConverter();

    private DrumKitHitDataConverter() {}

    public static DrumKitHitDataConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public DrumKitHit convert(final String data) {
        if (NO_EVENT.equals(data)) return SILENT;
        if ("X".equals(data)) return NORMAL;
        if ("x".equals(data)) return SOFT;

        throw new IllegalArgumentException("Invalid DrumKit hit specification: " + data);
    }

    @Override
    public List<DrumKitHit> tokenize(final String line) {
        final char[] chars = line.toCharArray();
        final List<String> strings = new ArrayList<>();
        for(int index = 0; index < chars.length; index++)
            strings.add(String.valueOf(chars[index]));

        return strings.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    public static final DrumKitHit NORMAL = DrumKitHit.of(DrumKitHit.Intensity.NORMAL);
    public static final DrumKitHit SOFT = DrumKitHit.of(DrumKitHit.Intensity.SOFT);
    public static final DrumKitHit SILENT = DrumKitHit.of(DrumKitHit.Intensity.SILENT);

    public static class DrumKitHit {
        private final Intensity intensity;

        private DrumKitHit(final Intensity intensity) {
            this.intensity = intensity;
        }

        public static DrumKitHit of(final Intensity intensity) {
            return new DrumKitHit(intensity);
        }

        public Intensity getIntensity() {
            return intensity;
        }

        public enum Intensity {
            NORMAL,
            SOFT,
            SILENT
        }
    }
}
