package de.uulm.in.vs.grn.p3a;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;

public class Sockagram {
    private int seqNum;
    private int ackNum;
    private final Socket socket;
    private final short srcPort;
    private final short destPort;

    public Sockagram(String host, int port) throws Exception{
        //TODO gib den sinnvolle werte
        this.seqNum = 0;
        this.ackNum = 0;
        this.socket = new Socket(host,port); // definier die OutputStreams lokal da sonst der Inhalt Unpredictable wird(nvm vllt doch anders idk)
        this.srcPort = (short) this.socket.getLocalPort();
        this.destPort = (short) this.socket.getPort();
    }

    public void main(String[] args) throws Exception {
        //error handling ist für anfänger
        Sockagram sock = new Sockagram("vns.lxd-vs.uni-ulm.de",7777); // TODO: Muss inputs als adressen und port nutzen
        sock.sendMessage(constructTCPSegmentHeader(false,false,false),null);


    }
    //muss noch schauen ob ich das nur für den Handshake nutze oder auch für mehr
    public void sendMessage(byte[] requestHeader,byte[] data){
        try (OutputStream out = this.socket.getOutputStream()) {
            out.write(requestHeader);
            out.write(data);
            out.flush();
        }catch (Exception e){

        }
    }

    //Das ist eigentlich echt dumm wie ich das mache aber ich hab kein bock mehr das umzuschreiben
    private byte[] constructTCPSegmentHeader(Boolean ackBit, Boolean synBit, Boolean finBit) throws IOException {

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
        //Construct the segment as a Bytebuffer with the size we defined above
        ByteBuffer segment = ByteBuffer.allocate(20);

        //---First Line
        segment.putShort((short) srcPort);
        segment.putShort((short) destPort);
        //---Second Line
        segment.putInt(this.seqNum);
        segment.putInt(this.ackNum);
        //--- Third Line
        segment.put((byte) 0); //set Dataoffset and reserved to zero
        //set the flags
        int flags = 0; //int macht mehr sinn da sonst bei jeder operation zu byte ge typcasted werden muss
        flags = ackBit ? flags | (1 << 4) : flags;//TODO : schauen ob die shifts überhaupt passen
        flags = synBit ? flags | (1 << 1) : flags;
        flags = finBit ? flags | (1 << 0) : flags;
        segment.put((byte) flags);
        segment.putShort((short) 0); //TODO: sets the window Size
        //Fourth Line
        segment.putShort((short) 0); //TODO: checksum
        segment.putShort((short) 0); // Urgend Data bit (not used by us)
        return segment.array();
    }
}
