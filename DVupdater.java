import java.util.Timer;
import java.util.TimerTask;

/*
One thread for sending a DV update (that should happen every n seconds)
 */
public class DVupdater implements Runnable{
    public Router router;
    public Timer timer;

    public int n; //time period of auto update
    private int cnt; //count how many updates

    public DVupdater(Router r, int period){
        this.router = r;
        n = period;
    }

    @Override
    public void run() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                router.autoBroadcast();
            }
        }, 0, n*1000);

    }
}
