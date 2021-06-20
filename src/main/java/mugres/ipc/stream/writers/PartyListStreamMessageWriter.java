package mugres.ipc.stream.writers;

import mugres.core.common.Party;
import mugres.ipc.protocol.messages.PartyListMessage;
import mugres.ipc.protocol.messages.SetPartyMessage;

import java.io.DataOutputStream;
import java.io.IOException;

public class PartyListStreamMessageWriter implements StreamMessageWriter<PartyListMessage> {
    @Override
    public void write(final PartyListMessage message, final DataOutputStream dataOutputStream) throws IOException {
        writeMessageType(message.type(), dataOutputStream);
        dataOutputStream.writeInt(message.partyList().size());
        for(final Party party : message.partyList()) {
            dataOutputStream.writeUTF(party.getName());
            dataOutputStream.writeInt(party.getInstrument().getId());
        }
    }
}
