package org.example;

public class Main {

    static {
        System.loadLibrary("native");
    }

    private native PythonObject evaluate(String str);

    private native String representation(PythonObject obj);

    private native PythonObject getAttribute(PythonObject obj, String attrName);

    private native PythonObject callFunction(PythonObject func, PythonObject... arg);

    private native void free(PythonObject obj);

    private native int asInt(PythonObject obj);

    public static void main(String[] args) {
        try (PythonInitializer pythonInitializer = new PythonInitializer()) {
            var m = new Main();
            var list = m.evaluate("[1, 2, 3]");
            System.out.println(list.getIndex());

            var getLen = m.getAttribute(list, "__len__");
            System.out.println(getLen.getIndex());
            var len = m.callFunction(getLen);
            System.out.println(len.getIndex());

            var javaItem = m.asInt(len);
            System.out.println("javaItem " + javaItem);

            var index = m.evaluate("1");
            var getitem = m.getAttribute(list, "__getitem__");
            var item = m.callFunction(getitem, index);
            var javaItem2 = m.asInt(item);
            System.out.println("javaItem2 " + javaItem2);

            m.free(list);
            list = m.evaluate("[1, 2, 3]");
            System.out.println(list.getIndex());
            var list2 = m.evaluate("[1, 2, 3]");
            System.out.println(list2.getIndex());
        }
    }
}
