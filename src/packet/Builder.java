package packet;

import java.awt.image.AffineTransformOp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;

public interface Builder {
    void ajouterCoucheApplicationClient(String filename, int sequenceNumber) throws IOException;
    void ajouterCoucheApplicationServeur(boolean valide) throws IOException;
    void ajouterCoucheTransportClient(int sequenceNumber, NetworkInterface ni) throws IOException;
    void ajouterCoucheTransportServeur(int sequenceNumber, NetworkInterface ni) throws IOException;
    void ajouterCoucheLiaisonClient(DatagramSocket socket, InetAddress address, int port) throws IOException;
    void ajouterCoucheLiaisonServeur(DatagramSocket socket, DatagramPacket packet) throws IOException;
    PDUMaison getPacket();
}
