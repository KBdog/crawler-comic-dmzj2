package entity;

public class Comic {
    private int comicId;
    private String comicName;

    public Comic() {
    }

    public Comic(int comicId, String comicName) {
        this.comicId = comicId;
        this.comicName = comicName;
    }

    public int getComicId() {
        return comicId;
    }

    public void setComicId(int comicId) {
        this.comicId = comicId;
    }

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }
}
