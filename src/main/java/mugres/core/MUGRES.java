package mugres.core;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MUGRES {
    private static Transmitter midiInputPort = null;
    private static Receiver midiOutputPort = null;

    private MUGRES() {}

    private Transmitter getMidiInputPort() {
        if (midiInputPort != null)
            return midiInputPort;

        final String portName = System.getProperty("mugres.inputPort");
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
                midiInputPort = midiDevice.getTransmitter();
                return midiInputPort;
            } catch (Exception e) {
                if (midiDevice != null && open)
                    midiDevice.close();
            }
        }

        throw new RuntimeException("Invalid MUGRES Midi input port: " + portName);
    }

    public synchronized static Receiver getMidiOutputPort() {
        if (midiOutputPort != null)
            return midiOutputPort;

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
                midiOutputPort = midiDevice.getReceiver();
                return midiOutputPort;
            } catch (Exception e) {
                if (midiDevice != null && open)
                    midiDevice.close();
            }
        }

        throw new RuntimeException("Invalid MUGRES Midi output port: " + portName);
    }
}
