#include "org_python_integration_core_PythonSession.h"
#include "globals.h"
#include "python_object_manager.h"
#include <Python.h>

JNIEXPORT void JNICALL Java_org_python_integration_core_PythonSession_initializePy(JNIEnv *, jobject) {
    Py_Initialize();
    Py_UNBLOCK_THREADS object_manager = new PythonObjectManager();
}

JNIEXPORT void JNICALL Java_org_python_integration_core_PythonSession_finalizePy(JNIEnv *, jobject) {
    Py_BLOCK_THREADS delete object_manager;
    object_manager = nullptr;
    Py_Finalize();
}
