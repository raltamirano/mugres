package mugres.ipc.stream.readers;

import mugres.core.common.Instrument;
import mugres.core.common.Party;
import mugres.ipc.protocol.MessageType;
import mugres.ipc.protocol.messages.PartyListMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartyListStreamMessageReader implements StreamMessageReader<PartyListMessage> {
    @Override
    public PartyListMessage read(final MessageType messageType, final DataInputStream dataInputStream) throws IOException {
        final int numberOfParties = dataInputStream.readInt();

        final List<Party> partyList = new ArrayList<>();
        for(int i=0; i<numberOfParties; i++) {
            final String name = dataInputStream.readUTF();
            final Instrument instrument = Instrument.of(dataInputStream.readInt());
            partyList.add(new Party(name, instrument, 0));
        }
        return PartyListMessage.of(partyList);
    }
}
