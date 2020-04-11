package mugres.core.ui;

import mugres.core.notation.Section;
import mugres.core.notation.Song;
import mugres.core.notation.readers.JSONReader;
import mugres.core.performance.Performance;
import mugres.core.performance.Performer;
import mugres.core.performance.converters.ToMIDISequenceConverter;

import javax.sound.midi.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class REPL {
    private static File songFile;
    private static Song song;
    private static Map<Integer, String> sectionsMap = new HashMap<>();
    private static Sequencer sequencer;

    private static final Map<String, Function<String[], Boolean>> HANDLERS = new HashMap<>();
    private static final JSONReader SONG_JSON_READER = new JSONReader();
    private static final ToMIDISequenceConverter TO_MIDI_SEQUENCE_CONVERTER = new ToMIDISequenceConverter();

    public static void main(String[] args) {
        loadCommandHandlers();
        createMIDISequencer();

        System.out.println("Welcome to MUGRES.");

        final Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.print("mugres> ");
            final String line = scanner.nextLine().trim();
            if (!line.isEmpty())
                running = executeCommand(line.split("\\s"));
        }

        System.out.print("Goodbye.");
        System.exit(0);
    }

    private static void loadCommandHandlers() {
        HANDLERS.put("status", REPL::status);
        HANDLERS.put("load-song", REPL::loadSong);
        HANDLERS.put("play-song", REPL::playSong);
        HANDLERS.put("sections", REPL::sections);
        HANDLERS.put("play-section", REPL::playSection);
        HANDLERS.put("loop-section", REPL::loopSection);
        HANDLERS.put("stop", REPL::stop);
        HANDLERS.put("quit", REPL::quit);
    }

    private static void createMIDISequencer() {
        sequencer = createSequencer(createOutputPort());
    }

    private static Receiver createOutputPort() {
        final String portName = System.getProperty("mugres.outputPort");
        final List<MidiDevice.Info> candidates = Arrays.stream(MidiSystem.getMidiDeviceInfo())
                .filter(d -> d.getName().equals(portName)).collect(Collectors.toList());

        MidiDevice midiDevice = null;
        boolean open = false;
        for(MidiDevice.Info candidate : candidates) {
            try {
                open = false;
                midiDevice = MidiSystem.getMidiDevice(candidate);
                midiDevice.open();
                open = true;
                return midiDevice.getReceiver();
            } catch (Exception e) {
                if (midiDevice != null && open)
                    midiDevice.close();
            }
        }

        throw new RuntimeException("Invalid MIDI output port: " + portName);
    }

    private static Sequencer createSequencer(final Receiver outputPort) {
        try {
            final Sequencer aSequencer = MidiSystem.getSequencer(false);
            aSequencer.getTransmitter().setReceiver(outputPort);
            aSequencer.open();
            return aSequencer;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean executeCommand(final String[] tokens) {
        if (tokens.length == 0)
            return true;

        final String command = tokens[0];
        final Function<String[], Boolean> handler = HANDLERS.get(command);

        if (handler != null) {
            return handler.apply(tokens);
        } else {
            System.out.println("Unknown command: " + command);
            return true;
        }
    }

    private static boolean status(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else {

            System.out.println(String.format("Song loaded = %s", isSongLoaded() ? "Yes" : "No"));
            if (isSongLoaded()) {
                System.out.println("Song: " + song.getTitle());
                System.out.println("Source file: " + songFile.getAbsolutePath());
            }
        }

        return true;
    }

    private static boolean isSongLoaded() {
        return song != null;
    }

    private static boolean loadSong(final String[] args) {
        if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: path to file to load the song from");
        } else {
            doStop();
            song = null;
            songFile = null;
            sectionsMap.clear();

            final File file = new File(args[1]);

            if (!file.exists()) {
                System.out.println("Source file doesn't exists: " + file.getAbsolutePath());
            } else {
                try {
                    song = SONG_JSON_READER.readSong(new FileInputStream(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                songFile = file;

                int sectionId = 1;
                for(Section section : song.getSections())
                    sectionsMap.put(sectionId++, section.getName());

                System.out.println(String.format("Successfully loaded song '%s' from '%s'",
                        song.getTitle(), songFile.getAbsolutePath()));
            }
        }

        return true;
    }

    private static boolean playSong(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else if (!isSongLoaded()) {
            System.out.println(args[0] + ": no song loaded");
        } else {
            final Performance performance = Performer.perform(song);
            final Sequence songMIDISequence = TO_MIDI_SEQUENCE_CONVERTER.convert(performance);
            playMIDISequence(songMIDISequence, false);
        }

        return true;
    }

    private static boolean playSection(final String[] args) {
        if (!isSongLoaded()) {
            System.out.println(args[0] + ": no song loaded");
        } else if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: section id (issue command 'sections' to list available sections)");
        } else {
            final int sectionId = Integer.parseInt(args[1]);
            String sectionName = sectionsMap.get(sectionId);
            if (sectionName == null) {
                System.out.println("Invalid section id: " + sectionId);
            } else {
                doPlaySection(sectionName, false);
            }
        }

        return true;
    }

    private static boolean loopSection(final String[] args) {
        if (!isSongLoaded()) {
            System.out.println(args[0] + ": no song loaded");
        } else if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: section id (issue command 'sections' to list available sections)");
        } else {
            final int sectionId = Integer.parseInt(args[1]);
            String sectionName = sectionsMap.get(sectionId);
            if (sectionName == null) {
                System.out.println("Invalid section id: " + sectionId);
            } else {
                doPlaySection(sectionName, true);
            }
        }

        return true;
    }

    private static void doPlaySection(final String sectionName, final boolean loop) {
        final Performance performance = Performer.perform(song.createSectionSong(sectionName));
        final Sequence songMIDISequence = TO_MIDI_SEQUENCE_CONVERTER.convert(performance);
        playMIDISequence(songMIDISequence, loop);

    }

    private static boolean sections(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else if (!isSongLoaded()) {
            System.out.println(args[0] + ": no song loaded");
        } else {
            for(Integer sectionId : sectionsMap.keySet())
                System.out.println(String.format("%-3s\t%s", sectionId, sectionsMap.get(sectionId)));
        }

        return true;
    }

    private static void playMIDISequence(final Sequence midiSequence, final boolean loop) {
        doStop();

        try {
            sequencer.setSequence(midiSequence);
            sequencer.setLoopCount(loop ? Integer.MAX_VALUE : 0);
            sequencer.start();
        } catch (final InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean stop(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else {
            doStop();
        }

        return true;
    }

    private static boolean quit(final String[] args) {
        return false;
    }

    private static void doStop() {
        if (sequencer.isRunning())
            sequencer.stop();
    }
}
