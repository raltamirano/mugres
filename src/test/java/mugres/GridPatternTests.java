package mugres;

import mugres.common.Context;
import mugres.common.gridpattern.GridPattern;
import mugres.common.gridpattern.converters.DrumKitHitElementPatternParser;
import mugres.common.gridpattern.converters.DyadElementPatternParser;
import org.junit.jupiter.api.Test;

import static mugres.common.Value.EIGHTH;
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
                        Context.basicContext());

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
                        Context.basicContext());

        assertNotNull(riff);
        System.out.println(riff);
        assertEquals("Untitled", riff.getName());
        assertEquals(EIGHTH, riff.getDivision());
        assertEquals(32, riff.getSlots());
        assertEquals(32, riff.getEvents().size());
    }
}
