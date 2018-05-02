import java.io.*;
import java.net.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class UDP {

    private DatagramSocket srcSocket;

    public UDP(DatagramSocket rs){
        this.srcSocket = rs;
    }

/*
    send message
    format: Message msg from ipAddr:port to ipAddr:port forwarded to ip:port
            msg(the actual message with the concatenated ip addresses and port numbers)
 */
    public void sendMessage(String msg, Neighbor dest) throws IOException{
        //calculate new msg size = msg length + next hop's ip + port
        int length = 0;
        byte splitter = 120;
        String address = dest.getIp() + "-" + Integer.toString(dest.getPort());

        length = msg.length() + 1 + address.length();

        byte[] sentMsg = new byte[length];

        int curIndex = 0;

        //fill in the contents
        byte[] msgContent = msg.getBytes();
        for(int i = 0; i < msg.length(); i++){
            sentMsg[i] = msgContent[i];
        }

        sentMsg[msg.length()] = splitter;

        byte[] add = address.getBytes();
        int temp = 0;
        for(int i = msg.length() + 1; i < curIndex + 1 + address.length(); i++){
            sentMsg[i] = add[temp];
            temp++;
        }

        sendHelper(sentMsg, dest.getIp(), dest.getPort());


    }

    public void sendHelper(byte[] msg, String ip, int port){
        try {
            InetAddress ipAdress = InetAddress.getByName(ip);
            DatagramPacket packet = new DatagramPacket(msg, msg.length, ipAdress, port);
            srcSocket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
