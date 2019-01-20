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

    public void register(Class clazz) { registered_classes.add(clazz); }
    public void unregister(Class clazz) { registered_classes.remove(clazz); }
    public void register(Collection<Class> classes) { registered_classes.addAll(classes); }

    public void run() {
        reset();
        for (Class clazz : registered_classes) {
            Object inst = null; try { inst = clazz.newInstance(); }
            catch (InstantiationException | IllegalAccessException  e) { e.printStackTrace(); continue; }

            _invokeHookMethod(inst, "initialize");
            boolean testAll = clazz.getDeclaredAnnotation(TestCase.class) != null;
            for (Method method : clazz.getDeclaredMethods())
                if ((testAll && method.getName().startsWith("test"))
                        || method.getDeclaredAnnotationsByType(TestCase.class).length != 0) {
                    test_count++;
                    method.setAccessible(true);
                    try {
                        _invokeHookMethod(inst, "setup");
                        method.invoke(inst);
                        _invokeHookMethod(inst, "teardown");
                    } catch (IllegalAccessException ignore) { }
                    catch (InvocationTargetException e) { e.printStackTrace(); error_count++; }
                    printSectionSpliter();
                }
            _invokeHookMethod(inst, "destroy");
        }
        report();
    }
    private void _invokeHookMethod(Object inst, String methodName) {
        try {
            Method mthd = inst.getClass().getDeclaredMethod(methodName);
            mthd.setAccessible(true); mthd.invoke(inst);
        }  catch (NoSuchMethodException | IllegalAccessException ignore) { }
        catch (InvocationTargetException e) { system_error_count++; e.printStackTrace(); }
    }
    private void reset() { test_count = error_count = system_error_count = 0; }
    private void report() {
        System.out.printf("Test of %d cases finished with %d passed and %d failed (%d system errors)\n",
                test_count, test_count - error_count, error_count, system_error_count);
    }

    private void printSectionSpliter() {
        System.out.println(String.format("%" + 5 + "d", 111111).replace("1", "==========="));
    }

    // hooks
    protected void setup() { }
    protected void teardown() { }
    protected void initialize() { }
    protected void destroy() { }

}