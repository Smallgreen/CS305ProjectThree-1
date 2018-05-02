import java.io.*;
import java.net.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class UDP {

    private DatagramSocket routerSocket;

    public UDP(DatagramSocket rs){
        this.routerSocket = rs;
    }

/*
    send message
    format: Message msg from ipAddr:port to ipAddr:port forwarded to ip:port
            msg(the actual message with the concatenated ip addresses and port numbers)
 */
    public void sendMessage(String msg, DatagramSocket dest) throws IOException{


    }

}
