import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

public class QuoteServerThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;
    private File fileRecu;
    private OutputStream osRecu;
    private static File log;
    private static OutputStream osLog;

    public QuoteServerThread() throws IOException {
        this("QuoteServerThread");
    }

    public QuoteServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(27841);

        try {
            in = new BufferedReader(new FileReader("one-liners.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open quote file. Serving time instead.");
        }

        log = new File("liaisonDeDonnees.log");
        osLog = new FileOutputStream(log,true);
    }

    public void run() {
        //TODO:Replace le while (true)
        while (true) {
            try {
                byte[] receivedData = new byte[32768];
                // Recoit le packet
                DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
                socket.receive(packet);
                log("Packet reçu.");
                // Sauvegarde le nom du fichier
                //TODO: Enleve le size du header à l'array de byte?
                String fileName;
                byte[] fileNameBytes = new byte[receivedData.length];
                log("Taille du data reçu : " + packet.getData()[14]);
                System.arraycopy(receivedData, 15, fileNameBytes, 0, packet.getData()[14]);
                fileName = new String(fileNameBytes).trim();
                log("Nom du fichier : " + fileName);
                fileRecu = new File("received/"+fileName);
                osRecu = new FileOutputStream(fileRecu);
                osRecu.write("Test pour voir si le fichier s'écrit bien".getBytes());
                log("Fichier reçu créé.");
                //log("Premier acknowledgement écrit.");
                /**buf = "Message recu!".getBytes();
                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
                log("Réponse envoyée.");**/

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //socket.close();
    }

    private synchronized static void log(String msg) throws IOException {
        // Crée un time stamp assez clean
        osLog.write(("[" + new Timestamp(System.currentTimeMillis()).toString().substring(11,21) + "] [Serveur] : ").getBytes());
        osLog.write(msg.trim().getBytes());
        // Change de ligne
        osLog.write(10);
    }
}