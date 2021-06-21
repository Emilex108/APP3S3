package CouchesServeur;

import client.QuoteClient;
import packet.PDUMaison;
import server.QuoteServerThread;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;

public class Transport {
    public static void ajouterCouche(PDUMaison pdu, int sequenceNumber, NetworkInterface ni) throws IOException {
        byte[] dataHeader = createHeader(pdu.getPacket().getData(), sequenceNumber, ni);
        QuoteServerThread.log("Header créé.");
        byte[] dataTotalAvantCRC = new byte[dataHeader.length+pdu.getPacket().getData().length];
        System.arraycopy(dataHeader, 0, dataTotalAvantCRC, 0, dataHeader.length);
        System.arraycopy(pdu.getPacket().getData(), 0, dataTotalAvantCRC, dataHeader.length, pdu.getPacket().getData().length);
        pdu.getPacket().setData(dataTotalAvantCRC);
        QuoteServerThread.log("Couche de transport effectuée.");
    }

    private static byte[] createHeader(byte[] data, int sequenceNumber, NetworkInterface ni) throws SocketException {
        /**
         * sequenceNumber  = 1 byte
         * délimiteur = 1 byte
         * adresse destination = 6 bytes
         * adresse source = 6 bytes
         * taille du message = 1 byte
         * total = 15 bytes
         */
        byte[] header = new byte[15];
        //Ajoute le numéro de packet
        header[0] = (byte) (sequenceNumber);
        //Ajoute le délimiteur (0) -> Simple car premier packet est 1
        header[1] = (byte) '~';
        for(int i = 2; i < 8; i++){
            //Check si l'adresse est la même (send/receive)
            //Sinon remplacer la deuxième assignation par l'adresse locale
            header[i] = ni.getHardwareAddress()[i-2];
            header[i+6] = ni.getHardwareAddress()[i-2];
        }
        //Ajoute la longueur du message dans le header
        header[14] = (byte) data.length;
        return header;
    }
}
