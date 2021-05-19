package parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import entity.ComicChapter;
import entity.Comic;
import entity.ComicPic;
import entity.FreeProxy;
import utils.JSONFormatTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DmzjParserImpl implements DmzjParser {
    /*
    @param:keyword
    动漫之家搜索api:
    http://sacg.dmzj.com/comicsum/search.php?s=${comic/author}
    http://sacg.dmzj1.com/comicsum/search.php?s=${comic/author}
    关键字查询所有结果
    2021年1月31日14:48:31换新method,List<Comic> getComicList
     */
    @Override
    public Comic getComic(String keyword, FreeProxy freeProxy)  {
        URL url=null;
        HttpURLConnection connection=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        int id=-1;
        String name=null;
        Comic comic=new Comic();
        try {
            url=new URL("http://sacg.dmzj.com/comicsum/search.php?s="+keyword);
            connection=(HttpURLConnection) url.openConnection(
                    new Proxy(Proxy.Type.HTTP,new InetSocketAddress(freeProxy.getIp(),freeProxy.getPort())));
            connection.connect();
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
            //根据结果获取漫画id和name
            id=Integer.parseInt(firstComicJson.get("id").toString());
            name=firstComicJson.get("comic_name").toString();
            comic.setComicId(id);
            comic.setComicName(name);

        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭流
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader!=null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭连接
            if(connection!=null){
                connection.disconnect();
            }
        }
        return comic;
    }


    /*
    @param:comic_id
    漫画详情api:
    http://v2.api.dmzj.com/comic/${id}.json (旧)
    http://v3api.dmzj.com/comic/comic_${id}.json(新)
    http://v3api.dmzj1.com/comic/comic_${id}.json(新)
    https://api.dmzj.com/dynamic/comicinfo/34802.json
    漫画简介信息
     */
    @Override
    public List<ComicChapter> getChapters(int comic_id,FreeProxy freeProxy) {
        URL url=null;
        HttpURLConnection connection=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        ComicChapter chapter=null;
        //章节集合
        List<ComicChapter> chaptersList=new ArrayList<>();
        try {
            url=new URL("http://api.dmzj.com/dynamic/comicinfo/"+comic_id+".json");
            connection=(HttpURLConnection) url.openConnection
                    (new Proxy(Proxy.Type.HTTP,new InetSocketAddress(freeProxy.getIp(),freeProxy.getPort())));
//            connection.connect();
            //io流操作
            inputStreamReader=new InputStreamReader(connection.getInputStream());
            bufferedReader=new BufferedReader(inputStreamReader);

            //循环读取
            StringBuffer sb=new StringBuffer();
            int cache_size=1024;
            char[]cache=new char[cache_size];
            int length=0;
            while((length=bufferedReader.read(cache,0,cache_size))!=-1){
                sb.append(cache,0,length);
            }
            String comicJson=sb.toString();
            //判断是否是json数组:被隐藏了就会返回null，没有隐藏就返回正常json
            Boolean isJson=JSON.isValidObject(comicJson);
            if(isJson==true){
                //获取到漫画简介json
                JSONObject jsonObject=JSONObject.parseObject(comicJson);
                //获取data.list
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
                for(Object obj:jsonArray){
                    JSONObject tmp=(JSONObject) obj;
                    chapter=new ComicChapter();
                    chapter.setChapterName(tmp.getString("chapter_name"));
                    chapter.setChapterId(Integer.parseInt(tmp.getString("id")));
                    //把chapter加进集合
                    chaptersList.add(chapter);
                }
            }else {
                chaptersList=null;
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭流
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader!=null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭连接
            if(connection!=null){
                connection.disconnect();
            }
        }
        return chaptersList;
    }


    /*
    @param:chapter_id,comic_id
    漫画章节信息api:
    http://v3api.dmzj.com/chapter/${comic_id}/${chapter_id}.json
    http://v3api.dmzj1.com/chapter/${comic_id}/${chapter_id}.json
    https://m.dmzj.com/chapinfo/47139/92386.html
    漫画章节信息
     */
    @Override
    public List<ComicPic> getPictures(int comic_id, int chapter_id,FreeProxy freeProxy){
        URL url=null;
        HttpURLConnection connection=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        List<ComicPic> comicPicList=new ArrayList<>();
        ComicPic picUrl=null;
        try {
            url=new URL("https://m.dmzj.com/chapinfo/"+comic_id+"/"+chapter_id+".html");
            connection=(HttpURLConnection) url.openConnection(
                    new Proxy(Proxy.Type.HTTP,new InetSocketAddress(freeProxy.getIp(),freeProxy.getPort())));
            //io流操作
            inputStreamReader=new InputStreamReader(connection.getInputStream());
            bufferedReader=new BufferedReader(inputStreamReader);
            //循环读取
            StringBuffer sb=new StringBuffer();
            int cache_size=1024;
            char[]cache=new char[cache_size];
            int length=0;
            while((length=bufferedReader.read(cache,0,cache_size))!=-1){
                sb.append(cache,0,length);
            }
            String comicJson=sb.toString();
            //解析获得到的json字符串
            JSONObject jsonObject=JSONObject.parseObject(comicJson);
            //获取每一页的url json
            JSONArray urlObject= jsonObject.getJSONArray("page_url");
            //截掉前后的中括号
            String tmpUrlString=urlObject.toString().substring(1,urlObject.toString().length()-1);
            //根据逗号分割每个url
            String []urlList=tmpUrlString.split(",");
            for(int i=0;i<urlList.length;i++){
                //去掉头尾的引号
                String page_url=urlList[i].replaceAll("\"","").replaceAll("com//","com/");
                picUrl=new ComicPic();
                picUrl.setUrl(page_url);
                //把所有页面的url加入集合
                comicPicList.add(picUrl);
            }
            return comicPicList;
        } catch (IOException e) {
            System.out.println("io流异常_获取不到该话信息"+"_漫画id:"+comic_id+"_章节id:"+chapter_id);
            return getPictures(comic_id,chapter_id,freeProxy);
        }finally {
            //关闭流
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader!=null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭连接
            if(connection!=null){
                connection.disconnect();
            }
        }
    }

    //根据关键字获得所有漫画列表
    @Override
    public List<Comic> getComicList(String keyword, FreeProxy freeProxy) {
        URL url=null;
        HttpURLConnection connection=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        //返回的漫画列表
        List<Comic> comicList=null;
        //漫画实体
        Comic comic=null;
        try {
            url=new URL("http://sacg.dmzj.com/comicsum/search.php?s="+keyword);
            connection=(HttpURLConnection) url.openConnection(
                    new Proxy(Proxy.Type.HTTP,new InetSocketAddress(freeProxy.getIp(),freeProxy.getPort())));
//            connection.connect();
            //io流操作
            inputStreamReader=new InputStreamReader(connection.getInputStream());
            bufferedReader=new BufferedReader(inputStreamReader);
            //循环读取
            StringBuffer sb=new StringBuffer();
            int cache_size=1024;
            char[]cache=new char[cache_size];
            int length=0;
            while((length=bufferedReader.read(cache,0,cache_size))!=-1){
                sb.append(cache,0,length);
            }
            String comicJson=sb.toString();
            if(comicJson!=null){
                //获得到数据再实例化comicList
                comicList=new ArrayList<>();
                //截掉定义字符串
                String []comic_array=comicJson.split("var g_search_data = ");
                //截掉最后语句分号
                String result_comic_array=comic_array[1].substring(0,comic_array[1].length()-1);
                //解析json
                JSONArray jsonArray=JSONArray.parseArray(result_comic_array);
                for(int i=0;i<jsonArray.size();i++){
                    JSONObject object=jsonArray.getJSONObject(i);
                    //防止某些漫画名出现空格的现象不好创建文件，此处把空格全部去掉
                    String comicName=object.get("comic_name").toString().replaceAll(" ","");
                    int comicId=Integer.parseInt(object.get("id").toString().replaceAll(" ",""));
                    comic=new Comic();
                    comic.setComicName(comicName);
                    comic.setComicId(comicId);
                    comicList.add(comic);
                }
                return comicList;
            }
            //返回漫画列表
            return comicList;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            //关闭流
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader!=null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭连接
            if(connection!=null){
                connection.disconnect();
            }
        }
    }
}
