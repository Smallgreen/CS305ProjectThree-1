import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistanceVector {
    private Map<Neighbor, Integer> dvector;
    private Router srcRouter;

    public DistanceVector(){
        dvector = new ConcurrentHashMap<>();
    }

    public DistanceVector(String vector, Router r){
        dvector = new ConcurrentHashMap<>();
        srcRouter = r;
        //format ip port weight&ip port weight
        String[] vectors = vector.split("&");

        for(String v :vectors){
            String[] info = v.split(" ");
            String ip = info[0];
            int port = Integer.parseInt(info[2]);
            int weight = Integer.parseInt(info[3]);
            Neighbor n = new Neighbor(ip, port, weight, srcRouter);
            dvector.put(n, weight);
        }

    }

    public void removeVector(Neighbor n){
        dvector.remove(n);
    }

    public String getDV() {
        String dv = "";

        for(Neighbor n: dvector.keySet()){
            dv += n.getIp() + " : " + n.getPort() + " " + dvector.get(n) + "\n";
        }

        return dv;
    }

    public void update(Neighbor n, Integer weight){
        dvector.put(n,weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DistanceVector) {
            DistanceVector dv = (DistanceVector) obj;
            return this.dvector.equals(dv.dvector);
        }
        return false;
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
        //str += srcRouter.getIp()+ " " + srcRouter.getPort() + " " + getDV();
        for(Neighbor n: dvector.keySet()){
            str += n.getIp() + " : " + n.getPort() + " " + n.getWeight() + "&";
        }
        //String str = getDV();
        return str;
    }
}
