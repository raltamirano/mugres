package mugres.common.ttm;

import mugres.common.Note;

import java.util.*;

import static java.util.Arrays.asList;

public class TwelveToneMatrix {
    private final Note[][] matrix = new Note[ROW_SIZE][ROW_SIZE];
    private final List<Note> map = new ArrayList<>();

    public TwelveToneMatrix() {
        this(createRandomOriginalRow());
    }

    public TwelveToneMatrix(final List<Note> primeRow) {
        if (primeRow == null || primeRow.size() != ROW_SIZE)
            throw new IllegalArgumentException("Invalid prime row!");
        if (new HashSet<>(primeRow).size() != ROW_SIZE)
            throw new IllegalArgumentException("Prime row must not contain duplicate notes!");

        buildMatrix(primeRow);
    }

    public List<Note> originalRow() {
        return primeRow(0);
    }

    public List<Note> primeRow(final int row) {
        if (row < 0 || row >= ROW_SIZE)
            throw new IllegalArgumentException("row");

        return Collections.unmodifiableList(asList(matrix[row]));
    }

    public List<Note> retrogradeRow(final int row) {
        if (row < 0 || row >= ROW_SIZE)
            throw new IllegalArgumentException("row");

        final List<Note> rowCells = new ArrayList<>(asList(matrix[row]));
        Collections.reverse(rowCells);
        return Collections.unmodifiableList(rowCells);
    }

    public List<Note> inversionRow(final int row) {
        if (row < 0 || row >= ROW_SIZE)
            throw new IllegalArgumentException("row");

        final List<Note> rowCells = new ArrayList<>();
        for(int i=0; i<ROW_SIZE; i++)
            rowCells.add(matrix[i][row]);
        return Collections.unmodifiableList(rowCells);
    }

    public List<Note> retrogradeInversionRow(final int row) {
        if (row < 0 || row >= ROW_SIZE)
            throw new IllegalArgumentException("row");

        final List<Note> rowCells = new ArrayList<>();
        for(int i=ROW_SIZE-1; i>=0; i--)
            rowCells.add(matrix[i][row]);
        return Collections.unmodifiableList(rowCells);
    }

    public int getRowSize() {
        return ROW_SIZE;
    }

    private static List<Note> createRandomOriginalRow() {
        final List<Note> notes = new ArrayList<>();

        while(notes.size() != ROW_SIZE) {
            final Note note = Note.values()[RND.nextInt(ROW_SIZE)];
            if (!notes.contains(note))
                notes.add(note);
        }

        return notes;
    }

    private void buildMatrix(final List<Note> primeRow) {
        buildPitchesMap(primeRow);

        // Populate prime row
        for(int i = 0; i< ROW_SIZE; i++)
            matrix[0][i] = primeRow.get(i);

        // Populate Inversion Row 0
        for(int i = 1; i< ROW_SIZE; i++)
            matrix[i][0] = map.get(ROW_SIZE-mapNoteToIndex(matrix[0][i]));

        // Populate remaining cells
        for(int i = 1; i< ROW_SIZE; i++) {
            for(int j = 1; j< ROW_SIZE; j++) {
                final int sum = mapNoteToIndex(matrix[i][0])+mapNoteToIndex(matrix[0][j]);
                matrix[i][j] = map.get(sum >= ROW_SIZE ? sum-ROW_SIZE : sum);
            }
        }
    }

    private void buildPitchesMap(final List<Note> row) {
        Note note = row.get(0);
        map.add(note);
        for(int i=1; i<ROW_SIZE; i++)
            map.add(note.up(i));
    }

    public int mapNoteToIndex(final Note note) {
        for(int i=0; i<ROW_SIZE; i++)
            if (map.get(i).equals(note))
                return i;
        throw new IllegalArgumentException("Invalid note: " + note);
    }

    @Override
    public String toString() {
        return asString(true);
    }

    public String asString(final boolean showNotes) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                if (j > 0) builder.append(" ");
                if (showNotes)
                    builder.append(String.format("%-2s", matrix[i][j].label()));
                else
                    builder.append(String.format("%2s", mapNoteToIndex(matrix[i][j])));
            }
            builder.append("\n");
        }


        return builder.toString();
    }

    private static final int ROW_SIZE = 12;
    private static final Random RND = new Random();
}
