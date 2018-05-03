import java.util.Scanner;

public class cmdReader implements Runnable{

//    PRINT
//    MSG <dst-ip> <dst-port> <msg>
//    CHANGE <dst-ip> <dst-port> <new-weight>

    private Router r;

    public cmdReader(Router r){
        this.r = r;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){

            String line = sc.nextLine();
            Scanner lineSc = new Scanner(line);

            if(lineSc.hasNext()){
                String cmd = lineSc.next();


                switch (cmd){
                    case("MSG"): {
                        String destIP = lineSc.next();
                        String dPort = lineSc.next();
                        int destPort = Integer.parseInt(dPort);
                        String msg = "";

                        msg += destIP;
                        msg += " " +dPort;

                        msg += " " +r.getIp();
                        msg += " " +r.getPort();

                        //get msg content
                        while(lineSc.hasNext()){
                            msg += " " + lineSc.next();
                        }

                        r.sendMessage(msg, destIP, destPort);
                        break;
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
