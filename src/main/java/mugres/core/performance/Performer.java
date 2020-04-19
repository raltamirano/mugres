package mugres.core.performance;

import mugres.core.common.Context;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.common.Party;
import mugres.core.function.Call;
import mugres.core.function.Result;
import mugres.core.notation.Arrangement;
import mugres.core.notation.Song;

import java.util.*;

public class Performer {
    private Performer() {}

    public static Performance perform(final Song song) {
        final Map<String, Map<String, List<Event>>> generatedMatrix = new HashMap<>();
        final Performance performance = new Performance(song.getTitle());

        addControlEvents(song, performance);

        Length offset = Length.ZERO;
        for(Party party : song.getParties()) {
            final Track track = performance.createTrack(party);
            offset = Length.ZERO;
            for(Arrangement.Entry arrangementEntry : song.getArrangement().getEntries()) {
                for(int arrangementEntryIndex = 1; arrangementEntryIndex <= arrangementEntry.getRepetitions();
                    arrangementEntryIndex++) {
                    if (arrangementEntry.getSection().hasPartsFor(party)) {
                        if (!arrangementEntry.getSection().isRegenerate() &&
                                generatedMatrix.containsKey(arrangementEntry.getSection().getName()) &&
                                generatedMatrix.get(arrangementEntry.getSection().getName()).containsKey(party.getName())) {
                            for (Event event : generatedMatrix.get(arrangementEntry.getSection().getName())
                                    .get(party.getName())) {
                                track.addEvent(Event.of(event.getPosition().plus(offset),
                                        event.getPlayed().getPitch(),
                                        event.getValue(),
                                        event.getPlayed().getVelocity()));
                            }
                        } else {
                            if (!generatedMatrix.containsKey(arrangementEntry.getSection().getName()))
                                generatedMatrix.put(arrangementEntry.getSection().getName(), new HashMap<>());

                            final List<Event> partyEvents = new ArrayList<>();
                            Length previousCallsOffset = Length.ZERO;
                            for (Call call : arrangementEntry.getSection().getMatrix().get(party)) {
                                final Context callContext = arrangementEntry.getSection().getContext();
                                final Result functionResult = call.execute(callContext);
                                if (functionResult.succeeded()) {
                                    final List<Event> events = sortEventList(functionResult.getEvents());
                                    for (Event event : events) {
                                        track.addEvent(Event.of(event.getPosition().plus(offset).plus(previousCallsOffset),
                                                event.getPlayed().getPitch(),
                                                event.getValue(),
                                                event.getPlayed().getVelocity()));
                                        partyEvents.add(Event.of(event.getPosition().plus(previousCallsOffset),
                                                event.getPlayed().getPitch(),
                                                event.getValue(),
                                                event.getPlayed().getVelocity()));
                                    }
                                    previousCallsOffset = previousCallsOffset.plus(callContext.getTimeSignature()
                                            .measuresLength(call.getLengthInMeasures()));
                                } else {
                                    // TODO: better error handling
                                    throw new RuntimeException(functionResult.getError());
                                }
                            }

                            generatedMatrix.get(arrangementEntry.getSection().getName())
                                    .put(party.getName(), partyEvents);
                        }
                    }
                    offset = offset.plus(arrangementEntry.getSection().getLength());
                }
            }
        }

        performance.setLength(offset);

        return performance;
    }

    private static void addControlEvents(final Song song, final Performance performance) {
        Length offset = Length.ZERO;
        for(Arrangement.Entry arrangementEntry : song.getArrangement().getEntries()) {
            for (int arrangementEntryIndex = 1; arrangementEntryIndex <= arrangementEntry.getRepetitions();
                 arrangementEntryIndex++) {

                final Context context = arrangementEntry.getSection().getContext();
                final Control currentControl = Control.of(context.getTempo(), context.getKey(),
                        context.getTimeSignature());

                final Control lastControl =
                        performance.getControlEvents().isEmpty() ? null :
                                performance.getControlEvents()
                                        .get(performance.getControlEvents().size() - 1).getControl();

                if (lastControl == null || !lastControl.equals(currentControl))
                    performance.addControlEvent(offset, currentControl);

                offset = offset.plus(arrangementEntry.getSection().getLength());
            }
        }
    }

    private static List<Event> sortEventList(final List<Event> events) {
        final List<Event> sortedEventList = new ArrayList<>(events);
        sortedEventList.sort(Comparator.comparing(Event::getPosition));
        return sortedEventList;
    }
}
