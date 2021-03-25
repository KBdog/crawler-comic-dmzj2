package parser;

import entity.ComicChapter;
import entity.Comic;
import entity.ComicPic;
import entity.FreeProxy;

import java.util.List;

public interface DmzjParser {
    //根据关键字搜索获得漫画信息(废弃)
    @Deprecated
    public Comic getComic(String keyword, FreeProxy freeProxy);
    //根据漫画id获得漫画章节和章节id信息
    public List<ComicChapter> getChapters(int comic_id,FreeProxy freeProxy);
    //根据漫画id和章节id获得详细章节信息(该章的所有图片url)
    public List<ComicPic> getPictures(int comic_id,int chapter_id,FreeProxy freeProxy);
    //根据关键字获得所有漫画列表
    public List<Comic> getComicList(String keyword,FreeProxy freeProxy);
}
