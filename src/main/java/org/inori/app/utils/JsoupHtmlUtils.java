package org.inori.app.utils;

import org.jsoup.select.Elements;

/**
 * 所有相关HTML操作的Jsoup方法
 * @author InoriHimea
 * @date 2018/8/8
 * @since 1.8
 * @version 1.0
 */
public class JsoupHtmlUtils {

    public static boolean hasTargetFile(Elements elements,String fileName) {
        boolean flag = false;

        if (elements.eachText().indexOf(fileName) != -1) {
            flag = true;
        }
        return flag;
    }
}
