package packet;

import java.net.DatagramPacket;

public class PDUMaison {
    private DatagramPacket packet;

    public PDUMaison(){
        byte[] buffer = new byte[0];
        this.packet = new DatagramPacket(buffer, 0,null,0);
    }

    public DatagramPacket getPacket(){
        return this.packet;
    }

}
