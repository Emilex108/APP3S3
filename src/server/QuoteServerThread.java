package server;

import packet.PDUBuilder;
import packet.PDUMaison;
import packet.PacketDecoder;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;

public class QuoteServerThread extends Thread {

    private DatagramSocket socket = null;
    private File fileRecu;
    private OutputStream osRecu;
    private NetworkInterface ni;
    private int currentPacketNumber = 0;
    private static File log;
    private static OutputStream osLog;
    private static PacketDecoder decoder = new PacketDecoder();

    public QuoteServerThread() throws IOException {
        this("QuoteServerThread");
    }

    public QuoteServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(27841);

        log = new File("liaisonDeDonnees.log");
        osLog = new FileOutputStream(log,true);
    }

    public void run() {
        while (true) {
            try {
                byte[] receivedData = new byte[32768];
                // Recoit le packet
                DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
                socket.receive(packet);

                ni = NetworkInterface.getByInetAddress(packet.getAddress());
                log("Packet reçu.");
                if(decoder.checkValidity(packet)){
                    log("Fichier validé.");
                }else{
                    log("Fichier avec erreur, demande de réenvoie.");
                }

                // Sauvegarde le nom du fichier
                decoder.saveFile(packet);

                PDUBuilder builder = new PDUBuilder();

                builder.ajouterCoucheApplicationServeur(decoder.checkValidity(packet));
                builder.ajouterCoucheTransportServeur(0, ni);
                builder.ajouterCoucheLiaisonServeur(socket, packet);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //socket.close();
    }

    public synchronized static void log(String msg) throws IOException {
        // Crée un time stamp assez clean
        osLog.write(("[" + new Timestamp(System.currentTimeMillis()).toString().substring(11,21) + "] [Serveur] : ").getBytes());
        osLog.write(msg.trim().getBytes());
        // Change de ligne
        osLog.write(10);
    }

}