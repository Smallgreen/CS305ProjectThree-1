public class Neighbor {

    private String ip;
    private int port;
    private int weight;

    private DistanceVector dv;

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
