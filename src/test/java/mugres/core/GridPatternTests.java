package mugres.core;

import mugres.core.common.Context;
import mugres.core.common.gridpattern.GridPattern;
import mugres.core.common.gridpattern.converters.DrumKitHitElementPatternParser;
import mugres.core.common.gridpattern.converters.DyadElementPatternParser;
import org.junit.jupiter.api.Test;

import static mugres.core.common.Value.EIGHTH;
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

        final GridPattern<DrumKitHitElementPatternParser.DrumKitHit> drumPattern =
                GridPattern.parse(pattern, DrumKitHitElementPatternParser.getInstance(),
                        Context.createBasicContext());

        assertNotNull(drumPattern);
        System.out.println(drumPattern);
        assertEquals("8ths Basic Rock", drumPattern.getName());
        assertEquals(EIGHTH, drumPattern.getDivision());
        assertEquals(16, drumPattern.getSlots());
        assertEquals(64, drumPattern.getEvents().size());
    }

    @Test
    public void loadDyadPattern() {
        final String pattern =
                "Division=EIGHTH\n" +
                "IN   A5---------------D5-------E5-------";

        final GridPattern<DyadElementPatternParser.Dyad> riff =
                GridPattern.parse(pattern, DyadElementPatternParser.getInstance(),
                        Context.createBasicContext());

        assertNotNull(riff);
        System.out.println(riff);
        assertEquals("Untitled", riff.getName());
        assertEquals(EIGHTH, riff.getDivision());
        assertEquals(32, riff.getSlots());
        assertEquals(32, riff.getEvents().size());
    }
}
