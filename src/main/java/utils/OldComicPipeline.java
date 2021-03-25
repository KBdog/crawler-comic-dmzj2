package utils;

import entity.Comic;
import entity.ComicChapter;
import entity.ComicPic;
import parser.DmzjParser;
import parser.DmzjParserImpl;
import run.RunProperties;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import java.io.*;
import java.net.*;
import java.util.List;
/*
该pipeline已废弃
 */
public class OldComicPipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
        DmzjParser parser=new DmzjParserImpl();
        Comic comic = resultItems.get("comic");
        List<ComicChapter> chapterList= resultItems.get("chapterList");
        List<ComicPic> picList=null;
        if(null!=comic&&null!=chapterList){
            System.out.println("漫画名:"+comic.getComicName());
            for(int i=chapterList.size()-1;i>=0;i--){
                ComicChapter chapter=chapterList.get(i);
                System.out.println("章节名:"+chapter.getChapterName());
                picList = parser.getPictures(comic.getComicId(), chapter.getChapterId(), RunProperties.proxy);
                //下载
                downLoadImg(comic.getComicName(),chapter.getChapterName(),picList);
            }
        }
    }

    private void downLoadImg(String comicName,String chapterName,List<ComicPic>picList){
        URL url=null;
        HttpURLConnection connection=null;
        //io流
        InputStream inputStream=null;
        BufferedInputStream bufferedInputStream=null;
        BufferedOutputStream bufferedOutputStream=null;
        //图片
        File img=null;
        FileOutputStream fileOutputStream=null;

        //存放爬虫的根目录
        File rootDirectory=new File(RunProperties.crawlerDirectory);
        if (!rootDirectory.exists() && !rootDirectory.isDirectory()) {
            rootDirectory.mkdir();
        }
        //存放漫画的目录
        File comicDirectory=new File(RunProperties.crawlerDirectory+"/"+comicName);
        if (!comicDirectory.exists() && !comicDirectory.isDirectory()) {
            comicDirectory.mkdir();
        }
        //存放单话的目录
        File chapterDirectory=new File(RunProperties.crawlerDirectory+"/"+comicName+"/"+chapterName);
        if (!chapterDirectory.exists() && !chapterDirectory.isDirectory()) {
            chapterDirectory.mkdir();
        }


        for(int i=0;i<picList.size();i++){
            try {
                ComicPic pic=picList.get(i);
                final String picUrl=pic.getUrl();
                img=new File(RunProperties.crawlerDirectory+"/"+comicName+"/"+chapterName+"/第"+(i+1)+"页.jpg");
                if(!img.exists()){
                    img.createNewFile();
                }
                fileOutputStream=new FileOutputStream(img);
                //图片链接
                url=new URL(picUrl);
                connection=(HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP,
                        new InetSocketAddress(RunProperties.proxy.getIp(),RunProperties.proxy.getPort())));
                connection.setRequestProperty("referer","http://imgsmall.dmzj.com/");
                connection.connect();
                inputStream=connection.getInputStream();
                //输入流
                bufferedInputStream=new BufferedInputStream(inputStream);
                //输出流
                bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
                //下载
                int length;
                byte[] cache = new byte[1024 * 20];
                while ((length = bufferedInputStream.read(cache, 0, cache.length)) != -1) {
                    fileOutputStream.write(cache, 0, length);
                }
                //控制台监控下载过程
                if(i==picList.size()-1){
                    System.out.println(picList.size()+"/"+picList.size());
                    System.out.println(chapterName+"下载完成!");
                    System.out.println("---------------------");
                }else{
                    System.out.println((i+1)+"/"+(picList.size()));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bufferedOutputStream.close();
                    fileOutputStream.close();
                    bufferedInputStream.close();
                    inputStream.close();
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
