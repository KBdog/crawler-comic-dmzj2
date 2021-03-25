package run;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import utils.CrawlerComicPipeline;
import utils.CrawlerComicProcessor;
import utils.OldComicPipeline;

public class RunProgram {
    public static void main(String[] args) {
        Spider spider=Spider.create(new CrawlerComicProcessor());
        spider.addPipeline(new CrawlerComicPipeline())
                .setDownloader(new HttpClientDownloader())
                .addUrl("https://www.baidu.com")
                .start();
    }
}
