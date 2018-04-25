import java.util.Timer;

/*
One thread for sending a DV update (that should happen every n seconds)
 */
public class DVupdater implements Runnable{
    public Router router;
    public Timer timer;

    public DVupdater(Router r){
        this.router = r;
    }

    @Override
    public void run() {

    }
}
