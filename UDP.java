import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDP {

    private DatagramSocket srcSocket;

    public UDP(DatagramSocket rs){
        this.srcSocket = rs;
    }

/*

    send message
    format: msg ip-port ip-port ip-port …
    debug: Message msg from ipAddr:port to ipAddr:port forwarded to ip:port
            msg(the actual message with the concatenated ip addresses and port numbers)
 */
    public void sendMessage(String msg, String destIp, int destPort, String srcIp, int srcPort) throws IOException{
        //calculate new msg size = type + msg length + next hop's ip + port
        int length;
        byte type = 0;
        byte splitter = 120;
        String address = " " + destIp + " - " + Integer.toString(destPort);

        length = 1 + msg.length() + 1 + address.length();

        byte[] sentMsg = new byte[length];

        sentMsg[0] = type;

        //fill in the contents
        byte[] msgContent = msg.getBytes();
        for(int i = 1; i < msg.length() + 1; i++){
            sentMsg[i] = msgContent[i-1];
        }

        sentMsg[msg.length()] = splitter;

        byte[] add = address.getBytes();
        int temp = 0;
        for(int i = msg.length() + 2; i <  length; i++){
            sentMsg[i] = add[temp];
            temp++;
        }

        sendHelper(sentMsg, destIp, destPort);

        System.out.println("Message from " + srcIp + " : " + srcPort + " forward to " +
                destIp + " : " + destPort + " " + sentMsg.toString());

    }

    public void sendDV(){

    }

    public void sendWeight(){

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
