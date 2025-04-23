#include "headers/org_python_integration_object_PythonList.h"
#include "headers/python_object_manager.h"
#include "headers/python_object_factory.h"
#include "headers/globals.h"
#include <Python.h>


JNIEXPORT jobject JNICALL Java_org_python_integration_object_PythonList_of(JNIEnv *env, jclass cls, jobject java_object) {
    PyObject *sequence = object_manager->get_object(env, java_object);
    if (!sequence) {
        return nullptr;
    }
    PyObject *list = PySequence_List(sequence);
    if (!list) {
        jthrowable java_exception = create_python_exception(env);
        env->Throw(java_exception);
        return nullptr;
    }
    std::size_t index = object_manager->add_object(list);
    jobject python_list = create_python_list(env, index, object_manager->get_object_manager_scope());
    return python_list;
}
