package packetClient;

import packet.Builder;
import packet.PDUMaison;

public class PDUBuilder implements Builder {
    PDUMaison pdu = new PDUMaison();

    @Override
    public void ajouterCoucheApplication() {

    }

    public void ajouterCoucheApplication(String filename) {

    }

    @Override
    public void ajouterCoucheTransport() {

    }

    @Override
    public void ajouterCoucheLiaison() {

    }

    @Override
    public PDUMaison getPacket() {
        return null;
    }


}
