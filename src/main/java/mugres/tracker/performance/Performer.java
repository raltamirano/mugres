package mugres.tracker.performance;

import mugres.common.Context;
import mugres.tracker.Event;
import mugres.common.Length;
import mugres.common.Party;
import mugres.function.Call;
import mugres.function.Result;
import mugres.tracker.Arrangement;
import mugres.tracker.Song;

import java.util.*;

public class Performer {
    private Performer() {}

    public static Performance perform(final Song song) {
        final Map<String, Map<String, List<Event>>> generatedMatrix = new HashMap<>();
        final Performance performance = new Performance(song.title());

        addControlEvents(song, performance);

        Length offset = Length.ZERO;
        for(Party party : song.parties()) {
            final Track track = performance.createTrack(party);
            offset = Length.ZERO;
            for(Arrangement.Entry arrangementEntry : song.arrangement().entries()) {
                for(int arrangementEntryIndex = 1; arrangementEntryIndex <= arrangementEntry.repetitions();
                    arrangementEntryIndex++) {
                    if (arrangementEntry.pattern().hasPartsFor(party)) {
                        if (!arrangementEntry.pattern().isRegenerate() &&
                                generatedMatrix.containsKey(arrangementEntry.pattern().name()) &&
                                generatedMatrix.get(arrangementEntry.pattern().name()).containsKey(party.name())) {
                            for (Event event : generatedMatrix.get(arrangementEntry.pattern().name())
                                    .get(party.name())) {
                                track.addEvent(event.offset(offset));
                            }
                        } else {
                            if (!generatedMatrix.containsKey(arrangementEntry.pattern().name()))
                                generatedMatrix.put(arrangementEntry.pattern().name(), new HashMap<>());

                            final List<Event> partyEvents = new ArrayList<>();
                            Length previousCallsOffset = Length.ZERO;
                            for (Call<List<Event>> call : arrangementEntry.pattern().matrix().get(party)) {
                                final Context callContext = arrangementEntry.pattern().context();
                                final Result<List<Event>> functionResult = call.execute(callContext);
                                if (functionResult.succeeded()) {
                                    final List<Event> events = sortEventList(functionResult.data());
                                    for (Event event : events) {
                                        track.addEvent(event.offset(offset.plus(previousCallsOffset)));
                                        partyEvents.add(event.offset(previousCallsOffset));
                                    }
                                    previousCallsOffset = previousCallsOffset.plus(callContext.timeSignature()
                                            .measuresLength(call.getLengthInMeasures()));
                                } else {
                                    // TODO: better error handling
                                    throw new RuntimeException(functionResult.error());
                                }
                            }

                            generatedMatrix.get(arrangementEntry.pattern().name())
                                    .put(party.name(), partyEvents);
                        }
                    }
                    offset = offset.plus(arrangementEntry.pattern().length());
                }
            }
        }

        performance.length(offset);

        return performance;
    }

    private static void addControlEvents(final Song song, final Performance performance) {
        Length offset = Length.ZERO;
        for(Arrangement.Entry arrangementEntry : song.arrangement().entries()) {
            for (int arrangementEntryIndex = 1; arrangementEntryIndex <= arrangementEntry.repetitions();
                 arrangementEntryIndex++) {

                final Context context = arrangementEntry.pattern().context();
                final Control currentControl = Control.of(context.tempo(), context.key(),
                        context.timeSignature());

                final Control lastControl =
                        performance.controlEvents().isEmpty() ? null :
                                performance.controlEvents()
                                        .get(performance.controlEvents().size() - 1).control();

                if (lastControl == null || !lastControl.equals(currentControl))
                    performance.addControlEvent(offset, currentControl);

                offset = offset.plus(arrangementEntry.pattern().length());
            }
        }
    }

    private static List<Event> sortEventList(final List<Event> events) {
        final List<Event> sortedEventList = new ArrayList<>(events);
        sortedEventList.sort(Comparator.comparing(Event::position));
        return sortedEventList;
    }
}
