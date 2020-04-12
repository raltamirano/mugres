package mugres.core.notation.readers;

import mugres.core.common.Context;
import mugres.core.common.Key;
import mugres.core.common.TimeSignature;
import mugres.core.function.Call;
import mugres.core.common.Party;
import mugres.core.notation.Section;
import mugres.core.notation.Song;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                final Object partyCallsObject = sectionData.getJSONObject("matrix").get(partyName);
                final List<Object> partyCalls = new ArrayList<>();
                if (partyCallsObject instanceof JSONArray) {
                    final JSONArray partyCallsArray = (JSONArray) partyCallsObject;
                    for(int index=0; index < partyCallsArray.length(); index++)
                        partyCalls.add(partyCallsArray.get(index));
                } else
                    partyCalls.add(partyCallsObject);

                for(Object callDataObject : partyCalls) {
                    if (callDataObject instanceof JSONObject) {
                        final JSONObject callData = (JSONObject)callDataObject;
                        final Map<String, String> arguments = new HashMap<>();
                        if (callData.has("arguments")) {
                            for(String argumentName : callData.getJSONObject("arguments").keySet()) {
                                final Object argumentDataObject = callData.getJSONObject("arguments").get(argumentName);
                                if (argumentDataObject instanceof JSONArray) {
                                    final String argumentValue = ((JSONArray)argumentDataObject).toList()
                                            .stream()
                                            .map(Object::toString)
                                            .collect(Collectors.joining());

                                    arguments.put(argumentName, "'" +  argumentValue + "'");
                                } else if (argumentDataObject instanceof JSONObject) {
                                    throw new IllegalArgumentException("Expected either a primitive value or " +
                                            "an array of string as argument values");
                                } else {
                                    arguments.put(argumentName, argumentDataObject.toString());
                                }
                            }
                        }

                        final String callSpec = callData.getString("call");
                        final Call call = Call.parse(callSpec, arguments);
                        section.addPart(party, call);
                    } else {
                        final Call call = Call.parse(callDataObject.toString());
                        section.addPart(party, call);
                    }
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
