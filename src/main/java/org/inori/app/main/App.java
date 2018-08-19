package org.inori.app.main;

import org.inori.app.aop.LogInterceptor;
import org.inori.app.save.InputFile;
import org.inori.app.save.OutputFile;
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
import java.util.concurrent.CountDownLatch;

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
    private static final String TARGET_MD5 = "maven-metadata.xml.md5";
    private static final int threadNum = 3;
    private static List<String> topElementsList = new LinkedList<String>();
    private static List<String> tempList = new LinkedList<String>();
    private static Set<String> targetSet = new LinkedHashSet<String>();

    static {

    }

    /**
     * 主方法
     * @param args
     */
    public static void main(String[] args) {
        long mainStart = System.currentTimeMillis();

        PrintStream ps = null;
        MultiOutputStream outputStream = null;

        try {
            ps = new PrintStream(new FileOutputStream("app.log"));
            outputStream = new LogInterceptor().getLogger(
                    new MultiOutputStream(ps, System.out),
                    new Class[]{OutputStream.class, OutputStream.class},
                    new Object[]{ps, System.out});

            //使用新的输出策略
            System.setOut(new PrintStream(outputStream));

            System.out.println("程序执行开始");

            //先读取文件
            inputFile2Collection();

            //搜索顶级元素
            getTopElementsList();

            //根据顶级节点元素递归搜索下一级元素节点，直到找到目标元素文本
            getTarget4Next();

            //把所有list存入文件
            outputCollection2File();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("出现异常");
            System.out.println(e.getLocalizedMessage());
            System.out.println("异常信息：" + e.getMessage());
            System.out.println("异常原因：" + e.getCause());

            //发生异常，将list中的内容写入文件中保存，以便下次继续执行
            outputCollection2File();
        } finally {
            long mainStop = System.currentTimeMillis();
            System.out.println("程序执行结束，使用时间：" + (mainStop - mainStart) + "ms");

            try {
                if (ps != null) {
                    ps.close();
                }
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把文件内容输入到集合中并分配接下来需要执行的方法
     */
    private static void inputFile2Collection() {
        long readStart = System.currentTimeMillis();
        System.out.println("开始多线程文件读取");

        CountDownLatch singleDown = new CountDownLatch(threadNum);
        new InputFile(singleDown, "parentList.txt", topElementsList).start();
        new InputFile(singleDown, "childrenList.txt", tempList).start();
        new InputFile(singleDown, "targetSet.txt", targetSet).start();

        try {
            singleDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();

            long countNo = singleDown.getCount();
            System.out.println("当前异常" + e.getLocalizedMessage());
            System.out.println("countNo:" + countNo);
        } finally {
            long readStop = System.currentTimeMillis();
            System.out.println("所有文件读取完成，所消耗时间：" + (readStop - readStart) + "ms");
        }
    }

    /**
     * 将存储与list中的内容，写入文件
     */
    private static void outputCollection2File() {
        long writeStart = System.currentTimeMillis();
        System.out.println("开始多线程文件输出");

        CountDownLatch singleDown = new CountDownLatch(threadNum);
        new OutputFile(singleDown, "parentList.txt", topElementsList).start();
        new OutputFile(singleDown, "childrenList.txt", tempList).start();
        new OutputFile(singleDown, "targetSet.txt", targetSet).start();

        try {
            singleDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();

            long countNo = singleDown.getCount();
            System.out.println("当前异常" + e.getLocalizedMessage());
            System.out.println("countNo:" + countNo);
        } finally {
            long writeStop = System.currentTimeMillis();
            System.out.println("所有文件写入完成，所消耗时间：" + (writeStop - writeStart) + "ms");
        }
    }

    /**
     * 搜索顶级集合
     */
    private static void getTopElementsList() throws IOException {
        long topStart = System.currentTimeMillis();
        System.out.println("开始执行顶级节点搜索");

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

        long topStop = System.currentTimeMillis();
        System.out.println("搜索顶级元素节点完成，用时" + (topStop - topStart) + "ms");
    }

    /**
     * 递归搜索
     */
    private static void getTarget4Next() throws IOException {
        long getNextAllTime = 0;
        long getNextStart = System.currentTimeMillis();
        System.out.println("开始执行次级节点搜索");
        System.out.println("当前父节点数量：" + topElementsList.size());
        System.out.println("当前临时节点数量：" + tempList.size());
        System.out.println("已匹配目标的数量：" + targetSet.size());

        if (topElementsList != null) {

            //进行下级搜索
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

            long getNextStop = System.currentTimeMillis();
            System.out.println("搜索下一级节点完成，消耗时间：" + (getNextStop - getNextStart) + "ms");
            getNextAllTime += getNextStop;
            if (tempList.size() > 0) {
                topElementsList.clear();
                topElementsList.addAll(tempList);
                tempList.clear();

                System.out.println("因为临时搜索集合中还有待搜索的内容，继续执行下级节点搜索");
                System.out.println("下一轮起始时间：" + getNextAllTime + "ms");
                getTarget4Next();
            } else {
                System.out.println("临时元素集合不包括任何内容，不执行后续节点搜索");
                System.out.println("所有节点已搜索完成，消耗时间："  + getNextAllTime + "ms");
            }
        } else {
            long getNextStop = System.currentTimeMillis();
            System.out.println("顶级节点不存在匹配内容，不执行后续操作，处理所用时间：" + (getNextStop - getNextStart) + "ms");
        }
    }

    /**
     * 请求远端资源
     * @param parent
     * @return
     * @throws IOException
     */
    private static Elements getDocElementsByTagName(String parent) throws IOException {
        long getStart = System.currentTimeMillis();
        System.out.println("开始执行远端支援Get请求");

        String requestUrl = MAVEN_CENTRAL_REPOSITORY_URL;
        if (parent != null) {
            requestUrl += parent;
        }

        Document topDoc = Jsoup.connect(requestUrl).timeout(1000000000).get();
        Elements elements = topDoc.getElementsByTag("a");

        long getStop = System.currentTimeMillis();
        System.out.println("执行远端请求结束，获得a标签元素：" + elements.size() + "个");
        return elements;
    }
}
