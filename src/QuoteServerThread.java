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
                byte[] buf = new byte[32768];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                log("Premier packet reçu.");
                // write data to file
                fileRecu = new File("testRecu.txt");
                osRecu = new FileOutputStream(fileRecu);
                osRecu.write(packet.getData());
                log("Premier acknowledgement écrit.");
                buf = "Message recu!".getBytes();

                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
                log("Réponse envoyée.");

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