package packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class PDUBuilder implements Builder {
    PDUMaison pdu = new PDUMaison();

    @Override
    public void ajouterCoucheApplicationClient(String filename, int sequenceNumber) throws IOException {
        CouchesClient.Application.ajouterCouche(pdu, filename, sequenceNumber);
    }

    @Override
    public void ajouterCoucheApplicationServeur(boolean valide) throws IOException {
        CouchesServeur.Application.ajouterCouche(pdu, valide);
    }

    @Override
    public void ajouterCoucheTransportClient(int sequenceNumber, NetworkInterface ni) throws IOException {
        CouchesClient.Transport.ajouterCouche(pdu, sequenceNumber, ni);
    }

    @Override
    public void ajouterCoucheTransportServeur(int sequenceNumber, NetworkInterface ni) throws IOException {
        CouchesServeur.Transport.ajouterCouche(pdu, sequenceNumber, ni);
    }

    @Override
    public void ajouterCoucheLiaisonClient(DatagramSocket socket, InetAddress address, int port) throws IOException {
        CouchesClient.Liaison.ajouterCouche(pdu, socket, address, port);
    }

    @Override
    public void ajouterCoucheLiaisonServeur(DatagramSocket socket, DatagramPacket packet) throws IOException {
        CouchesServeur.Liaison.ajouterCouche(pdu, socket, packet);
    }

    @Override
    public PDUMaison getPacket() {
        return null;
    }


}
