#ifndef GIL_H
#define GIL_H

#include <Python.h>

class GIL {
public:
    GIL();

    ~GIL();

    GIL(const GIL &) = delete;

    GIL(GIL &&) = delete;

    GIL &operator=(const GIL &) = delete;

    GIL &operator=(GIL &&) = delete;

private:
    PyGILState_STATE gil_state;
};

#endif  // GIL_H
