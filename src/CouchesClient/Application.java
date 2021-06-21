package CouchesClient;

import client.QuoteClient;
import packet.PDUMaison;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Application {
    public static void ajouterCouche(PDUMaison pdu, String filename, int sequenceNumber) throws IOException {
        byte[] dataFichier;
        if(sequenceNumber == 0){
            dataFichier = filename.getBytes();
        }else{
            //TODO: Fragmentation
            dataFichier = Files.readAllBytes(Paths.get(filename));
            if(dataFichier.length > 200){
                
            }
        }
        pdu.getPacket().setData(dataFichier);
        QuoteClient.log("Couche application effectuée.");
    }
}
