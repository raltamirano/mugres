package mugres.core.notation;

import mugres.core.common.Context;

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

    void addParty(final Party party) {
        if (party == null)
            throw new IllegalArgumentException("party");

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
}
