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
            Transport.ajouterCouche(pdu, sequenceNumber, QuoteClient.ni);
            Liaison.ajouterCouche(pdu, QuoteClient.socket, QuoteClient.address, QuoteClient.portName);
        }else{
            dataFichier = Files.readAllBytes(Paths.get(filename));
            if(dataFichier.length > 100){
                do{
                    byte[] temp = new byte[100];
                    System.arraycopy(dataFichier,0,temp,0,100);
                    pdu.getPacket().setData(temp);
                    QuoteClient.log("Couche application effectuée.");
                    Transport.ajouterCouche(pdu, sequenceNumber, QuoteClient.ni);
                    Liaison.ajouterCouche(pdu, QuoteClient.socket, QuoteClient.address, QuoteClient.portName);
                    byte[] temp2 = new byte[dataFichier.length-100];
                    System.arraycopy(dataFichier, 100, temp2, 0, dataFichier.length-100);
                    dataFichier = temp2;
                    sequenceNumber++;
                }while(dataFichier.length > 100);
                byte[] temp = new byte[dataFichier.length];
                System.arraycopy(dataFichier,0,temp,0,dataFichier.length);
                pdu.getPacket().setData(temp);
                QuoteClient.log("Couche application effectuée.");
                Transport.ajouterCouche(pdu, sequenceNumber, QuoteClient.ni);
                Liaison.ajouterCouche(pdu, QuoteClient.socket, QuoteClient.address, QuoteClient.portName);
            }else{
                pdu.getPacket().setData(dataFichier);
                Transport.ajouterCouche(pdu, sequenceNumber, QuoteClient.ni);
                Liaison.ajouterCouche(pdu, QuoteClient.socket, QuoteClient.address, QuoteClient.portName);
                QuoteClient.log("Couche application effectuée.");
            }
        }
    }
}
