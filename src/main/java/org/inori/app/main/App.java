package org.inori.app.main;

import org.inori.app.utils.HttpClientUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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

    public static void main(String[] args) {
        //String text = HttpClientUtils.doGet(MAVEN_CENTRAL_REPOSITORY_URL);
        //System.out.println(text);

        try {
            Document topDoc = Jsoup.connect(MAVEN_CENTRAL_REPOSITORY_URL).get();
            Elements aTags = topDoc.getElementsByTag("a");

            int size = aTags.size();
            System.out.println(size);

            /**
             * 读取一级目录
             */
            List<String> firstGroupIdList = new LinkedList<String>();
            List<String> xmlList = new LinkedList<String>();
            List<String> txtList = new LinkedList<String>();
            for (Element aTag : aTags) {
                if (aTag.hasText()) {
                    String text = aTag.text();

                    if (! text.equals("../") && text.endsWith("/")) {
                        firstGroupIdList.add(text);
                    }

                    if (text.endsWith(".xml")) {
                        xmlList.add(text);
                    }

                    if (text.endsWith(".txt")) {
                        txtList.add(text);
                    }
                }

            }

            for (String firstGroupId : firstGroupIdList) {
                Document secondaryDoc = Jsoup.connect(MAVEN_CENTRAL_REPOSITORY_URL + firstGroupId).get();

                Elements secTags = secondaryDoc.getElementsByTag("a");

                for (Element secTag : secTags) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
