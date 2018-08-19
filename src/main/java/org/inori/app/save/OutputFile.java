package org.inori.app.save;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * 通过多个不同的线程写出数据
 */
public class OutputFile extends Thread {

    private CountDownLatch singleDown;
    private String filePath;
    private Object textObj;

    public OutputFile (CountDownLatch singleDown, String filePath, Object textObj) {
        this.singleDown = singleDown;
        this.filePath = filePath;
        this.textObj = textObj;
    }

    @Override
    public void run() {
        long runStart = System.currentTimeMillis();
        System.out.println(this.toString() + "开始执行文件写入");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            if (textObj instanceof List) {
                for (String text : (List<String>) textObj) {
                    writer.write(text);
                    writer.newLine();
                }
            } else if (textObj instanceof Set) {
                for (String text : ((Set<String>) textObj)) {
                    writer.write(text);
                    writer.newLine();
                }
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            singleDown.countDown();

            long runStop = System.currentTimeMillis();
            System.out.println(this.toString() + "文件写入执行完毕，使用时间：" + (runStop - runStart) + "ms");
        }
    }
}
