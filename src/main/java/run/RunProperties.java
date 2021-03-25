package run;

import entity.Comic;
import entity.FreeProxy;
import us.codecraft.webmagic.Spider;

import java.util.List;

public class RunProperties {
    //默认目录是d:/dmzj
    public static String crawlerDirectory="D:/dmzj/";
    public static Comic comic=null;
    public static FreeProxy proxy= new FreeProxy("127.0.0.1",1080);
    public static Spider spider=null;
//    public static FreeProxy proxy= new FreeProxy("59.29.245.151",3128);
//    public static FreeProxy proxy= null;
}
