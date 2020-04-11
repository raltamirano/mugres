package mugres.core.notation.readers;

import mugres.core.common.Context;
import mugres.core.common.Key;
import mugres.core.common.TimeSignature;
import mugres.core.function.Call;
import mugres.core.notation.Party;
import mugres.core.notation.Section;
import mugres.core.notation.Song;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class JSONReader implements Reader {
    @Override
    public Song readSong(final InputStream inputStream)
            throws IOException {
        final JSONObject songData = new JSONObject(IOUtils.toString(inputStream, Charset.defaultCharset()));

        // Song
        final String title = songData.getString("title");
        final Context songContext = createSongContext(songData);
        final Song song = Song.of(title, songContext);

        // Sections
        for(Object sectionDataObject : songData.getJSONArray("sections")) {
            final JSONObject sectionData = (JSONObject) sectionDataObject;
            final String name = sectionData.getString("name");
            final int measures = sectionData.getInt("measures");
            final Section section = song.createSection(name, measures);
            loadContext(sectionData, section.getContext());

            // Party/function calls matrix
            for(String partyName : sectionData.getJSONObject("matrix").keySet()) {
                final Party party = Party.WellKnownParties.valueOf(partyName).getParty();
                for(Object callDataObject : sectionData.getJSONObject("matrix").getJSONArray(partyName)) {
                    final Call call = Call.parse(callDataObject.toString());
                    section.addPart(party, call);
                }
            }
        }

        // Arrangement
        for(Object arrangementDataObject : songData.getJSONArray("arrangement")) {
            final JSONObject arrangementData = (JSONObject) arrangementDataObject;
            final String sectionName = arrangementData.getString("section");
            final int repetitions = arrangementData.getInt("repetitions");

            final Section section = song.getSection(sectionName);
            if (section == null)
                throw new RuntimeException("Invalid arrangement section: " + sectionName);

            song.getArrangement().addEntry(section, repetitions);
        }

        return song;
    }

    private Context createSongContext(final JSONObject data) {
        final Context context = Context.createBasicContext();
        loadContext(data, context);
        return context;
    }

    private void loadContext(final JSONObject data, final Context context) {
        if (data.has("tempo"))
            context.setTempo(data.getInt("tempo"));
        if (data.has("key"))
            context.setKey(Key.fromLabel(data.getString("key")));
        if (data.has("timeSignature"))
            context.setTimeSignature(TimeSignature.of(data.getString("timeSignature")));
    }
}
