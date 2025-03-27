#include "headers/org_example_PythonInitializer.h"
#include "headers/python_object_manager.h"
#include "headers/globals.h"
#include <Python.h>


JNIEXPORT void JNICALL Java_org_example_PythonInitializer_initializePy(JNIEnv *env, jobject obj) {
  Py_Initialize();
  object_manager = new PythonObjectManager;
}

JNIEXPORT void JNICALL Java_org_example_PythonInitializer_finalizePy(JNIEnv *env, jobject obj) {
  delete object_manager;
  object_manager = nullptr;
  Py_Finalize();
}
