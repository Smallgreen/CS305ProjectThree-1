import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RouterReceiver implements Runnable{

    private Router router;

    RouterReceiver(Router r){
        this.router = r;
    }

    @Override
    public void run() {

        try {
            //DatagramSocket listeningSocket = new DatagramSocket(router.getPort());
            DatagramSocket listeningSocket = router.getSocket();
            while(true){
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                listeningSocket.receive(receivePacket);
                byte[] bytes = receivePacket.getData();
                String data = new String(bytes).trim();
                //System.out.println(bytes[0]);
                operate(data, bytes[0]);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void operate(String data, byte type){
        //format: type destIp destPort srcIp srcPort msg

        switch(type){
            //normal message
            case(0):
                String[] msg = data.split(" ");
                //if port = dest port
                String content = "";
                if(Integer.parseInt(msg[1]) == router.getPort()){
                    for(int i = 4; i < msg.length; i++) {
                        content += msg[i];
                    }
                    System.out.println("Message received from " + msg[2] +" : "+ Integer.parseInt(msg[3])
                                        + " message: " + content);
                }
                else{
                    router.sendMessage(data, msg[0], Integer.parseInt(msg[1]));
                }
                break;

            //dv
            case(1):{

                //set this neighbor's dv

                }
                break;
        }

    }


}
