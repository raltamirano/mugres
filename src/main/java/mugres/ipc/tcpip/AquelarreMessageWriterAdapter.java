package mugres.ipc.tcpip;

import aquelarre.Envelope;
import aquelarre.MessageWriter;
import mugres.ipc.protocol.Message;
import mugres.ipc.stream.DataOutputStreamWriter;

import java.io.DataOutputStream;
import java.io.IOException;

import static mugres.ipc.tcpip.Utils.switchEnvelope;

public class AquelarreMessageWriterAdapter implements MessageWriter<Message> {
    private final DataOutputStreamWriter dataOutputStreamWriter;

    public AquelarreMessageWriterAdapter(final DataOutputStreamWriter dataOutputStreamWriter) {
        if (dataOutputStreamWriter == null)
            throw new IllegalArgumentException("dataOutputStreamWriter");

        this.dataOutputStreamWriter = dataOutputStreamWriter;
    }

    @Override
    public void write(final Envelope<Message> message, final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStreamWriter.write(switchEnvelope(message), dataOutputStream);
    }
}
