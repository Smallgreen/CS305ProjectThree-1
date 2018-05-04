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
        timer = new Timer();
        startTimer();
    }

    final void startTimer() {
        timer.cancel();
        timer = new Timer();
        //this task drop the neighbor from the current router that it's contained in
        TimerTask dropNeighborTask = new TimerTask() {
            @Override
            public void run() {
                r.dropNeighbor(port);
            }
        };
        timer.schedule(dropNeighborTask, n * t * 1000);
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
}
