import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Router {

    private boolean reverse;
    private String ip;
    private int port;

    private DatagramSocket socket;

    private UDP sender;
    private RouterReceiver routerRcv;
    private cmdReader creader;

    //port - weight
    private static ArrayList<Neighbor> neighborList;
    private Map<Integer, Neighbor> forwardingTable;

    public Router(String ip, int port, boolean isReverse){
        this.reverse = isReverse;
        this.ip = ip;
        this.port = port;

        forwardingTable = new HashMap<>();

        //init socket
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("failed");
            e.printStackTrace();
        }

        sender = new UDP(socket);
    }

    public void startThreads(){
        //start receiver
        routerRcv = new RouterReceiver(this);
        Thread routerRcvThread = new Thread(routerRcv);
        routerRcvThread.start();

        //start cmdReader
        creader = new cmdReader(this);
        Thread creaderThread = new Thread(creader);
        creaderThread.start();

    }

    public void fowardMessage(String msg, String destIp, int destPort){

        try {
            sender.sendMessage(msg, destIp, destPort, ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg, String destIp, int destPort){
        Neighbor nextHop = forwardingTable.get(destPort);

        if(nextHop == null){
            return;
        }

        fowardMessage(msg, nextHop.getIp(), nextHop.getPort());
        //fowardMessage(msg, destIp, destPort);
    }

    public static void addNeighbor(Neighbor n){
        neighborList.add(n);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    //0-reverse 1-neighbors.txt
    public static void main(String[] args){

        boolean isReverse = false;
        String filePath;
        Router r;
        neighborList = new ArrayList<>();

        //get input
        if(args.length < 1){
            System.out.println("Needed [-reverse] [filepath]");
            return;
        }

        if(args[0].equals("-reverse")){
            isReverse = true;
            filePath = args[1];
        }
        else{
            filePath = args[0];
        }

        if(filePath == null) return;


        //read file
        BufferedReader br = null;
        ArrayList<String> inputs = new ArrayList<>();


        try {
            br = new BufferedReader(new FileReader(filePath));
            String lines = br.readLine();

            while(lines != null){
                inputs.add(lines);
                lines = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //init router r
        String routerAddress = inputs.get(0);
        String[] rAdd = routerAddress.split(" ");
        String rIP = rAdd[0];
        int rPort = Integer.parseInt(rAdd[1]);

        r = new Router(rIP, rPort, isReverse);

        System.out.println(rIP +" " + rPort);

        //add neighbors
        for(int i = 1; i < inputs.size(); i++){
            String neighbor = inputs.get(i);
            String[] n = neighbor.split(" ");
            String nIP = n[0];
            int nPort = Integer.parseInt(n[1]);
            int weight = Integer.parseInt(n[2]);

        Neighbor newNeighbor = new Neighbor(nIP, nPort, weight, r);
        addNeighbor(newNeighbor);


        }
        r.startThreads();


    }


}
