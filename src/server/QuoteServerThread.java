package server;

import exceptions.TransmissionErrorException;
import packet.PDUBuilder;
import packet.PacketDecoderServer;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;

public class QuoteServerThread extends Thread {

    private DatagramSocket socket = null;
    private NetworkInterface ni;
    private static File log;
    private static OutputStream osLog;
    private int erreurs;

    public QuoteServerThread() throws IOException {
        this("QuoteServerThread");
    }

    public QuoteServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(27841);
        log = new File("liaisonDeDonnees.log");
        osLog = new FileOutputStream(log,true);
        erreurs = 0;
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
                PDUBuilder builder = new PDUBuilder();
                if(PacketDecoderServer.getInstance().checkValidity(packet)){
                    log("Fichier validé.");
                    PacketDecoderServer.getInstance().saveFile(packet);
                    builder.ajouterCoucheApplicationServeur(PacketDecoderServer.getInstance().checkValidity(packet));
                    builder.ajouterCoucheTransportServeur(0, ni);
                    builder.ajouterCoucheLiaisonServeur(socket, packet);
                }else{
                    erreurs++;
                    if(erreurs == 3){
                        throw new TransmissionErrorException("Trop d'erreurs. Arrêt du serveur.");
                    }
                    log("Fichier avec erreur, demande de réenvoie.");
                    builder.ajouterCoucheApplicationServeur(PacketDecoderServer.getInstance().checkValidity(packet));
                    builder.ajouterCoucheTransportServeur(0, ni);
                    builder.ajouterCoucheLiaisonServeur(socket, packet);
                }

                // Sauvegarde le nom du fichier






            } catch (IOException | TransmissionErrorException e) {
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