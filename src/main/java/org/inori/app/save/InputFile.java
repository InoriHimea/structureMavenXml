package org.inori.app.save;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class InputFile extends Thread {

    private CountDownLatch singleDown;
    private String filePath;
    private Object target;

    public InputFile (CountDownLatch singleDown, String filePath, Object target) {
        this.singleDown = singleDown;
        this.filePath = filePath;
        this.target = target;
    }

    @Override
    public void run() {
        long runStart = System.currentTimeMillis();
        System.out.println(this.toString() + "开始执行文件读取");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (target instanceof List) {
                    ((List) target).add(line);
                } else if (target instanceof Set) {
                    ((Set) target).add(line);
                }
            }
        } catch (IOException e) {
            System.out.println(e.fillInStackTrace());
        } finally {
            singleDown.countDown();

            long runStop = System.currentTimeMillis();
            System.out.println(this.toString() + "文件读取执行完毕，使用时间：" + (runStop - runStart) + "ms");
        }
    }
}
