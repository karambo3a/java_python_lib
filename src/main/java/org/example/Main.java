package org.example;

public class Main {

    static {
        System.loadLibrary("native");
    }

    private native PythonObj evaluate(String str);
    private native String representation(Object obj);

    public static void main(String[] args) {
        var m = new Main();

        var pythonObj = m.evaluate("string");
        var res = m.representation(pythonObj);
        System.out.println(res);
    }
}
