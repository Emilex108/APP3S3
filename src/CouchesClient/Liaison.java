package CouchesClient;

import client.QuoteClient;
import packet.PDUMaison;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


public class Liaison {
    public static void handle(PDUMaison pdu, DatagramSocket socket, InetAddress address, int port, boolean error) throws IOException {
        pdu.getPacket().setAddress(address);
        pdu.getPacket().setPort(port);
        pdu.getPacket().setLength(pdu.getPacket().getLength());
        byte[] currentData = pdu.getPacket().getData();
        Checksum checksum = new CRC32();
        checksum.update(currentData, 0, currentData.length);
        long checksumValue = checksum.getValue();
        byte[] result = new byte[Long.BYTES];
        for(int i = Long.BYTES -1; i >= 0; i--){
            result[i] = (byte) (checksumValue & 0xFF);
            checksumValue >>= Byte.SIZE;
        }
        byte[] dataFinal = new byte[pdu.getPacket().getData().length+result.length];
        System.arraycopy(pdu.getPacket().getData(), 0, dataFinal, 0, pdu.getPacket().getData().length);
        System.arraycopy(result, 0, dataFinal, pdu.getPacket().getData().length, result.length);
        if(error){
            dataFinal[20] = (byte) (dataFinal[1]-1);
        }
        pdu.getPacket().setData(dataFinal);
        socket.send(pdu.getPacket());
        QuoteClient.log("Couche de liaison effectuée.");
    }
}
