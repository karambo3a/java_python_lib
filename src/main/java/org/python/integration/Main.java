package org.python.integration;

public class Main {

    public static void main(String[] args) {
        try (PythonSession pythonSession = new PythonSession()) {
            var m = new Main();
            var list = PythonCore.evaluate("[1, 2, 3]");
            System.out.println(list.getIndex());    // list index
            System.out.println(list.representation());
            var getLen = list.getAttribute("__len__");
            System.out.println(getLen.getIndex());  // attribute index
            PythonBaseObject len = null;
            if (getLen.asCallable().isPresent()) {
                var a = getLen.asCallable().get();
                len = a.call();
            }
            System.out.println(len == null ? -1 : len.getIndex());     // attribute value

            int javaItem = 0;
            if (len.asInt().isPresent()) {
                var a = len.asInt().get();
                javaItem = a.toJavaInt();
            }
            System.out.println("javaItem " + javaItem);  //

            var index = PythonCore.evaluate("1");
            System.out.println(index.getIndex());
            var getitem = list.getAttribute("__getitem__");
            System.out.println(getitem.getIndex());
            PythonBaseObject item = null;
            if (getitem.asCallable().isPresent()) {
                item = getitem.asCallable().get().call(index);
            }
            System.out.println(item == null ? -1 : item.getIndex());

            int javaItem2 = 0;
            if (item.asInt().isPresent()) {
                var a = item.asInt().get();
                javaItem2 = a.toJavaInt();
            }
            System.out.println("javaItem2 " + javaItem2);

            PythonCore.free(list);
            list = PythonCore.evaluate("[1, 2, 3]");
            System.out.println(list.getIndex());
            var list2 = PythonCore.evaluate("[1, 2, 3]");
            System.out.println(list2.getIndex());
        }
    }
}
