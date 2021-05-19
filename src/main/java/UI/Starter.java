package UI;

import entity.Comic;
import parser.DmzjParser;
import parser.DmzjParserImpl;
import run.RunProperties;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import utils.CrawlerComicPipeline;
import utils.CrawlerComicProcessor;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Scanner;

public class Starter {
    public static void main(String[] args) {
        //监控程序结束线程
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if(RunProperties.spider!=null){
                    System.out.println("漫画已下载完毕,程序结束！");
                }else {
                    System.out.println("程序结束！");
                }
            }
        }));
        //程序开始
        DmzjParser parser=new DmzjParserImpl();
        Scanner in=new Scanner(System.in);
        System.out.print("请输入漫画存放目录(若不设置默认为"+RunProperties.crawlerDirectory+"):");
        String directory=in.nextLine();
        if(directory!=null&&!directory.equals("")){
            RunProperties.crawlerDirectory=directory;
        }
        System.out.println("存放目录:"+RunProperties.crawlerDirectory);
        System.out.println("-----------------------");
        System.out.print("请输入漫画关键字进行搜索:");
        String keyword=in.nextLine();
        List<Comic> comicList=null;
        if(keyword!=null&&!keyword.equals("")){
            System.out.println("关键字:"+keyword);
            System.out.println("关键字长度:"+keyword.length());
            comicList=parser.getComicList(keyword, RunProperties.proxy);
        }
        System.out.println("-----------------------");
        if(comicList!=null){
            int i=1;
            for(Comic comic:comicList){
                if(comic!=null){
                    System.out.println(i+"-"+comic.getComicName());
                    i++;
                }
            }
            System.out.println("-----------------------");
            System.out.print("请输入您要爬的漫画序号:");
            RunProperties.comic=comicList.get(Integer.parseInt(in.nextLine())-1);
            //启动
            RunProperties.spider=Spider.create(new CrawlerComicProcessor());
            RunProperties.spider.addPipeline(new CrawlerComicPipeline())
                    .setDownloader(new HttpClientDownloader())
                    .addUrl("https://www.baidu.com")
                    .thread(5)
                    .start();
        }else {
            System.out.println("漫画不存在!");
        }
        in.close();
    }
}
