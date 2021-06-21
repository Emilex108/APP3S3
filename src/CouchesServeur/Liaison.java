package CouchesServeur;

import client.QuoteClient;
import packet.PDUMaison;
import server.QuoteServerThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Liaison {
    public static void ajouterCouche(PDUMaison pdu, DatagramSocket socket, DatagramPacket packet) throws IOException {
        pdu.getPacket().setAddress(packet.getAddress());
        pdu.getPacket().setPort(packet.getPort());
        pdu.getPacket().setLength(pdu.getPacket().getLength());
        socket.send(pdu.getPacket());
        QuoteServerThread.log("Couche de liaison effectuée.");
    }
}
