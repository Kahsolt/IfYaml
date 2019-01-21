/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-19
 *  * Update Date : 2019-1-19
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 轻量化自动测试启动类...
 */

package util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Test {

    private Set<Class> registered_classes = new HashSet<>();
    private int test_count = 0;
    private int error_count = 0;
    private int system_error_count = 0;
    private long start_time = 0;

    public void register(Class clazz) { registered_classes.add(clazz); }
    public void register(Collection<Class> classes) { registered_classes.addAll(classes); }
    public void unregister(Class clazz) { registered_classes.remove(clazz); }

    public void run() {
        reset();
        for (Class clazz : registered_classes) {
            Object inst = null; try { inst = clazz.newInstance(); }
            catch (InstantiationException | IllegalAccessException  e) { e.printStackTrace(); continue; }

            _invokeHook(inst, "initialize");
            boolean testAll = clazz.getDeclaredAnnotation(TestCase.class) != null;
            for (Method method : clazz.getDeclaredMethods())
                if ((testAll && method.getName().startsWith("test"))
                        || method.getDeclaredAnnotationsByType(TestCase.class).length != 0) {
                    long S, T, s, t;
                    try {
                        S = System.currentTimeMillis();
                        _invokeHook(inst, "setup");
                        method.setAccessible(true);
                        s = System.currentTimeMillis();
                        method.invoke(inst);
                        t = System.currentTimeMillis();
                        _invokeHook(inst, "teardown");
                        T = System.currentTimeMillis();
                        test_count++;
                        System.out.printf("\n>> Test of %s.%s() in %d/%d mills.\n",
                                clazz.getSimpleName(), method.getName(), t - s, T - S);
                    } catch (IllegalAccessException | SecurityException ignore) { }
                    catch (InvocationTargetException e) { e.printStackTrace(); error_count++; }
                    System.out.println(StringEx.repeat("=", 78));
                }
            _invokeHook(inst, "destroy");
        }
        report();
    }
    private void _invokeHook(Object inst, String methodName) {
        if (inst == null) return;
        try {
            Method mthd = inst.getClass().getDeclaredMethod(methodName);
            mthd.setAccessible(true); mthd.invoke(inst);
        }  catch (NoSuchMethodException | IllegalAccessException ignore) { }
        catch (InvocationTargetException e) { system_error_count++; e.printStackTrace(); }
    }
    private void reset() {
        test_count = error_count = system_error_count = 0;
        start_time = System.currentTimeMillis();
    }
    private void report() {
        System.out.printf("\n>> Test of %d cases in %d mills.\n" +
                        "\t- %d/%d passed \n" +
                        "\t- %d/%d failed (%d system errors) \n",
                test_count, System.currentTimeMillis() - start_time,
                test_count - error_count, test_count,
                error_count, test_count, system_error_count);
    }

    // hooks
<<<<<<< HEAD
    protected void initialize() { /* invoked entering each class */ }
    protected void setup()      { /* invoked before each method */ }
    protected void teardown()   { /* invoked after each method */ }
    protected void destroy()    { /* invoked exiting each class */ }
=======
    protected void initialize() { /* invoked before each class */ }
    protected void setup()      { /* invoked before each method */ }
    protected void teardown()   { /* invoked after each method */ }
    protected void destroy()    { /* invoked after each class */ }
>>>>>>> 4760708... gameover

}