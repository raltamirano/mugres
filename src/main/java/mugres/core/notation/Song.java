package mugres.core.notation;

import mugres.core.common.Context;
import mugres.core.common.Party;
import mugres.core.function.Call;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    public static Song of(final Context functionCallsContext, final Party functionCallsParty, final Call call) {
        final Song functionCallSong = new Song("Untitled", functionCallsContext);
        final Section section = functionCallSong.createSection("A", call.getLengthInMeasures());
        section.addPart(functionCallsParty, call);
        functionCallSong.arrangement.addEntry(section, 1);
        return functionCallSong;
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

    public Section createSection(final String sectionName, final int measures) {
        if (sections.stream().anyMatch(s -> s.getName().equals(sectionName)))
            throw new IllegalArgumentException(String.format("Section '%s' already exists!", sectionName));

        final Section section = new Section(this, sectionName, measures);
        sections.add(section);
        return section;
    }

    /** Creates a song that contains a single section from this songs. That section will be arranged
     * to be repeated once. */
    public Song createSectionSong(final String sectionName) {
        final Section section = getSection(sectionName);
        if (section == null)
            throw new IllegalArgumentException("Unknown section: " + sectionName);

        final Song sectionSong = Song.of(sectionName, context);
        sectionSong.parties.addAll(parties);
        sectionSong.sections.add(section);
        sectionSong.arrangement.addEntry(section, 1);

        return sectionSong;
    }

    void addParty(final Party party) {
        if (party == null)
            throw new IllegalArgumentException("party");

        parties.add(party);
    }

    public Set<Section> getSections() {
        return Collections.unmodifiableSet(sections);
    }

    public Section getSection(final String name) {
        for(Section section : sections)
            if (section.getName().equals(name))
                return section;

        return null;
    }

    public Set<Party> getParties() {
        return Collections.unmodifiableSet(parties);
    }

    public Arrangement getArrangement() {
        return arrangement;
    }
}
