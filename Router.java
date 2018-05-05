import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Router {

    private boolean reverse;
    private String ip;
    private int port;

    private DatagramSocket socket;

    private UDP sender;
    private RouterReceiver routerRcv;
    private cmdReader creader;
    private DistanceVector dv;
    private DVupdater dvp;

    //port - weight
    private ArrayList<Neighbor> neighborList;
    private Map<Integer, Neighbor> forwardingTable;

    public Router(String ip, int port, boolean isReverse){
        this.reverse = isReverse;
        this.ip = ip;
        this.port = port;

        neighborList = new ArrayList<>();
        forwardingTable = new ConcurrentHashMap<>();
        dv = new DistanceVector();

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

        dvp = new DVupdater(this, 2);
        Thread dvpThread = new Thread(dvp);
        dvpThread.start();

    }


    public void forwardMessage(String msg, String destIp, int destPort){

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

        forwardMessage(msg, nextHop.getIp(), nextHop.getPort());

        //forwardMessage(msg, destIp, destPort);
    }

    public void updateWeight(int newWeight, String destIp, int destPort){
        sender.sendWeight(newWeight, destIp, destPort, ip, port);
    }

    public synchronized boolean updateLocalWeight(int newWeight, String destIp, int destPort){

        System.out.println(neighborList);
        Neighbor n = getNeighbor(destPort);
        int oldWeight = n.getWeight();

        if(oldWeight == newWeight){
            return false;
        }
        else {
            n.setWeight(newWeight);
            return true;
        }
    }

    public void autoBroadcast(){
        if(reverse){
            for(Neighbor n : neighborList){
                DistanceVector temp = new DistanceVector();
                //copy the entries
                for(Map.Entry<Neighbor, Integer> d: dv.getMap().entrySet()){
                    temp.getMap().put(d.getKey().copy(), d.getValue());
                }
                for(Integer port: forwardingTable.keySet()){
                    int nextHopPort = forwardingTable.get(port).getPort();
                    if(nextHopPort == n.getPort() && port!= nextHopPort){
                        temp.removeVector(forwardingTable.get(port));
                    }
                }
                sender.sendDV(temp, n.getIp(), n.getPort(), ip, port);
            }

        }
        else{
            for(Neighbor n: neighborList){
                DistanceVector temp = new DistanceVector();
                //copy the entries
                for(Map.Entry<Neighbor, Integer> d: dv.getMap().entrySet()){
                    temp.getMap().put(d.getKey().copy(), d.getValue());
                }
                sender.sendDV(temp, n.getIp(), n.getPort(), ip, port);
            }
        }

    }


    public boolean dvAlgorithm(){
        boolean isUpdated = false;

        DistanceVector temp = new DistanceVector();
        //copy the entries
        for(Map.Entry<Neighbor, Integer> d: dv.getMap().entrySet()){
            temp.getMap().put(d.getKey().copy(), d.getValue());
        }

        dv.getMap().clear();

        initDV(dv);

        //iterate through every neighbor's dv
        for(Neighbor neighbor: neighborList){
            DistanceVector nDV = neighbor.getDv();

//            if(nDV.getMap() == null){
//                System.out.println("aaa");
//            }
            for(Neighbor dest : nDV.getMap().keySet()){
                //if it is not go back to itself, find the min distance
                //compare cur distance to dest and dist to neighbor + neighbor to dest
                if(dest.getPort() != port){
                    Integer dist = neighbor.getWeight() + nDV.getDist(dest);
                    Integer curDist = dv.getDist(dest);

                    if(curDist == null){
                        dv.update(dest, dist);
                        forwardingTable.put(dest.getPort(), neighbor);
                    }
                    else if(dist < curDist){
                        dv.update(dest, dist);
                        forwardingTable.put(dest.getPort(), neighbor);
                    }
                }


            }
        }
//
//        System.out.println("temp " + temp.getMap());
//        System.out.println("dv "+dv.getMap());

        if(!temp.equals(dv)){
            isUpdated = true;
        }

        //System.out.println("equal "+temp.getMap().equals(dv.getMap()));

        if(isUpdated){
//            new dv calculated:
//            ipAddress:port distance nextHopIpAddress:nextHopPort
            System.out.println("new dv calculated:");
            for(Neighbor n : dv.getMap().keySet()){
                System.out.println(n.getIp() + " : " + n.getPort() + " " + dv.getDist(n)
                + " nextHopIpAddress: " + forwardingTable.get(n.getPort()).getIp()
                        + " : " +forwardingTable.get(n.getPort()).getPort());
            }

        }
        return isUpdated;

    }

    public boolean updateDV(DistanceVector udv, int uport){
        Neighbor un = getNeighbor(uport);

        //System.out.println("udv "+udv.getDV());
        //System.out.println("un "+un.getDv().getDV());
        if(un.getDv().equals(udv)){
            return false;
        }
        un.setDV(udv);
        return true;
    }

    public Neighbor getNeighbor(int nport){

        for(Neighbor n : neighborList){
            if(n.getPort() == nport){
                return n;
            }
        }
        return null;
    }


    private void initDV(DistanceVector dv){
        //add all neighbors into dv
        for(Neighbor neighbor: neighborList){
            dv.update(neighbor, neighbor.getWeight());
            forwardingTable.put(neighbor.getPort(), neighbor);
        }
    }


    public void printDV(){
        System.out.println("DV: " + dv.getDV());
    }



    public void addNeighbor(Neighbor n){
        neighborList.add(n);
        System.out.println("add neighbor");
        //recalculate forwarding table and run dv alg
        if(dvAlgorithm()){
            autoBroadcast();
        }
    }


    public synchronized void dropNeighbor(int dPort){
        Neighbor n = getNeighbor(dPort);
        if(neighborList.contains(n)){

            neighborList.remove(n);
            forwardingTable.remove(n);
            for(int destPort: forwardingTable.keySet()){
                if(forwardingTable.get(destPort).getPort() == n.getPort()){
                    forwardingTable.remove(destPort);
                }
            }
            System.out.println("neighbor " + n.getIp() + " : " + n.getPort() + " dropped");

        }

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
        r.addNeighbor(newNeighbor);


        }
        r.startThreads();

    }


}
