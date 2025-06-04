#include "gil.h"

GIL::GIL() {
    this->gil_state = PyGILState_Ensure();
}

GIL::~GIL() {
    PyGILState_Release(this->gil_state);
}
