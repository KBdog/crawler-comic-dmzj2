package entity;
/*
代理
 */
public class FreeProxy {
    private String ip;
    private int port;

    public FreeProxy() {
    }

    public FreeProxy(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
