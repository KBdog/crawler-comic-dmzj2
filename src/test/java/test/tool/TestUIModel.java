package test.tool;

import entity.Comic;
import parser.DmzjParser;
import parser.DmzjParserImpl;
import run.RunProperties;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import utils.CrawlerComicPipeline;
import utils.CrawlerComicProcessor;

import java.util.List;
import java.util.Scanner;

public class TestUIModel {
    public static void main(String[] args) {
        DmzjParser parser=new DmzjParserImpl();
        Scanner in=new Scanner(System.in);
        System.out.print("请输入关键字进行搜索:");
        List<Comic> comicList=parser.getComicList(in.nextLine(), RunProperties.proxy);
        System.out.println("-----------------------");
        int i=1;
        for(Comic comic:comicList){
            if(comic!=null){
                System.out.println(i+"-"+comic.getComicName()+"-"+comic.getComicId());
                i++;
            }
        }
        System.out.println("-----------------------");
        System.out.print("请输入您要爬的漫画序号:");
        RunProperties.comic=comicList.get(Integer.parseInt(in.nextLine())-1);
        //启动
        Spider spider=Spider.create(new CrawlerComicProcessor());
        spider.addPipeline(new CrawlerComicPipeline())
                .setDownloader(new HttpClientDownloader())
                .addUrl("https://www.baidu.com")
                .start();
    }
}
