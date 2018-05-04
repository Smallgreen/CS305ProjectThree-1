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

                this.operate(data, bytes[0]);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void operate(String data, byte type){
        //format: type destIp destPort srcIp srcPort msg

        switch(type){
            //normal message
            case(0): {

                String[] msg = data.split(" ");
                //if port = dest port
                String content = "";
                if (Integer.parseInt(msg[1]) == router.getPort()) {
                    for (int i = 4; i < msg.length; i++) {
                        content += msg[i];
                    }
                    System.out.println("Message received from " + msg[2] + " : " + Integer.parseInt(msg[3])
                            + " message: " + content);
                } else {
                    router.sendMessage(data, msg[0], Integer.parseInt(msg[1]));
                }
                break;
            }

            //dv
            case(1):{

                //System.out.println(data);
                //format: srcIP port#ip : port weight#
                String[] msgDV = data.split("#");

                //set this neighbor's dv
                String src = msgDV[0];
                String[] srcAdd = src.split(" ");
                String srcIp = srcAdd[0];
                String srcPort = srcAdd[1];

                System.out.println("new dv received from " + srcIp + " : " + srcPort + " with the following distances:");

                if(router.getNeighbor(Integer.parseInt(srcPort)) == null){
                    router.addNeighbor(new Neighbor(srcIp, Integer.parseInt(srcPort), Integer.MAX_VALUE, router));
                }

                //router.getNeighbor(Integer.parseInt(srcPort)).startTimer();

                DistanceVector dv = new DistanceVector(msgDV[1], router);
                System.out.println(dv.getDV());

                if(router.updateDV(dv, Integer.parseInt(srcPort))){
                    if(router.dvAlgorithm()){
                        //System.out.println("aaa");
                        router.autoBroadcast();
                    }
                }
                break;
                }

            //weight
            case (2): {
                //format: weight srcIP srcPort
                //2 127.0.0.1 9876
                //System.out.println(data);
                String[] msg = data.split(" ");
                int weight = Integer.parseInt(msg[0]);
                String srcIp = msg[1];
                int srcPort = Integer.parseInt(msg[2]);

                System.out.println("get"+router.getNeighbor(srcPort).getPort());
                //if null, new neighbor
                if(router.getNeighbor(srcPort) == null){
                    System.out.println("here");
                    router.addNeighbor(new Neighbor(srcIp, srcPort, weight, router));
                }
                else{
                    if(router.updateLocalWeight(weight, srcIp, srcPort)){

                        if(router.dvAlgorithm()){
                            //System.out.println("aaa");
                            router.autoBroadcast();
                        }
                    }
                }
                //System.out.println("new weight to neighbor "+router.getIp()+" : "+ router.getPort() + " " + );
                break;
            }
        }

    }


}
