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
            pdu.getPacket().setData(dataFichier);
        }else{
            dataFichier = Files.readAllBytes(Paths.get(filename));
            if(dataFichier.length > 200){
                while(dataFichier.length > 200){
                    byte[] temp = new byte[200];
                    System.arraycopy(dataFichier,0,temp,0,200);
                    pdu.getPacket().setData(temp);
                    QuoteClient.log("Couche application effectuée.");
                    Transport.ajouterCouche(pdu, sequenceNumber, QuoteClient.ni);
                    Liaison.ajouterCouche(pdu, QuoteClient.socket, QuoteClient.address, QuoteClient.portName);
                    System.arraycopy(dataFichier, 200, dataFichier, 0, dataFichier.length-200);
                    sequenceNumber++;
                }
            }else{
                pdu.getPacket().setData(dataFichier);
                QuoteClient.log("Couche application effectuée.");
            }
        }
    }
}
