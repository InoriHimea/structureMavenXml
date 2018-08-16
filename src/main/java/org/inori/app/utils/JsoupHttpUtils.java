package org.inori.app.utils;

import org.jsoup.select.Elements;

/**
 * @author InoriHimea
 * @version 1.0
 * @date 2018/8/13 10:54
 * @since jdk1.8
 */
public class JsoupHttpUtils {

    public static boolean hasTargetName(Elements elements, String targetName) {
        boolean flag = false;

        if (elements.eachText().indexOf(targetName) != -1) {
            flag = true;
        }
        return flag;
    }
}
