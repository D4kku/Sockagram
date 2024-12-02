package de.uulm.in.vs.grn.p3a;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.NoSuchFileException;

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
    }

    public void sendImage(byte filter, byte[] fileData) throws IOException{
        out.write(createRequest(filter,fileData));
        out.flush();
    }

    //Returns the specified Image
    public byte[] getImageResponse() throws IOException{
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
