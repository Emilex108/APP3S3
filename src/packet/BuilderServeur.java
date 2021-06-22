package packet;

import java.awt.image.AffineTransformOp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;

public interface BuilderServeur {
    void ajouterCoucheApplicationServeur(boolean valide) throws IOException;
    void ajouterCoucheTransportServeur(int sequenceNumber, NetworkInterface ni) throws IOException;
    void ajouterCoucheLiaisonServeur(DatagramSocket socket, DatagramPacket packet) throws IOException;
    PDUMaison getPacket();
}
