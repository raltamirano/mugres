package mugres.core;

import mugres.core.common.Value;
import mugres.core.common.gridpattern.GridPattern;
import mugres.core.common.gridpattern.converters.DrumKitHitDataConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GridPatternTests {
    @Test
    public void loadDrumPattern() {
        final String pattern =
                "# This an example of a basic rock beat\n" +
                "Name=8ths Basic Rock\n" +
                "Division=EIGHTH\n" +
                "Slots=16\n" +
                "\n" +
                "BD   x-x-x-x-x-x-x-x-\n" +
                "SD   -x-x-x-x-x-x-x-x\n" +
                "OHH  -xxxxxxxxxxxxxxx\n" +
                "CR1  x---------------";

        final GridPattern<DrumKitHitDataConverter.DrumKitHit> drumPattern =
                GridPattern.parse(pattern, DrumKitHitDataConverter.getInstance());

        assertNotNull(drumPattern);
        assertEquals("8ths Basic Rock", drumPattern.getName());
        assertEquals(Value.EIGHTH, drumPattern.getDivision());
        assertEquals(16, drumPattern.getSlots());
        assertEquals(64, drumPattern.getEvents().size());
    }
}
