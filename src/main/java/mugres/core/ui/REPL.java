package mugres.core.ui;

import mugres.core.common.Context;
import mugres.core.common.Key;
import mugres.core.common.TimeSignature;
import mugres.core.function.Call;
import mugres.core.common.Party;
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
    private static Context functionCallsContext = Context.createBasicContext();
    private static Party functionCallsParty = Party.WellKnownParties.PIANO.getParty();

    private static String loopingSection = null;
    private static Sequence loopingSectionMIDISequence = null;

    private static final Map<String, Function<String[], Boolean>> HANDLERS = new HashMap<>();
    private static final JSONReader SONG_JSON_READER = new JSONReader();
    private static final ToMIDISequenceConverter TO_MIDI_SEQUENCE_CONVERTER = new ToMIDISequenceConverter();
    private static FileWatcher songFileWatcher = null;

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
        HANDLERS.put("calls-show-context", REPL::callsShowContext);
        HANDLERS.put("calls-tempo", REPL::callsSetTempo);
        HANDLERS.put("calls-key", REPL::callsSetKey);
        HANDLERS.put("calls-ts", REPL::callsSetTimeSignature);
        HANDLERS.put("calls-party", REPL::callsSetParty);
        HANDLERS.put("calls-show-parties", REPL::callsShowAvailableParties);
        HANDLERS.put("call", REPL::callsExecute);
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
                System.out.println("Looping section: " + loopingSection == null ? "No" : loopingSection);
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
            doLoadSong(args[1], false);
        }

        return true;
    }

    private static void doLoadSong(final String filePath, final boolean reload) {
        if (!reload)
            doStop();

        song = null;
        songFile = null;
        sectionsMap.clear();

        final File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Source file doesn't exists: " + file.getAbsolutePath());
        } else {
            try {
                song = SONG_JSON_READER.readSong(new FileInputStream(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            songFile = file;
            if (songFileWatcher != null)
                try { songFileWatcher.stopThread(); } catch (final Throwable t) {}
            songFileWatcher = new FileWatcher(songFile, REPL::onSongFileChanged);
            songFileWatcher.start();

            int sectionId = 1;
            final List<Section> sections =new ArrayList<>(song.getSections());
            sections.sort(Comparator.comparing(Section::getName));
            for(final Section section : sections)
                sectionsMap.put(sectionId++, section.getName());

            if (!reload)
                System.out.println(String.format("Successfully loaded song '%s' from '%s'",
                        song.getTitle(), songFile.getAbsolutePath()));
        }
    }

    private static void onSongFileChanged(final File changed) {
        final String loopedSection = loopingSection;

        doLoadSong(changed.getAbsolutePath(), true);
        if (loopedSection != null && song.getSection(loopingSection) != null) {
            loopingSectionMIDISequence = createSectionSongMIDISequence(loopedSection);
            try {
                System.out.println("X");
                sequencer.setSequence(loopingSectionMIDISequence);
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }
        }
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
        final Sequence sequence = createSectionSongMIDISequence(sectionName);
        playMIDISequence(sequence, loop);

        if (loop) {
            loopingSection = sectionName;
            loopingSectionMIDISequence = sequence;
        }
    }

    private static Sequence createSectionSongMIDISequence(String sectionName) {
        final Performance performance = Performer.perform(song.createSectionSong(sectionName));
        return TO_MIDI_SEQUENCE_CONVERTER.convert(performance);
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

    private static boolean callsShowContext(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else {
            System.out.println(String.format("Tempo = %d", functionCallsContext.getTempo()));
            System.out.println(String.format("Key = %s", functionCallsContext.getKey()));
            System.out.println(String.format("Time Signature = %s", functionCallsContext.getTimeSignature()));
            System.out.println(String.format("Party = %s", functionCallsParty.getName()));
        }

        return true;
    }

    private static boolean callsSetTempo(final String[] args) {
        if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: tempo in BPM");
        } else {
            final int tempo = Integer.parseInt(args[1]);
            if (tempo <= 0) {
                System.out.println("Invalid tempo: " + tempo);
            } else {
                functionCallsContext.setTempo(tempo);
            }
        }

        return true;
    }

    private static boolean callsSetKey(final String[] args) {
        if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: Key");
        } else {
            final Key key = Key.fromLabel(args[1]);
            functionCallsContext.setKey(key);
        }

        return true;
    }

    private static boolean callsSetTimeSignature(final String[] args) {
        if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: Time Signature. Format: nn/dd");
        } else {
            final TimeSignature timeSignature = TimeSignature.of(args[1]);
            functionCallsContext.setTimeSignature(timeSignature);
        }

        return true;
    }

    private static boolean callsSetParty(final String[] args) {
        if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: Party ID");
        } else {
            functionCallsParty = Party.WellKnownParties.valueOf(args[1]).getParty();
        }

        return true;
    }

    private static boolean callsShowAvailableParties(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else {
            for(Party.WellKnownParties w : Party.WellKnownParties.values())
                System.out.println(String.format("%-30s\t%s", w.name(), w.getParty().getName()));
        }

        return true;
    }

    private static boolean callsExecute(final String[] args) {
        if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: call function specification");
        } else {
            final Call call = Call.parse(args[1]);
            final Song functionCallSong = Song.of(functionCallsContext, functionCallsParty, call);
            final Performance performance = Performer.perform(functionCallSong);
            final Sequence songMIDISequence = TO_MIDI_SEQUENCE_CONVERTER.convert(performance);
            playMIDISequence(songMIDISequence, false);
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
        loopingSection = null;
        loopingSectionMIDISequence = null;

        if (sequencer.isRunning())
            sequencer.stop();
    }
}
