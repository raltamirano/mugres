package mugres.core.utils.repl;

import mugres.MUGRES;
import mugres.core.common.Context;
import mugres.core.common.Key;
import mugres.core.common.Party;
import mugres.core.common.TimeSignature;
import mugres.core.function.Call;
import mugres.core.function.Function;
import mugres.core.tracker.Section;
import mugres.core.tracker.Song;
import mugres.core.tracker.performance.Performance;
import mugres.core.tracker.performance.Performer;
import mugres.core.tracker.performance.converters.ToMidiSequenceConverter;
import mugres.core.tracker.readers.JSONReader;
import mugres.core.utils.RandomSong;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class REPL {
    private static File songFile;
    private static Song song;
    private static Map<Integer, String> sectionsMap = new HashMap<>();
    private static Sequencer sequencer;
    private static Context functionCallsContext = Context.basicContext();
    private static Party functionCallsParty = Party.WellKnownParties.PIANO.party();
    private static String loopingSection = null;
    private static Sequence loopingSectionMidiSequence = null;
    private static final Map<String, java.util.function.Function<String[], Boolean>> HANDLERS = new HashMap<>();
    private static final JSONReader SONG_JSON_READER = new JSONReader();
    private static FileWatcher songFileWatcher = null;

    public static void main(String[] args) {
        loadCommandHandlers();
        createMidiSequencer();

        System.out.println("Welcome to MUGRES");

        final Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.print("mugres> ");
            final String line = scanner.nextLine().trim();
            if (!line.isEmpty())
                running = executeCommand(line.split("\\s"));
        }

        System.out.println("Goodbye");
        System.exit(0);
    }

    private static void loadCommandHandlers() {
        HANDLERS.put("help", REPL::help);
        HANDLERS.put("status", REPL::status);
        HANDLERS.put("load-song", REPL::loadSong);
        HANDLERS.put("play-song", REPL::playSong);
        HANDLERS.put("random-song", REPL::randomSong);
        HANDLERS.put("sections", REPL::sections);
        HANDLERS.put("play-section", REPL::playSection);
        HANDLERS.put("loop-section", REPL::loopSection);
        HANDLERS.put("calls-show-context", REPL::callsShowContext);
        HANDLERS.put("calls-tempo", REPL::callsSetTempo);
        HANDLERS.put("calls-key", REPL::callsSetKey);
        HANDLERS.put("calls-ts", REPL::callsSetTimeSignature);
        HANDLERS.put("calls-party", REPL::callsSetParty);
        HANDLERS.put("calls-show-functions", REPL::callsShowFunctions);
        HANDLERS.put("calls-show-parties", REPL::callsShowAvailableParties);
        HANDLERS.put("call", REPL::callsExecute);
        HANDLERS.put("stop", REPL::stop);
        HANDLERS.put("quit", REPL::quit);
    }

    private static void createMidiSequencer() {
        sequencer = createSequencer(MUGRES.getMidiOutputPort());
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
        final java.util.function.Function<String[], Boolean> handler = HANDLERS.get(command);

        if (handler != null) {
            return handler.apply(tokens);
        } else {
            System.out.println("Unknown command: " + command);
            return true;
        }
    }

    private static boolean help(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else {
            System.out.println("Available commands:");
            final List<String> commandNames = new ArrayList<>(HANDLERS.keySet());
            Collections.sort(commandNames);
            for(String name : commandNames)
                System.out.println(String.format("\t%s", name));
        }

        return true;
    }

    private static boolean status(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else {

            System.out.println(String.format("Song loaded = %s", isSongLoaded() ? "Yes" : "No"));
            if (isSongLoaded()) {
                System.out.println("Song: " + song.title());
                System.out.println("Source file: " + songFile.getAbsolutePath());
                System.out.println("Looping section: " + ((loopingSection == null) ? "No" : loopingSection));
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
        try {
            if (!reload)
                doStop();

            song = null;
            songFile = null;
            sectionsMap.clear();

            final File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("Source file doesn't exists: " + file.getAbsolutePath());
            } else {
                song = SONG_JSON_READER.readSong(new FileInputStream(file));
                songFile = file;
                if (songFileWatcher != null)
                    try {
                        songFileWatcher.stopThread();
                    } catch (final Throwable t) {
                    }
                songFileWatcher = new FileWatcher(songFile, REPL::onSongFileChanged);
                songFileWatcher.setDaemon(true);
                songFileWatcher.start();

                int sectionId = 1;
                final List<Section> sections = new ArrayList<>(song.sections());
                sections.sort(Comparator.comparing(Section::name));
                for (final Section section : sections)
                    sectionsMap.put(sectionId++, section.name());

                if (!reload)
                    System.out.println(String.format("Successfully loaded song '%s' from '%s'",
                            song.title(), songFile.getAbsolutePath()));
            }
        } catch(final Throwable t) {
            t.printStackTrace();
        }
    }

    private static void onSongFileChanged(final File changed) {
        try {
            final String loopedSection = loopingSection;

            doLoadSong(changed.getAbsolutePath(), true);
            if (loopedSection != null && song.section(loopingSection) != null) {
                loopingSectionMidiSequence = createSectionSongMidiSequence(loopedSection);
                try {
                    sequencer.setSequence(loopingSectionMidiSequence);
                } catch (InvalidMidiDataException e) {
                    e.printStackTrace();
                }
            }
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    private static boolean playSong(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else if (!isSongLoaded()) {
            System.out.println(args[0] + ": no song loaded");
        } else {
            doPlaySong();
        }

        return true;
    }

    private static boolean randomSong(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else {
            song = RandomSong.randomSong();
            doPlaySong();
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

    private static void doPlaySong() {
        playMidiSequence(song.toMidiSequence(), false);
    }

    private static void doPlaySection(final String sectionName, final boolean loop) {
        final Sequence sequence = createSectionSongMidiSequence(sectionName);
        playMidiSequence(sequence, loop);

        if (loop) {
            loopingSection = sectionName;
            loopingSectionMidiSequence = sequence;
        }
    }

    private static Sequence createSectionSongMidiSequence(final String sectionName) {
        return song.createSectionSong(sectionName).toMidiSequence();
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
            System.out.println(String.format("Tempo = %d", functionCallsContext.tempo()));
            System.out.println(String.format("Key = %s", functionCallsContext.key()));
            System.out.println(String.format("Time Signature = %s", functionCallsContext.timeSignature()));
            System.out.println(String.format("Party = %s", functionCallsParty.name()));
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
                functionCallsContext.tempo(tempo);
            }
        }

        return true;
    }

    private static boolean callsSetKey(final String[] args) {
        if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: Key");
        } else {
            final Key key = Key.fromLabel(args[1]);
            functionCallsContext.key(key);
        }

        return true;
    }

    private static boolean callsSetTimeSignature(final String[] args) {
        if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: Time Signature. Format: nn/dd");
        } else {
            final TimeSignature timeSignature = TimeSignature.of(args[1]);
            functionCallsContext.timeSignature(timeSignature);
        }

        return true;
    }

    private static boolean callsSetParty(final String[] args) {
        if (args.length != 2) {
            System.out.println(args[0] + ": single argument expected: Party ID");
        } else {
            functionCallsParty = Party.WellKnownParties.valueOf(args[1]).party();
        }

        return true;
    }

    private static boolean callsShowAvailableParties(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else {
            for(Party.WellKnownParties w : Party.WellKnownParties.values())
                System.out.println(String.format("%-30s\t%s", w.name(), w.party().name()));
        }

        return true;
    }

    private static boolean callsExecute(final String[] args) {
        final Call call = Call.parse(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));

        Song functionCallSong = null;
        switch(call.getFunction().artifact()) {
            case SONG:
                functionCallSong = (Song)call.execute(functionCallsContext).data();
                break;
            case EVENTS:
                functionCallSong = Song.of(functionCallsContext, functionCallsParty, call);
                break;
            default:
                System.out.println("Unhandled function artifact: " + call.getFunction().artifact());
        }

        if (functionCallSong != null) {
            final Performance performance = Performer.perform(functionCallSong);
            final Sequence songMidiSequence = ToMidiSequenceConverter.getInstance().convert(performance);
            playMidiSequence(songMidiSequence, false);
        }

        return true;
    }

    private static boolean callsShowFunctions(final String[] args) {
        if (args.length != 1) {
            System.out.println(args[0] + ": no arguments expected");
        } else {
            for(Function<?> g : Function.allFunctions())
                System.out.println(String.format("%-20s\t%-50s\t%s", g.name(), g.description(),
                        g.artifact().label()));
        }

        return true;
    }

    private static void playMidiSequence(final Sequence midiSequence, final boolean loop) {
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
        loopingSectionMidiSequence = null;

        if (sequencer.isRunning())
            sequencer.stop();
    }
}
