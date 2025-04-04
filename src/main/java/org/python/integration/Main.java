package org.python.integration;

import org.python.integration.core.PythonCore;
import org.python.integration.core.PythonSession;
import org.python.integration.exception.NativeOperationException;
import org.python.integration.exception.PythonException;
import org.python.integration.object.IPythonObject;

public class Main {

    public static void main(String[] args) {
        try (PythonSession pythonSession = new PythonSession()) {
            var list = PythonCore.evaluate("[1, 2, 3]");

            System.out.println(list.representation());
            var getLen = list.getAttribute("__len__");
            try{
                var b = list.getAttribute("some attribute");
            } catch (PythonException e) {
                System.out.println("caught!");
                System.out.println(e.getType() == null);
                System.out.println(e.getValue() == null);
                System.out.println(e.getTraceback() == null);
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
            var getitem = list.getAttribute("__getitem__");
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
        } catch (NativeOperationException | PythonException e) {

        }
    }
}
