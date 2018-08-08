package org.inori.app.main;

import org.inori.app.utils.HttpClientUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * App
 * @author InoriHimea
 * @version 1.0
 * @date 2018/8/8 13:51
 * @since jdk1.8
 */
public class App {

    private static final String MAVEN_CENTRAL_REPOSITORY_URL = "http://repo2.maven.org/maven2/";

    public static void main(String[] args) {
        //String text = HttpClientUtils.doGet(MAVEN_CENTRAL_REPOSITORY_URL);
        //System.out.println(text);

        try {
            Document document = Jsoup.connect(MAVEN_CENTRAL_REPOSITORY_URL).get();
            Elements aTags = document.getElementsByTag("a");
            int size = aTags.size();
            System.out.println(size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
