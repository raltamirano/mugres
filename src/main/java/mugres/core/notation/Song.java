package mugres.core.notation;

import mugres.core.common.Context;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.function.Call;
import mugres.core.function.Result;
import mugres.core.performance.Performance;
import mugres.core.performance.Track;

import java.util.*;

public class Song {
    private String title;
    private final Context context;
    private final Set<Section> sections = new HashSet<>();
    private final Set<Party> parties = new HashSet<>();
    private final Arrangement arrangement = new Arrangement();

    private Song(final String title, final Context context) {
        this.title = title;
        this.context = context;
    }

    public static Song of(final String title, final Context context) {
        return new Song(title, context);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Context getContext() {
        return context;
    }

    public Section createSection(final String sectionName) {
        if (sections.stream().anyMatch(s -> s.getName().equals(sectionName)))
            throw new IllegalArgumentException(String.format("Section '%s' already exists!", sectionName));

        final Section section = new Section(this, sectionName);
        sections.add(section);
        return section;
    }

    void addParty(final Party party) {
        if (party == null)
            throw new IllegalArgumentException("party");

        if (!parties.contains(party))
            parties.add(party);
    }

    public Set<Section> getSections() {
        return Collections.unmodifiableSet(sections);
    }

    public Set<Party> getParties() {
        return Collections.unmodifiableSet(parties);
    }

    public Arrangement getArrangement() {
        return arrangement;
    }

    public Performance perform() {
        final Performance performance = new Performance(title);

        for(Party party : parties) {
            final Track track = performance.createTrack(party.getName());
            Length position = Length.ZERO;
            for(Arrangement.Entry arrangementEntry : arrangement.getEntries()) {
                for(int arrangementEntryIndex = 1; arrangementEntryIndex <= arrangementEntry.getRepetitions();
                    arrangementEntryIndex++) {
                    for (Call call : arrangementEntry.getSection().getMatrix().get(party)) {
                        final Result functionResult = call.execute(context);
                        if (functionResult.succeeded()) {
                            final List<Event> events = sortEventList(functionResult.getEvents());
                            Length lastPosition = Length.ZERO;
                            for(Event event : events) {
                                if (!event.getPosition().equals(lastPosition)) {
                                    position = position.plus(event.getPosition());
                                    lastPosition = event.getPosition();
                                }
                                track.addEvent(Event.of(position, event.getPitch(),
                                        event.getValue(), event.getVelocity()));
                            }
                        } else {
                            // TODO: better error handling
                            throw new RuntimeException(functionResult.getError());
                        }
                    }
                }
            }
        }

        return performance;
    }

    private List<Event> sortEventList(final List<Event> events) {
        final List<Event> sortedEventList = new ArrayList<>(events);
        sortedEventList.sort(Comparator.comparing(Event::getPosition));
        return sortedEventList;
    }
}
