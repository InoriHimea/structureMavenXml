package org.inori.app.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author InoriHimea
 * @version 1.0
 * @date 2018/8/16 10:38
 * @since jdk1.8
 */
public class MultiOutputStream extends OutputStream {

    private static final int ALL_OUT = 0;
    private static final int ONE_OUT = 1;
    private static final int TWO_OUT = 2;

    private OutputStream outputStream1;
    private OutputStream outputStream2;
    private int type = ALL_OUT;

    public MultiOutputStream(OutputStream outputStream1, OutputStream outputStream2) {
        this.outputStream1 = outputStream1;
        this.outputStream2 = outputStream2;
    }

    @Override
    public void write(int b) throws IOException {
        switch (type) {
            case ONE_OUT:
                outputStream1.write(b);

                break;
            case TWO_OUT:
                outputStream2.write(b);

                break;
            default:
                outputStream1.write(b);
                outputStream2.write(b);
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
