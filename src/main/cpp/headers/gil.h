#ifndef GIL_H
#define GIL_H

#include <Python.h>

class GIL {
public:
    GIL();

    ~GIL();

private:
    PyGILState_STATE gil_state;
};

#endif  // GIL_H
