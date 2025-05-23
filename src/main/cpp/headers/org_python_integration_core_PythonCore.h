/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_python_integration_core_PythonCore */

#ifndef _Included_org_python_integration_core_PythonCore
#define _Included_org_python_integration_core_PythonCore
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_python_integration_core_PythonCore
 * Method:    evaluate
 * Signature: (Ljava/lang/String;)Lorg/python/integration/object/IPythonObject;
 */
JNIEXPORT jobject JNICALL Java_org_python_integration_core_PythonCore_evaluate
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_python_integration_core_PythonCore
 * Method:    free
 * Signature: (Lorg/python/integration/object/IPythonObject;)V
 */
JNIEXPORT void JNICALL Java_org_python_integration_core_PythonCore_free
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_python_integration_core_PythonCore
 * Method:    importModule
 * Signature: (Ljava/lang/String;)Lorg/python/integration/object/IPythonObject;
 */
JNIEXPORT jobject JNICALL Java_org_python_integration_core_PythonCore_importModule
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_python_integration_core_PythonCore
 * Method:    fromImport
 * Signature: (Ljava/lang/String;[Ljava/lang/String;)Ljava/util/Map;
 */
JNIEXPORT jobject JNICALL Java_org_python_integration_core_PythonCore_fromImport
  (JNIEnv *, jclass, jstring, jobjectArray);

#ifdef __cplusplus
}
#endif
#endif
