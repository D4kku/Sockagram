package de.uulm.in.vs.grn.p3a;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;

public class SockagramClient {
    private final Socket socket;
    private final OutputStream out;
    private final InputStream in;

    public SockagramClient(String host, int port) throws Exception{
        this.socket = new Socket(host,port); // definier die OutputStreams lokal da sonst der Inhalt Unpredictable wird(nvm vllt doch anders idk)
        this.out = socket.getOutputStream();
        this.in = socket.getInputStream();
    }

    public void main(String[] args){
        //error handling ist für anfänger
        SockagramClient sock = null; // TODO: Muss inputs als adressen und port nutzen
        try {
            sock = new SockagramClient("vns.lxd-vs.uni-ulm.de",7777);
        } catch (Exception e) {
            throw new RuntimeException(e);//lmao
        }
        try {
            sock.sendImage(null, null);


            //TODO: Seperate close methode für alle dinge lmao
            sock.socket.close();
        } catch (IOException e){

        }
    }

    public void sendImage(String filter, byte[] data) throws IOException{
        byte filterByte = 0; //TODO: Implement the filter Type(ist aber noch nicht auf moodle)
        out.write(createRequest(filterByte,data));
        out.flush();
    }
    //Returns the specified Image
    //Daniel hat recht man sollte das eigentlich mit InputStreams machen und nicht wie ich es mache
    // wegen memory effizienz aber dann müsste ich die Request und response in datenStrukturen wrappen
    // und die dann weiter Verarbeiten
    public byte[] getImageResponse() throws IOException{ //TODO: rewrite mit Records das Error handling wird sonst komisch
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        int respCode = in.read();
        int payloadLen = ByteBuffer.wrap(in.readNBytes(4)).getInt();
        if(respCode == 1){
            return null;
        }else{
            return null;
        }
    }
    private byte[] createRequest(byte filter, byte[] data) throws IOException{
        int datLen = data.length;
        int headLen = 4;//constant offset for the length of the header
        ByteBuffer req = ByteBuffer.allocate(headLen + datLen); // das ging auch als ByteOutputStream aber für hier recht egal und nio ist toll
        req.put(filter);
        req.putInt(datLen);
        req.put(data);
        return req.array();
    }





    /* Das brauchen wir garnicht aber ich will das nicht einfach löschen ich hab da zuviel zeit reingesunken
    Dachte wieso auch immer das wir selber die TCP connection machen müssen was komplett dumm ist aber schlafentzug und lrs sind keine gute kombo

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
    */
}
