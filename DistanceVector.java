import java.util.HashMap;
import java.util.Map;

public class DistanceVector {
    private Map<Neighbor, Integer> dvector;
    private Router srcRouter;

    public DistanceVector(){
        dvector = new HashMap<>();
    }

    public DistanceVector(String vector, Router r){
        srcRouter = r;
        //format ip port weight&ip port weight
        String[] vectors = vector.split("&");

        for(String v :vectors){
            String[] info = v.split(" ");
            String ip = info[0];
            int port = Integer.parseInt(info[1]);
            int weight = Integer.parseInt(info[2]);
            Neighbor n = new Neighbor(ip, port, weight, r);
            dvector.put(n, weight);
        }

    }

    public void removeVector(Neighbor n){
        dvector.remove(n);
    }

    public String getDV() {
        String dv = "";

        for(Neighbor n: dvector.keySet()){
            dv += n.getIp() + " : " + n.getPort() + n.getWeight() + "\n";
        }

        return dv;
    }

    public void update(Neighbor n, Integer weight){
        dvector.put(n,weight);
    }

    public Integer getDist(Neighbor n){
        return dvector.get(n);
    }

    public Map<Neighbor, Integer> getMap(){
        return dvector;
    }

    @Override
    public String toString() {
        String str = "";
        str += srcRouter.getIp() + srcRouter.getPort() + getDV();

        return str;
    }
}
