import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

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
                operate(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void operate(byte[] data){
        byte type = data[0];

        switch(type){
            case(0): {
                //get content till splitter
                ArrayList<Byte> contentlist = new ArrayList<>();
                int index = 1;
                while(data[index] != 120){
                    contentlist.add(data[index]);
                    index++;
                }
                Byte[] temp = contentlist.toArray(new Byte[contentlist.size()]);
                byte[] content = toPrimitives(temp);

                handleContent(content);

                System.out.println(content.toString());

            }
        }

    }

    private void handleContent(byte[] data){

        //check if it is at the dest

    }

    private byte[] toPrimitives(Byte[] Bytes)
    {
        byte[] bytes = new byte[Bytes.length];

        for(int i = 0; i < Bytes.length; i++) {
            bytes[i] = Bytes[i];
        }

        return bytes;
    }
}
