package org.inori.app.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.inori.app.utils.MultiOutputStream;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *  给每个输出内容之前加上时间
 */
public class LogInterceptor implements MethodInterceptor {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    private MultiOutputStream target;

    public MultiOutputStream getLogger(MultiOutputStream target, Class[] args, Object[] argValues) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);
        return (MultiOutputStream) enhancer.create(args, argValues);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String data = null;

        synchronized (sdf) {
            data = sdf.format(Calendar.getInstance().getTime());
        }
        byte[] a = (data + "  ").getBytes("UTF-8");
        if (args.length == 3 && ! args[2].equals(2)) {
            target.write(a);
        }

        proxy.invokeSuper(obj, args);
        return null;
    }
}
