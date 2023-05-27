package mugres.ipc.stream.readers;

import mugres.live.Signal;
import mugres.live.Signals;
import mugres.ipc.protocol.MessageType;
import mugres.ipc.protocol.messages.SignalsMessage;

import java.io.DataInputStream;
import java.io.IOException;

public class SignalsStreamMessageReader implements StreamMessageReader<SignalsMessage> {
    @Override
    public SignalsMessage read(final MessageType messageType, final DataInputStream dataInputStream) throws IOException {
        final Signals signals = Signals.create();

        // Format:
        // 1 int: number of played events
        // N ints: packed Signals
        final int numberOfSignals = dataInputStream.readInt();

        for (int signalIndex = 0; signalIndex < numberOfSignals; signalIndex++) {
            final int packedSignal = dataInputStream.readInt();
            signals.add(Signal.of(packedSignal));
        }

        return SignalsMessage.of(signals);
    }
}
