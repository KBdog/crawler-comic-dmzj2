package entity;

/*
*漫画章节信息
* chapter_name:章节名
* chapter_id:章节id
 */
public class ComicChapter {
    private String chapterName;
    private int chapterId;

    public ComicChapter() {
    }

    public ComicChapter(String chapterName, int chapterId) {
        this.chapterName = chapterName;
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }
}
