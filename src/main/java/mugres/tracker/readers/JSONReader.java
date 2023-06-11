package mugres.tracker.readers;

import mugres.common.Context;
import mugres.common.Key;
import mugres.common.TimeSignature;
import mugres.function.Call;
import mugres.common.Party;
import mugres.tracker.Pattern;
import mugres.tracker.Song;
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
        final JSONObject metadataObject = songData.getJSONObject("metadata");
        final Song song = Song.of(title, songContext, metadataObject != null ? metadataObject.toMap() : null);

        // Patterns
        for(Object patternDataObject : songData.getJSONArray("patterns")) {
            final JSONObject patternData = (JSONObject) patternDataObject;
            final String name = patternData.getString("name");
            final int measures = patternData.getInt("measures");
            final Pattern pattern = song.createPattern(name, measures);
            loadContext(patternData, pattern.context());

            // Party/function calls matrix
            for(String partyName : patternData.getJSONObject("matrix").keySet()) {
                final Party party = Party.WellKnownParties.valueOf(partyName).party();
                final Object partyCallsObject = patternData.getJSONObject("matrix").get(partyName);
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
                        pattern.addPart(party, call);
                    } else {
                        final Call call = Call.parse(callDataObject.toString());
                        pattern.addPart(party, call);
                    }
                }
            }
        }

        // Arrangement
        for(Object arrangementDataObject : songData.getJSONArray("arrangement")) {
            final JSONObject arrangementData = (JSONObject) arrangementDataObject;
            final String patternName = arrangementData.getString("pattern");
            final int repetitions = arrangementData.getInt("repetitions");

            final Pattern pattern = song.pattern(patternName);
            if (pattern == null)
                throw new RuntimeException("Invalid arrangement pattern: " + patternName);

            song.arrangement().append(pattern, repetitions);
        }

        return song;
    }

    private Context createSongContext(final JSONObject data) {
        final Context context = Context.basicContext();
        loadContext(data, context);
        return context;
    }

    private void loadContext(final JSONObject data, final Context context) {
        if (data.has("tempo"))
            context.tempo(data.getInt("tempo"));
        if (data.has("key"))
            context.key(Key.of(data.getString("key")));
        if (data.has("timeSignature"))
            context.timeSignature(TimeSignature.of(data.getString("timeSignature")));
    }
}
