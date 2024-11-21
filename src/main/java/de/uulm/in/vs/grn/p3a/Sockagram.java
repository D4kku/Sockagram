package de.uulm.in.vs.grn.p3a;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;

public class Sockagram {

    public static void main(String[] args) throws Exception {
        byte[] bytes = ByteBuffer.allocate(2).putShort((short) 10).array();
        for (byte b : bytes) {
            System.out.format("0x%x ", b);
        }



        /*
        //error handling ist für anfänger
        Socket socket = new Socket("vns.lxd-vs.uni-ulm.de",7777); // TODO: Muss inputs als adressen und port nutzen
        OutputStream out = socket.getOutputStream();// MUSS MANUEL GEFLUSHED WERDEN!!
        InputStream in = socket.getInputStream();

        //Seperate requestHeader and data is easier to work with
        byte[] requestHeader =constructTCPSegmentHeader(socket.getLocalPort(),socket.getPort());
        byte[] data = null;

        out.write(requestHeader);
        out.write(data);
        out.flush();
        */
    }

    private static byte[] constructTCPSegmentHeader(int srcPort, int destPort) throws IOException {
        //Construct the segment as a Bytebuffer
        ByteBuffer segement = ByteBuffer.allocate(20);

        /* TCP Segment Header Structure:
        *
        *   <---------------------------------32 bit---------------------------------->
        *   +-------------------------------------------------------------------------+
        *   |               SrcPort              |               DestPort             |
        *   |                            sequence Number                              |
        *   |                                 ack Number                              |
        *   | headLen | unused | URG | ACK | PSH | RST | SYN | FIN |  receive Window  | Not to scale in this line as it would make it unreadable
        *   |            checksum                |           urg Data Pointer         |
        *   +-------------------------------------------------------------------------+
        *
        * This Only represents the The Part of the TCP segment that gets constructed in this Function
        * The Tcp standard features a bit more but we dont use options at all and its allowed to be len 0
        * The Only thing missing which we use is the Data but we create that in a different Function.
        */
        int seqNum = 0;//sollte global sein
        segement.putShort((short) srcPort);
        segement.putShort((short) destPort);
        segement.putInt(seqNum);

        return null;
    }

    private static byte[] intToByteArray(int in,int len){
        //TODO: Das ist sogar zu Cursed für mich
        // Das ist eine sünde gegen gott aber BITE ME God Is dead and we have killed him
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        //TODO ensure bite length by prepending 0
        String str = Integer.toBinaryString(in);
        for(int i = 0; i< len ;i++){
            arr.write(Byte.parseByte(str.substring(i*8,i*8+8)));//das sollte passen aber könnte auch violently explodieren
        }
        return arr.toByteArray();
    }
    private static byte[] stringToByteArray(String strIn,int len){
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        StringBuilder str = new StringBuilder(strIn);
        //Prepends Zeros so its always multiples of 8 long
        str.insert(0,str.repeat("0",strIn.length()%8));
        for(int i = 0; i < str.length()/8;i++){
            byteBuffer.put(Byte.parseByte(str.substring(i*8,(i*8+8)),2));//why the fuck are 8 bits too much for a byte???
        }
        return byteBuffer.array();
    }

}
