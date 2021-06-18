import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

public class QuoteClient {

    private static DatagramSocket socket;
    private static InetAddress address;
    private static int portName = 27841;

    private static OutputStream os;

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }

        socket = new DatagramSocket();

        address = InetAddress.getByName(args[0]);

        File file = new File("liaisonDeDonnees.log");
        os = new FileOutputStream(file, true);

        writeWithProtocol("one-liners.txt");

        socket.close();
    }

    private static void writeWithProtocol(String fileName) throws IOException {
        //--------------------COUCHE D'APPLICATION
        //Créer un espace mémoire
        byte[] bufferFichier = new byte[32768];
        //Charge le fichier
        bufferFichier = Files.readAllBytes(Paths.get(fileName));
        log("Fichier lu.");
        //--------------------COUCHE DE TRANSPORT
        //Premier packet contient : Le nom du fichier
        DatagramPacket packet = new DatagramPacket(fileName.getBytes(), fileName.getBytes().length, address, portName);
        log("Premier packet créé.");
        //TODO: Ajouter un header ? + CRC
        socket.send(packet);
        log("Premier packet envoyé.");
        //Recevoir la réponse (Acknowledgement)
        byte[] bufferReception = new byte[32768];
        packet = new DatagramPacket(bufferReception, bufferReception.length);
        socket.receive(packet);
        System.out.println(new String(packet.getData(), StandardCharsets.UTF_8));
        log(new String(packet.getData(), StandardCharsets.UTF_8));
    }

    private static void log(String msg) throws IOException {
        // Crée un time stamp assez clean
        os.write(("[" + new Timestamp(System.currentTimeMillis()).toString().substring(11,21) + "] [Client] : ").getBytes());
        os.write(msg.trim().getBytes());
        // Change de ligne
        os.write(10);
    }
}