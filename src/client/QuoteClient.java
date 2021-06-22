package client;

import CouchesClient.Application;
import packet.PDUBuilder;
import packet.PDUMaison;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;

public class QuoteClient {

    public static DatagramSocket socket;
    public static InetAddress address;
    public static NetworkInterface ni;
    public static int portName = 27841;
    private static int currentPacketNumber = 0;
    private static OutputStream os;

    public static void main(String[] args) throws IOException {
        //TODO : Serveur et client -> Replace les chiffres par des CONST
        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }

        socket = new DatagramSocket();
        address = InetAddress.getLocalHost();
        ni = NetworkInterface.getByInetAddress(address);

        File file = new File("liaisonDeDonnees.log");
        os = new FileOutputStream(file, true);

        //Création du premier packet
        Application.ajouterCouche(new PDUMaison(), args[0],0);
        byte[] receivedData = new byte[24];
        DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
        socket.receive(packet);

        while(packet.getData()[15] == 0){
            log("Renvoie du packet.");
            Application.ajouterCouche(new PDUMaison(), args[0],0);
            //builder.ajouterCoucheTransportClient(0, ni);
            //builder.ajouterCoucheLiaisonClient(socket, address, portName);
            socket.receive(packet);
        }

        Application.ajouterCouche(new PDUMaison(), args[0],1);
        //builder.ajouterCoucheTransportClient(1, ni);
        //builder.ajouterCoucheLiaisonClient(socket, address, portName);

        //builder.getPacket();

        socket.close();
    }

    /**
     * Permet de sauvegarder des logs
     * @param msg Message a enregistrer
     * @throws IOException Si erreur dans le fichier
     */
    public static void log(String msg) throws IOException {
        // Crée un time stamp assez clean
        os.write(("[" + new Timestamp(System.currentTimeMillis()).toString().substring(11,21) + "] [Client] : ").getBytes());
        os.write(msg.trim().getBytes());
        // Change de ligne
        os.write(10);
    }
}