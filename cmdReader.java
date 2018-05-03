import java.util.Scanner;

public class cmdReader implements Runnable{

//    PRINT
//    MSG <dst-ip> <dst-port> <msg>
//    CHANGE <dst-ip> <dst-port> <new-weight>

    private Router r;
    private Scanner sc;

    public cmdReader(Router r){
        this.r = r;
        sc = new Scanner(System.in);
    }

    @Override
    public void run() {

        while(sc.hasNextLine()){

            String line = sc.nextLine();
            Scanner lineSc = new Scanner(line);

            if(lineSc.hasNext()){
                String cmd = lineSc.next();

                switch (cmd){
                    case("MSG"): {
                        String destIP = lineSc.next();
                        int destPort = Integer.parseInt(lineSc.next());
                        String msg = "";

                        //get msg content
                        if(lineSc.hasNext()){
                            msg += lineSc.next();

                            while(lineSc.hasNext()){
                                msg += " " + lineSc.next();
                            }
                        }

                        r.sendMessage(msg, destIP, destPort);
                        return;
                    }

                    case("PRINT"): {

                    }

                    case("CHANGE"): {

                    }
                }
            }

        }

    }
}
