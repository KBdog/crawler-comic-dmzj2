package test.tool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import entity.ComicChapter;
import entity.Comic;
import entity.ComicPic;
import entity.FreeProxy;
import parser.DmzjParser;
import parser.DmzjParserImpl;
import utils.JSONFormatTool;
import utils.ProxyPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class TestDmzjParser {
    public static void main(String[] args) {
//        testGetComicMsg();
        getProxy();
    }

//    public static void testGetComicMsg(){
//        DmzjParser parser=new DmzjParserImpl();
//        Scanner in=new Scanner(System.in);
//        String keyword=in.nextLine();
//        //查询漫画简介信息
//        Comic comic_msg =parser.getComic(keyword);
//        System.out.println("关键字:"+keyword);
//        System.out.println("搜索结果第一个漫画id:"+comic_msg.getComicId());
//        System.out.println("漫画名:"+comic_msg.getComicName());
//        //查询漫画章节
//        if(null!=parser.getChapters(comic_msg.getComicId())){
//            List<ComicChapter> chapters_list=parser.getChapters(comic_msg.getComicId());
//            System.out.println("---------------章节目录-----------------");
//            for(ComicChapter chapter:chapters_list){
//                System.out.println(chapter.getChapterId()+":"+chapter.getChapterName());
//                //每章图片url
//                List<ComicPic> pageUrl= parser.getPictures(comic_msg.getComicId(),chapter.getChapterId());
//                for(ComicPic pic:pageUrl){
//                    System.out.println("----"+pic.getUrl());
//                }
//            }
//        }else {
//            System.out.println("漫画章节为空！");
//        }
//    }

    public static void getProxy() {
        URL url=null;
        Proxy proxy=null;
        HttpURLConnection connection=null;
        InputStream inputStream=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        FreeProxy freeProxy=null;
        try {
            //url
            url=new URL("http://sacg.dmzj1.com/comicsum/search.php?s=无职转生");
            //代理
            freeProxy=ProxyPool.getProxy();
            proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(freeProxy.getIp(),freeProxy.getPort()));
            //开启连接
            connection=(HttpURLConnection) url.openConnection(proxy);
            //连接等待时间
            connection.setConnectTimeout(5000);
            //io流操作
            inputStreamReader=new InputStreamReader(connection.getInputStream());
            bufferedReader=new BufferedReader(inputStreamReader);
            String comicJson=bufferedReader.readLine();
            //截掉定义字符串
            String []comic_array=comicJson.split("var g_search_data = ");
            //截掉最后语句分号
            String result_comic_array=comic_array[1].substring(0,comic_array[1].length()-1);
            //解析json
            JSONArray jsonArray=JSONArray.parseArray(result_comic_array);
            //获取第一个搜索结果
            JSONObject firstComicJson=(JSONObject) jsonArray.get(0);
            System.out.println(JSONFormatTool.formatJSON(firstComicJson));
        } catch (IOException e) {
            System.out.println("代理失效！");
            e.printStackTrace();
        } finally {
            try {
                if(bufferedReader!=null){
                    bufferedReader.close();
                }
                if(inputStreamReader!=null){
                    inputStreamReader.close();
                }
                if(connection!=null){
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
