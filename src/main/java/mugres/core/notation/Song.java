package mugres.core.notation;

import mugres.core.common.Context;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Song {
    private String title;
    private final Context context;
    private final Set<Section> sections = new HashSet<>();
    private final Arrangement arrangement = new Arrangement();

    public Song(final String title, final Context context) {
        this.title = title;
        this.context = context;
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
        if (sections.contains(sectionName))
            throw new IllegalArgumentException(String.format("Section '%s' already exists!", sectionName));

        final Section section = new Section(this, sectionName);
        sections.add(section);
        return section;
    }

    public Set<Section> getSections() {
        return Collections.unmodifiableSet(sections);
    }

    public Arrangement getArrangement() {
        return arrangement;
    }
}
