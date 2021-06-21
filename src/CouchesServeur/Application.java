package CouchesServeur;

import packet.PDUMaison;
import server.QuoteServerThread;

import java.io.IOException;

public class Application {
    public static void ajouterCouche(PDUMaison pdu, boolean valide) throws IOException {
        if(valide){
            pdu.getPacket().setData(new byte[]{1});
        }else{
            pdu.getPacket().setData(new byte[]{0});
        }
        QuoteServerThread.log("Couche application effectuée.");
    }
}
