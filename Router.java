import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class Router {

    private boolean reverse;
    private String ip;
    private int port;

    //port - weight
    private static ArrayList<Neighbor> neighborList;

    public Router(String ip, int port, boolean isReverse){
        this.reverse = isReverse;
        this.ip = ip;
        this.port = port;
    }

    public static void addNeighbor(Neighbor n){
        neighborList.add(n);
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

    }


}
