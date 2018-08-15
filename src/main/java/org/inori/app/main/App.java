package org.inori.app.main;

import org.inori.app.utils.HttpClientUtils;
import org.inori.app.utils.JsoupHtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * App
 * @author InoriHimea
 * @version 1.0
 * @date 2018/8/8 13:51
 * @since jdk1.8
 */
public class App {

    private static final String MAVEN_CENTRAL_REPOSITORY_URL = "http://repo2.maven.org/maven2/";
    private static final String TARGET_FILE = "maven-metadata.xml";

    public static void main(String[] args) {

        try {
            Document topDoc = Jsoup.connect(MAVEN_CENTRAL_REPOSITORY_URL).timeout(100000).get();
            Elements aTags = topDoc.getElementsByTag("a");

            List<String> topList = new LinkedList<String>();
            if (! JsoupHtmlUtils.hasTargetFile(aTags, TARGET_FILE)) {
                for (String text : aTags.eachText()) {
                    if (! text.equals("../") && text.endsWith("/")) {
                        topList.add(text);
                        System.out.println("---顶级目录添加一个元素---");
                    }
                }
            }

            getTarget4Next(topList);

            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:/temp.txt")));
            for (String text : topList) {
                bw.write(text);
                bw.newLine();
            }

            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getTarget4Next(List<String> topList) throws IOException {
        List<String> secondList = new LinkedList<String>();
        for (String text : topList) {
            System.out.println(text);
            Document document = Jsoup.connect(MAVEN_CENTRAL_REPOSITORY_URL + text).timeout(1000000000).get();
            Elements a = document.getElementsByTag("a");

            if (! JsoupHtmlUtils.hasTargetFile(a, TARGET_FILE)) {
                for (String aText : a.eachText()) {
                    if (! aText.equals("../") && aText.endsWith("/")) {
                        secondList.add(text + aText);
                        System.out.println("---次级目录添加一个元素---");
                    }
                }
            }
        }

        topList.clear();
        topList.addAll(secondList);
        secondList.clear();

        getTarget4Next(topList);
    }
}
