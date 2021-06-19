import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

public class QuoteServerThread extends Thread {

    private DatagramSocket socket = null;
    protected BufferedReader in = null;
    private File fileRecu;
    private OutputStream osRecu;
    private NetworkInterface ni;
    private int currentPacketNumber = 0;
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
                ni = NetworkInterface.getByInetAddress(packet.getAddress());
                log("Packet reçu.");
                //TODO: Check validité du packet avec le CRC

                // Sauvegarde le nom du fichier
                //TODO: Enleve le size du header à l'array de byte?
                String fileName;
                byte[] fileNameBytes = new byte[receivedData.length];
                //Set le currentPacketNumber à celui qu'il vient de recevoir
                currentPacketNumber = packet.getData()[0];
                log("Taille du data reçu : " + packet.getData()[14]);
                //Enleve le header de receivedData
                System.arraycopy(receivedData, 15, fileNameBytes, 0, packet.getData()[14]);
                fileName = new String(fileNameBytes).trim();
                log("Nom du fichier : " + fileName);
                //Crée le fichier vide recu dans le dossier received
                fileRecu = new File("received/"+fileName);
                osRecu = new FileOutputStream(fileRecu);
                //Test d'écrire quelque chose dedans
                osRecu.write("Test pour voir si le fichier s'écrit bien".getBytes());
                log("Fichier reçu créé.");
                //Écrit le packet de retour
                writeWithProtocolACK(packet);

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

    private void writeWithProtocolACK(DatagramPacket packet) throws IOException {
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        //16 -> Header (15) + 1 byte de ACK
        byte[] ack = new byte[16];
        //Ajoute le header dans le buffer de ack
        System.arraycopy(createHeaderACK(), 0, ack, 0, 15);
        log("Header créé pour le ACK");
        //Ajoute le flag de validité du ACK (1 si bien recu, 0 si mal recu?)
        //TODO: Parler a Florent pour le tag de validité
        ack[15] = 1;
        packet = new DatagramPacket(ack, ack.length, address, port);
        socket.send(packet);
        log("ACK envoyé.");
    }

    /**
     * Crée un header pour un data spécifique
     * @return Un header complet avant CRC
     */
    private byte[] createHeaderACK() throws SocketException {
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
        //Ajoute le délimiteur 01111110
        header[1] = (byte) '~';
        for(int i = 2; i < 8; i++){
            //Check si l'adresse est la même (send/receive)
            //Sinon remplacer la deuxième assignation par l'adresse locale
            //TODO: Check si l'adresse contient 6 bytes (Print length) et check si c'est MAC address
            header[i] = ni.getHardwareAddress()[i-2];
            header[i+6] = ni.getHardwareAddress()[i-2];
        }
        //Ajoute la longueur du message dans le header
        header[14] = 1;
        return header;
    }
}