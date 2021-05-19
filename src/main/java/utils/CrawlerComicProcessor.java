package utils;

import entity.Comic;
import entity.ComicChapter;
import entity.ComicPic;
import entity.FreeProxy;
import parser.DmzjParser;
import parser.DmzjParserImpl;
import run.RunProperties;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import java.util.List;

public class CrawlerComicProcessor implements PageProcessor {
    private Site site = Site.me().setCycleRetryTimes(3000).setCharset("UTF-8");
    //对漫画标题、话数等进行处理
    @Override
    public void process(Page page) {
        Comic comic=null;
        List<ComicChapter>chapterList=null;
        //解析器
        DmzjParser parser=new DmzjParserImpl();
        //代理
        while(RunProperties.proxy==null||ProxyPool.proxyConnectTest(RunProperties.proxy)!=true){
            System.out.println("代理正在测试..");
        }
        System.out.println("代理测试完成:连接成功!");
        comic=RunProperties.comic;
        if(comic!=null){
            //章节列表
            chapterList=parser.getChapters(comic.getComicId(),RunProperties.proxy);
            //重试十次
            if(chapterList==null){
                for(int i=0;i<10;i++){
                    chapterList=parser.getChapters(comic.getComicId(),RunProperties.proxy);
                    if(chapterList!=null){
                        break;
                    }
                }
            }
            if(null!=chapterList){
                //都不为空才放入域交给pipeline处理
                page.putField("comic",comic);
                page.putField("chapterList",chapterList);
            }else{
                System.out.println("章节列表为空！");
            }
        }else {
            System.out.println("查询漫画结果为空！");
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
