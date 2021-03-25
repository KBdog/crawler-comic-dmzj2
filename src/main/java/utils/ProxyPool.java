package utils;

import entity.FreeProxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

/*
从快代理上获取的代理
自制代理池
 */
public class ProxyPool {
    static String []proxyPool={
            "163.125.19.142:8888",
            "183.88.129.39:8080",
            "113.160.208.255:8080",
            "117.94.140.237:9999",
            "202.142.158.114:8080",
            "119.82.245.162:6060",
            "67.206.213.198:999",
            "185.198.188.55:8080",
            "183.88.232.207:8080",
            "183.89.64.13:8080",

            "221.5.80.66:3128",
            "14.97.2.107:80",
            "201.91.82.155:3128",
            "167.71.40.51:3128",
            "163.172.47.182:3128",
            "172.104.75.30:8080",
            "39.106.223.134:80",
            "59.29.245.151:3128",
            "218.60.8.99:3129",
            "221.182.31.54:8080",
    };

    //获得代理
    public static FreeProxy getProxy(){
        FreeProxy proxy;
        int num=(int)(Math.random()*10*2);
//        System.out.println(math);
        if(num==10||num==20){
            return getProxy();
        }else {
            String []proxyString=proxyPool[num].split(":");
            proxy=new FreeProxy();
            proxy.setIp(proxyString[0]);
            proxy.setPort(Integer.parseInt(proxyString[1]));
            System.out.println("当前代理:"+proxyPool[num]+"!");
            return proxy;
        }
    }

    //测试代理连接
    public static boolean proxyConnectTest(FreeProxy freeProxy){
        boolean flag=false;
        HttpURLConnection connection=null;
        InputStream inputStream=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        try {
//            URL url=new URL("http://icanhazip.com/");
            URL url=new URL("http://ip.3322.net/");
            connection=(HttpURLConnection)url.openConnection(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(freeProxy.getIp(),freeProxy.getPort())));
            connection.connect();
            inputStream = connection.getInputStream();
            if(null!=inputStream){
                System.out.println("连接代理成功！");
                flag=true;
                //io流操作
                inputStreamReader=new InputStreamReader(inputStream);
                bufferedReader=new BufferedReader(inputStreamReader);
                String tmp=bufferedReader.readLine();
                System.out.println(tmp);
            }
        }catch (IOException e) {
            System.out.println("连接代理失败！");
        }finally {
            try {
                if(bufferedReader!=null) {
                    bufferedReader.close();
                }
                if(inputStreamReader!=null){
                    inputStreamReader.close();
                }
                if(inputStream!=null){
                    inputStream.close();
                }
                if(connection!=null){
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
}
