package test.tool;

import parser.DmzjParser;
import parser.DmzjParserImpl;
import run.RunProperties;

public class TestGetAllComic {
    public static void main(String[] args) {
        DmzjParser parser=new DmzjParserImpl();
        parser.getComicList("无职转生", RunProperties.proxy);
    }
}
