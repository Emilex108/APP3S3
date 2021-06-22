package packet;

import java.io.*;
import java.net.DatagramPacket;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class PacketDecoder {

    private static String fileName;
    private static File fileRecu;
    private static OutputStream osRecu;

    public static boolean checkValidity(DatagramPacket packet){
        byte[] crc = new byte[8];
        System.arraycopy(packet.getData(), packet.getData()[14]+15, crc, 0, 8);
        byte[] tempData = new byte[packet.getData()[14]+15];
        System.arraycopy(packet.getData(), 0, tempData, 0, packet.getData()[14]+15);
        Checksum checksum = new CRC32();
        checksum.update(tempData, 0, tempData.length);
        long checksumValue = checksum.getValue();
        long checksumCheck = 0;

        for(int i = 0; i< 8; i++){
            checksumCheck <<= 8;
            checksumCheck |= (crc[i] & 0xFF);
        }

        //System.out.println("Checksum Value VS Checksum Check : " + checksumValue + " " + checksumCheck);
        if(checksumValue == checksumCheck){
            return true;
        }else{
            return false;
        }
    }

    public static void saveFile(DatagramPacket packet) throws IOException {
        if(packet.getData()[0] == 0){
            byte[] fileNameBytes = new byte[packet.getData()[14]];
            System.arraycopy(packet.getData(), 15, fileNameBytes, 0, packet.getData()[14]);
            fileName = new String(fileNameBytes).trim();
            fileRecu = new File("received/"+fileName);
            osRecu = new FileOutputStream(fileRecu, true);
        }else{
            byte[] data = new byte[packet.getData()[14]];
            System.arraycopy(packet.getData(), 15, data, 0, packet.getData()[14]);
            osRecu.write(data);
        }
    }
}
