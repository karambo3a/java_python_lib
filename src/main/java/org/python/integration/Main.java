package org.python.integration;

public class Main {

    public static void main(String[] args) {
        try (PythonSession pythonSession = new PythonSession()) {
            var list = PythonCore.evaluate("[1, 2, 3]");
            System.out.println(list.representation());
            IPythonObject getLen = null;
            try{
                getLen = list.getAttribute("__len__");
            } catch (Exception e) {
                
            }
            try{
                var b = list.getAttribute("kalmsclaks");
            } catch (Exception e) {
                System.out.println("caught!");
            }
            IPythonObject len = null;
            if (getLen.asCallable().isPresent()) {
                var a = getLen.asCallable().get();
                len = a.call();
            }

            int javaItem = 0;
            if (len.asInt().isPresent()) {
                var a = len.asInt().get();
                javaItem = a.toJavaInt();
            }
            System.out.println("javaItem " + javaItem);  //

            var index = PythonCore.evaluate("1");
            IPythonObject getitem = null;
            try {
                getitem = list.getAttribute("__getitem__");
            } catch (Exception e) {

            }
            IPythonObject item = null;
            if (getitem.asCallable().isPresent()) {
                item = getitem.asCallable().get().call(index);
            }

            int javaItem2 = 0;
            if (item.asInt().isPresent()) {
                var a = item.asInt().get();
                javaItem2 = a.toJavaInt();
            }
            System.out.println("javaItem2 " + javaItem2);

            PythonCore.free(list);
            list = PythonCore.evaluate("[1, 2, 3]");
            var list2 = PythonCore.evaluate("[1, 2, 3]");
        }
    }
}
