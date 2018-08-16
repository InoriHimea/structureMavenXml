package org.inori.app.main;

import org.inori.app.utils.JsoupHttpUtils;
import org.inori.app.utils.MultiOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * App
 * @author InoriHimea
 * @version 1.0
 * @date 2018/8/8 13:51
 * @since jdk1.8
 */
public class App {

    private static final String MAVEN_CENTRAL_REPOSITORY_URL = "http://repo2.maven.org/maven2/";
    private static final String TARGET_NAME = "maven-metadata.xml";
    private static List<String> topElementsList = new LinkedList<String>();
    private static List<String> tempList = new LinkedList<String>();
    private static Set<String> targetSet = new LinkedHashSet<String>();

    public static void main(String[] args) {

        try (PrintStream ps = new PrintStream(new FileOutputStream("app.log"));
               MultiOutputStream outputStream = new MultiOutputStream(ps, System.out)) {
            //使用新的输出策略
            System.setOut(new PrintStream(outputStream));

            //搜索顶级元素
            getTopElementsList();

            //根据顶级节点元素递归搜索下一级元素节点，直到找到目标元素文本
            getTarget4Next();

            BufferedWriter writer = new BufferedWriter(new FileWriter("archive.txt"));
            if (topElementsList.size() > 0) {
                for (String parent : topElementsList) {
                    writer.write(parent);
                    writer.newLine();
                }
                writer.write("-----parent-------");
                writer.newLine();
            }

            if (tempList.size() > 0) {
                for (String temp : tempList) {
                    writer.write(temp);
                    writer.newLine();
                }
                writer.write("-----temp-----");
                writer.newLine();
            }

            for (String target : targetSet) {
                writer.write(target);
                writer.newLine();
            }
            writer.write("------");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    private static void getTopElementsList() {
        System.out.println("开始执行顶级节点搜索");

        try {
            Elements topTags = getDocElementsByTagName(null);

            if (! JsoupHttpUtils.hasTargetName(topTags, TARGET_NAME)) {
                for (Element topTag : topTags) {
                    String tagText = topTag.text();
                    if (! tagText.equals("../") && tagText.endsWith("/")) {
                        topElementsList.add(tagText);
                        System.out.println("添加一个顶级路径节点：" + tagText);
                    } else {
                        System.out.println("非匹配内容：" + tagText + "，已舍弃");
                    }
                }
            } else {
                targetSet.add(TARGET_NAME);
                System.out.println("当前父节点下已存在目标元素，已将目标元素的完整路径放入结果集：" + TARGET_NAME);
            }
        } catch (IOException e) {
            System.out.println("出现异常");
            System.out.println("异常信息：" + e.getMessage());
            System.out.println("异常原因：" + e.getCause());
        } finally {
            System.out.println("搜索顶级元素节点完成");
        }
    }

    private static void getTarget4Next() {
        System.out.println("开始执行次级节点搜索");
        System.out.println("当前父节点数量：" + topElementsList.size());
        System.out.println("当前临时节点数量：" + tempList.size());
        System.out.println("已匹配目标的数量：" + targetSet.size());

        if (topElementsList != null) {

            try {
                for (String parentName : topElementsList) {
                    Elements childrenTags = getDocElementsByTagName(parentName);

                    if (! JsoupHttpUtils.hasTargetName(childrenTags, TARGET_NAME)) {
                        for (Element childrenTag : childrenTags) {
                            String text = childrenTag.text();

                            if (! text.equals("../") && text.endsWith("/")) {
                                tempList.add(parentName + text);
                                System.out.println("添加一个次级路径节点：" + (parentName + text));
                            } else {
                                System.out.println("非匹配内容：" + text + "，已舍弃");
                            }
                        }
                    } else {
                        parentName += TARGET_NAME;
                        targetSet.add(parentName);
                        System.out.println("当前父节点下已存在目标元素，已将目标元素的完整路径放入结果集：" + parentName);
                    }
                }
            } catch (IOException e) {
                System.out.println("出现异常");
                System.out.println("异常信息：" + e.getMessage());
                System.out.println("异常原因：" + e.getCause());
            } finally {
                System.out.println("搜索下一级节点完成");
                if (tempList.size() > 0) {
                    topElementsList.clear();
                    topElementsList.addAll(tempList);
                    tempList.clear();

                    System.out.println("因为临时搜索集合中还有待搜索的内容，继续执行下级节点搜索");
                    getTarget4Next();
                } else {
                    System.out.println("临时元素集合不包括任何内容，不执行后续节点搜索");
                    System.out.println("所有节点已搜索完成");
                }
            }
        } else {
            System.out.println("顶级节点不存在匹配内容，不执行后续操作");
        }
    }

    private static Elements getDocElementsByTagName(String parent) throws IOException {
        String requestUrl = MAVEN_CENTRAL_REPOSITORY_URL;
        if (parent != null) {
            requestUrl += parent;
        }

        Document topDoc = Jsoup.connect(requestUrl).timeout(1000000000).get();
        return topDoc.getElementsByTag("a");
    }
}
