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
import java.util.concurrent.*;

public class CrawlerComicPipeline implements Pipeline {
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
                System.out.println("-章节名:"+chapter.getChapterName());
                picList = parser.getPictures(comic.getComicId(), chapter.getChapterId(), RunProperties.proxy);
                //下载:五个线程，间隔0.5s
                downLoadImg(comic.getComicName(),chapter.getChapterName(),picList,5,500);
            }
        }
    }

    private void downLoadImg(String comicName,String chapterName,List<ComicPic>picList,final int threadsize, final long sleeptime){
        int count=0;
        int size=picList.size();
        System.out.println("--当前章节页数:"+picList.size());
        //开放多个线程进行并发下载
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadsize);
        CompletionService<String> cs = new ExecutorCompletionService<String>(fixedThreadPool);
        //遍历图片url集合
        for(int i=0;i<picList.size();i++){
            ComicPic pic=picList.get(i);
            final int page=i+1;
            final String picUrl=pic.getUrl();
            //提交
            cs.submit(new Callable<String>() {
                public String call() throws Exception {
                    try {
                        Thread.sleep(sleeptime);
                        //下载图片
                        return down(picUrl,page,chapterName,comicName);
                    } catch (InterruptedException e) {
                        System.out.println("线程异常");
                        return "error_" + "picUrl";
                    }
                }
            });
        }
        for (ComicPic pic : picList) {
            try {
                String a = cs.take().get();
                if (a != null) {
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (count == size) {
                    System.out.println("---"+count+"/"+size);
                    System.out.println("---"+chapterName+"下载完毕!");
                } else {
                    System.out.println("---"+count + "/" + size);
                }
            }
        }
        //关闭线程池
        fixedThreadPool.shutdown();
    }

    private String down(String picUrl,int pageNum,String chapterName,String comicName){
        File img=null;
        FileOutputStream fileOutputStream=null;
        URL url=null;
        HttpURLConnection connection=null;
        InputStream inputStream=null;
        BufferedInputStream bufferedInputStream=null;
        BufferedOutputStream bufferedOutputStream=null;
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
        try {
//            img=new File(RunProperties.crawlerDirectory+"/"+comicName+"/"+chapterName+"/第"+pageNum+"页.jpg");
            img=new File(chapterDirectory.getPath()+"/第"+pageNum+"页.jpg");
            if(!img.exists()){
                img.createNewFile();
            }
            fileOutputStream=new FileOutputStream(img);
            //图片链接
            url=new URL(picUrl.trim());
            connection=(HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(RunProperties.proxy.getIp(),RunProperties.proxy.getPort())));
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.addRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.104 Safari/537.36");
            connection.setRequestProperty("Referer","http://imgsmall.dmzj.com/");
//            connection.connect();
            inputStream=connection.getInputStream();
            //输入流
            bufferedInputStream=new BufferedInputStream(inputStream);
            //输出流
            bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
            //下载
            int length=0;
            byte[] cache = new byte[1024 * 20];
            while ((length = bufferedInputStream.read(cache, 0, cache.length)) != -1) {
//                fileOutputStream.write(cache, 0, length);
                bufferedOutputStream.write(cache,0,length);
            }
//            bufferedOutputStream.close();
//            fileOutputStream.close();
//            bufferedInputStream.close();
//            inputStream.close();
//            connection.disconnect();
            return "success_" + "picUrl";
        }catch (FileNotFoundException e){
            System.out.println("图片失效的章节:"+chapterName+"-----第"+pageNum+"页-----url:"+picUrl);
            return "error_" + "picUrl";
        } catch (IOException e) {
            System.out.println("下载异常的图片:"+chapterName+"-----第"+pageNum+"页-----url:"+picUrl);
            return down(picUrl,pageNum,chapterName,comicName);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("未知错误的章节:"+chapterName+"-----第"+pageNum+"页-----url:"+picUrl);
            return "error_" + "picUrl";
        }
        finally {
            try {
                if(bufferedOutputStream!=null)
                    bufferedOutputStream.close();
                if(fileOutputStream!=null)
                    fileOutputStream.close();
                if(bufferedInputStream!=null)
                    bufferedInputStream.close();
                if(inputStream!=null)
                    inputStream.close();
                if(connection!=null){
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
