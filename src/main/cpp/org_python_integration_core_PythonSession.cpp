#include "headers/org_python_integration_core_PythonSession.h"
#include "headers/python_object_manager.h"
#include "headers/globals.h"
#include <Python.h>
#include <iostream>


JNIEXPORT void JNICALL Java_org_python_integration_core_PythonSession_initializePy(JNIEnv *env, jobject obj) {
  Py_Initialize();
  object_manager = new PythonObjectManager();
}


JNIEXPORT void JNICALL Java_org_python_integration_core_PythonSession_finalizePy(JNIEnv *env, jobject obj) {
  delete object_manager;
  object_manager = nullptr;
  Py_Finalize();
}
