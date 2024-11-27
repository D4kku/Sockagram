package de.uulm.in.vs.grn.p3a;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SockagramClient {
    private final Socket socket;
    private final OutputStream out;
    private final InputStream in;

    public SockagramClient(String host, int port) throws Exception{
        this.socket = new Socket(host,port); // definier die OutputStreams lokal da sonst der Inhalt Unpredictable wird(nvm vllt doch anders idk)
        this.out = socket.getOutputStream();
        this.in = socket.getInputStream();
    }

    public static void main(String[] args){
        StringBuilder fileName = new StringBuilder("20240918_110728.jpg");// TODO: Make adresse changable
        String filter = "5";
        SockagramClient sock = null; // TODO: Muss inputs als adressen und port nutzen
        try {
            sock = new SockagramClient("vns.lxd-vs.uni-ulm.de",7777);
        } catch (Exception e) {
            throw new RuntimeException(e);//lmao
        }
        try {//todo: maybe implement ui
            Path file = Paths.get(fileName.toString());
            System.out.println(file.toAbsolutePath());
            sock.sendImage(filter,Files.readAllBytes(file));

            file = Paths.get(fileName.insert(fileName.indexOf("."),"_"+filter).toString()); //füge eine 2 zum datei namen hinzu
            System.out.println("New Name: " + file);
            Files.write(file,sock.getImageResponse());//dafuq passiert hier
            System.out.println("File received");

            sock.closeConnection();
        } catch (IOException e){

        }
    }

    public void sendImage(String filter, byte[] fileData) throws IOException{
        byte filterByte = Byte.parseByte(filter);
        out.write(createRequest(filterByte,fileData));
        out.flush();
    }
    //Returns the specified Image
    //Daniel hat recht man sollte das eigentlich mit InputStreams machen und nicht wie ich es mache
    // wegen memory effizienz aber dann müsste ich die Request und response in datenStrukturen wrappen
    // und die dann weiter Verarbeiten
    public byte[] getImageResponse() throws IOException{ //TODO: maybe rewrite mit records weil es funkt zwar aber ist memory ineffizient
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        int respCode = in.read();
        int payloadLen = ByteBuffer.wrap(in.readNBytes(4)).getInt();
        System.out.println(respCode + " . " + payloadLen);
        if(respCode == 0){
            return in.readNBytes(payloadLen);//memory sagt bye bye
        }else{ //weird error handling
            throw new NoSuchFileException(in.readNBytes(payloadLen).toString());//gibt den error string zu der exception hinzu
        }
    }
    //Creates the Request for the given filter and data
    private byte[] createRequest(byte filter, byte[] data) throws IOException{
        int datLen = data.length;
        int headLen = 5;//constant offset for the length of the header in byte (1 byte for the byte + 4 byte for the int)
        ByteBuffer req = ByteBuffer.allocate(headLen + datLen); // das ging auch als ByteOutputStream aber für hier recht egal und nio ist toll
        req.put(filter);
        req.putInt(datLen);
        req.put(data);
        return req.array();
    }


    public void closeConnection() throws IOException{
        this.in.close();
        this.out.close();
        this.socket.close();
    }
}
