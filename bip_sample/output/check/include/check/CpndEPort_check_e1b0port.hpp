#ifndef INCLUDE_CHECK_CPNDEPORT__CHECK__E1B0PORT_HPP_
#define INCLUDE_CHECK_CPNDEPORT__CHECK__E1B0PORT_HPP_

#include <CompoundExportPort.hpp>

#include <check/PT_check_e1b0port.hpp>

class CpndEPort_check_e1b0port :
    public virtual Port,
    public CompoundExportPort,
    public PT_check_e1b0port {
public:
    CpndEPort_check_e1b0port(const string &name);
    virtual ~CpndEPort_check_e1b0port();
}; 

#endif // INCLUDE_CHECK_CPNDEPORT__CHECK__E1B0PORT_HPP_
