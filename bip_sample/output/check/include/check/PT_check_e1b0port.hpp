#ifndef CHECK_PT__CHECK__E1B0PORT_HPP_
#define CHECK_PT__CHECK__E1B0PORT_HPP_

// /home/dsg003/Dropbox/eclipse_workspace/Text2BIP/bip_sample/check.bip:24:0
// include package "master" header
#include <check.hpp>

#include <Port.hpp>

// User include given in @cpp annotation
#include <Standard.hpp>
#include <stdio.h>

class PT_check_e1b0port : public virtual Port{

public:
    PT_check_e1b0port(const string &name, const ExportType& type);
    ~PT_check_e1b0port();
};
#endif // CHECK_PT__CHECK__E1B0PORT_HPP_
