import java.util.Timer;
import java.util.TimerTask;

public class Neighbor {

    private String ip;
    private int port;
    private int weight;
    private Timer timer;
    private int n = 3;
    private int t = 10;

    private DistanceVector dv;

    private Router r;

    Neighbor(String ip, int port, int weight, Router r){
        this.ip = ip;
        this.port = port;
        this.weight = weight;
        this.r = r;
        dv = new DistanceVector();
    }

    final void reStartTimer() {

        System.out.println(port+" restart");
        timer.cancel();
        timer = new Timer();
        //this task drop the neighbor if didnt receive dv after 3
        TimerTask dropNeighborTask = new TimerTask() {
            @Override
            public void run() {
                r.dropNeighbor(port);
            }
        };
        timer.schedule(dropNeighborTask, n*t*1000);
    }

    public void startTimer() {

        System.out.println(port+" start");
        timer = new Timer();
        //this task drop the neighbor if didnt receive dv after 3
        TimerTask dropNeighborTask = new TimerTask() {
            @Override
            public void run() {
                r.dropNeighbor(port);
            }
        };
        timer.schedule(dropNeighborTask, n*t*1000);
    }

    void stopTimer() {
        timer.cancel();
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public int getWeight() {
        return weight;
    }

    public void setDV(DistanceVector dv){
        this.dv = dv;
    }

    public DistanceVector getDv() {
        return dv;
    }

    public Neighbor copy(){
        return new Neighbor(ip, port, weight, r);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Neighbor){
            Neighbor n = (Neighbor) obj;
            return this.ip.equals(n.ip) && this.port == n.port;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash =  ip.hashCode() + port;
        return hash;
    }

    public void setWeight(int newWeight){
        weight = newWeight;
    }
}
