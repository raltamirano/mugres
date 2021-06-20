package mugres.ipc.protocol.messages;

import mugres.core.common.Party;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyListMessage extends Message {
    private final List<Party> partyList;

    private PartyListMessage(final List<Party> partyList) {
        super(MessageType.PARTY_LIST);

        if (partyList == null)
            throw new IllegalArgumentException("partyList");

        this.partyList = new ArrayList<>(partyList);
    }

    public static PartyListMessage of(final List<Party> partyList) {
        return new PartyListMessage(partyList);
    }

    public List<Party> partyList() {
        return Collections.unmodifiableList(partyList);
    }

    @Override
    public String toString() {
        return super.toString() + " - Party list: " + partyList;
    }
}
