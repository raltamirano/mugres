package mugres.core;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MUGRES {
    private static String midiInputPortName = System.getProperty("mugres.inputPort");
    private static String midiOutputPortName = System.getProperty("mugres.outputPort");
    private static Transmitter midiInputPort = null;
    private static Receiver midiOutputPort = null;

    /** Port name for Gervill, the built-in Java synth. */
    private static final String GERVILL = "Gervill";

    static {
        if (midiOutputPortName == null || midiOutputPortName.trim().isEmpty())
            midiOutputPortName = GERVILL;
    }

    private MUGRES() {}

    public static void useMidiInputPort(final String name) {
        midiInputPortName = name;
        midiInputPort = null;
    }

    public static void useMidiOutputPort(final String name) {
        midiOutputPortName = name;
        midiOutputPort = null;
    }

    public static synchronized Transmitter getMidiInputPort() {
        if (midiInputPort != null)
            return midiInputPort;

        final String portName = midiInputPortName;
        final MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        final List<MidiDevice.Info> candidates = Arrays.stream(midiDeviceInfo)
                .filter(d -> d.getName().equals(portName)).collect(Collectors.toList());

        MidiDevice midiDevice = null;
        boolean open = false;
        for(MidiDevice.Info candidate : candidates) {
            try {
                open = false;
                midiDevice = MidiSystem.getMidiDevice(candidate);
                midiDevice.open();
                open = true;
                midiInputPort = midiDevice.getTransmitter();
                return midiInputPort;
            } catch (Exception e) {
                if (midiDevice != null && open)
                    midiDevice.close();
            }
        }

        throw new RuntimeException(String.format("Invalid MUGRES MIDI input port: '%s'. Available ports: \n %s",
                portName, Arrays.stream(midiDeviceInfo)
                        .filter(MUGRES::isInputMidiDevice)
                        .map(i -> String.format("\t%s", i.getName()))
                        .collect(Collectors.joining("\n"))
        ));
    }

    public static synchronized Receiver getMidiOutputPort() {
        if (midiOutputPort != null)
            return midiOutputPort;

        final String portName = midiOutputPortName;
        final MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        final List<MidiDevice.Info> candidates = Arrays.stream(midiDeviceInfo)
                .filter(d -> d.getName().equals(portName)).collect(Collectors.toList());

        MidiDevice midiDevice = null;
        boolean open = false;
        for(MidiDevice.Info candidate : candidates) {
            try {
                open = false;
                midiDevice = MidiSystem.getMidiDevice(candidate);
                midiDevice.open();
                open = true;
                midiOutputPort = midiDevice.getReceiver();
                return midiOutputPort;
            } catch (Exception e) {
                if (midiDevice != null && open)
                    midiDevice.close();
            }
        }

        throw new RuntimeException(String.format("Invalid MUGRES MIDI output port: '%s'. Available ports: \n %s",
                portName, Arrays.stream(midiDeviceInfo)
                        .filter(MUGRES::isOutputMidiDevice)
                        .map(i -> String.format("\t%s", i.getName()))
                        .collect(Collectors.joining("\n"))
        ));
    }

    private static boolean isInputMidiDevice(final MidiDevice.Info info) {
        MidiDevice midiDevice = null;
        try {
            midiDevice = MidiSystem.getMidiDevice(info);
            midiDevice.open();
            midiDevice.getTransmitter();
            return true;
        } catch (final Throwable ignore) {
            return false;
        } finally {
            if (midiDevice != null) {
                try {
                    midiDevice.close();
                } catch (final Throwable ignore) {
                }
            }
        }
    }

    private static boolean isOutputMidiDevice(final MidiDevice.Info info) {
        MidiDevice midiDevice = null;
        try {
            midiDevice = MidiSystem.getMidiDevice(info);
            midiDevice.open();
            midiDevice.getReceiver();
            return true;
        } catch (final Throwable ignore) {
            return false;
        } finally {
            if (midiDevice != null) {
                try {
                    midiDevice.close();
                } catch (final Throwable ignore) {
                }
            }
        }
    }
}
