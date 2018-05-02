public class Neighbor {

    private String ip;
    private int port;
    private int weight;

    Router r;

    Neighbor(String ip, int port, int weight, Router r){
        this.ip = ip;
        this.port = port;
        this.weight = weight;
        this.r = r;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
