import java.util.Timer;
import java.util.TimerTask;

/*
One thread for sending a DV update (that should happen every n seconds)
 */
public class DVupdater implements Runnable{
    public Router router;
    public Timer timer;

    private int timeCnt;

    public int n; //time period of auto update

    public DVupdater(Router r, int period){
        this.router = r;
        n = period;
        timeCnt = 0;
    }

    @Override
    public void run() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                router.autoBroadcast();
                timeCnt++;
                System.out.println("auto update at time "+getTime());
            }
        }, 0,n*1000);
    }

    public void stopTimer(){
        timer.cancel();
        timeCnt = 0;
    }

    public int getTime(){
        return timeCnt*n;
    }
}
