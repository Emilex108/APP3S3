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
    private static NetworkInterface ni;
    private static int portName = 27841;
    private static int currentPacketNumber = 0;
    private static OutputStream os;

    public static void main(String[] args) throws IOException {

        socket = new DatagramSocket();
        address = InetAddress.getLocalHost();
        ni = NetworkInterface.getByInetAddress(address);

        File file = new File("liaisonDeDonnees.log");
        os = new FileOutputStream(file, true);

        writeWithProtocol("one-liners.txt");

        socket.close();
    }

    /**
     * Suit notre protocole maison pour envoyer 1 fichier
     * @param fileName Nom du fichier à envoyer
     * @throws IOException Si problème à lire le fichier
     */
    private static void writeWithProtocol(String fileName) throws IOException {
        //--------------------COUCHE D'APPLICATION
        //Créer un espace mémoire
        byte[] dataFichier;
        //Charge le fichier
        dataFichier = Files.readAllBytes(Paths.get(fileName));
        log("Fichier lu.");
        //--------------------COUCHE DE TRANSPORT
        //Crée un header
        byte[] dataHeader = createHeader(fileName.getBytes());
        //Crée le data total avant CRC
        byte[] dataTotalAvantCRC = new byte[dataHeader.length+fileName.getBytes().length];
        System.arraycopy(dataHeader, 0, dataTotalAvantCRC, 0, dataHeader.length);
        System.arraycopy(fileName.getBytes(), 0, dataTotalAvantCRC, dataHeader.length, fileName.getBytes().length);
        //TODO: Crée le CRC

        //TODO: Ajoute le CRC au data total

        //Premier packet contient : Le nom du fichier
        //TODO: Modifier le packet pour qu'il contienne le bon data
        DatagramPacket packet = new DatagramPacket(dataTotalAvantCRC, dataTotalAvantCRC.length, address, portName);
        log("Premier packet créé.");
        socket.send(packet);
        log("Premier packet envoyé.");
        //Recevoir la réponse (Acknowledgement)
        /**byte[] bufferReception = new byte[32768];
        packet = new DatagramPacket(bufferReception, bufferReception.length);
        socket.receive(packet);
        System.out.println(new String(packet.getData(), StandardCharsets.UTF_8));
        log(new String(packet.getData(), StandardCharsets.UTF_8));**/
        //TODO: Prendre une décision selon le message de ACK (Renvoyer ou non)
        //TODO: Même processus total pour le data du fichier
    }

    /**
     * Permet de sauvegarder des logs
     * @param msg Message a enregistrer
     * @throws IOException Si erreur dans le fichier
     */
    private static void log(String msg) throws IOException {
        // Crée un time stamp assez clean
        os.write(("[" + new Timestamp(System.currentTimeMillis()).toString().substring(11,21) + "] [Client] : ").getBytes());
        os.write(msg.trim().getBytes());
        // Change de ligne
        os.write(10);
    }

    /**
     * Crée un header pour un data spécifique
     * @param data Data à communiquer dans le packet
     * @return Un header complet avant CRC
     */
    private static byte[] createHeader(byte[] data) throws SocketException {
        /**
         * # de packet  = 1 byte
         * délimiteur = 1 byte
         * adresse destination = 6 bytes
         * adresse source = 6 bytes
         * taille du message = 1 byte
         * total = 15 bytes
         */
        //TODO: Ajouter place pour CRC
        byte[] header = new byte[15];
        //Ajoute le numéro de packet
        header[0] = (byte) (currentPacketNumber+1);
        //Ajoute le délimiteur (0) -> Simple car premier packet est 1
        header[1] = (byte) 01111110;
        for(int i = 2; i < 8; i++){
            //Check si l'adresse est la même (send/receive)
            //Sinon remplacer la deuxième assignation par l'adresse locale
            //TODO: Check si l'adresse contient 6 bytes (Print length) et check si c'est MAC address
            header[i] = ni.getHardwareAddress()[i-2];
            header[i+6] = ni.getHardwareAddress()[i-2];
        }
        //Ajoute la longueur du message dans le header
        header[14] = (byte) data.length;
        return header;
    }
}