#include "headers/org_python_integration_core_PythonSession.h"
#include "headers/globals.h"
#include "headers/python_object_manager.h"
#include <Python.h>

JNIEXPORT void JNICALL Java_org_python_integration_core_PythonSession_initializePy(JNIEnv *, jobject) {
    Py_Initialize();
    object_manager = new PythonObjectManager();
}

JNIEXPORT void JNICALL Java_org_python_integration_core_PythonSession_finalizePy(JNIEnv *, jobject) {
    delete object_manager;
    object_manager = nullptr;
    Py_Finalize();
}
