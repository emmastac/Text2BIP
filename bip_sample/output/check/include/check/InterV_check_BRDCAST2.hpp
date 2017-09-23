#ifndef INCLUDE_CHECK_INTERV__CHECK__BRDCAST2_HPP_
#define INCLUDE_CHECK_INTERV__CHECK__BRDCAST2_HPP_


#include <check/Inter_check_BRDCAST2.hpp>

class Port;
class PortValue;
class ConnT_check_BRDCAST2;

class InterV_check_BRDCAST2 : public InteractionValue {
public:
    virtual ~InterV_check_BRDCAST2();

    // getters for references (declared pure virtual in interface)
    virtual const Interaction &interaction() const { return mInteraction; }
    virtual const vector<PortValue *> &portValues() const;
    virtual bool hasPortValues() const;

    // specific operations
    const vector<Port *> &ports() const { return mInteraction.ports(); }

    // Only allow the connector to call the ctor here (see createInteractionValue() method)
    friend class ConnT_check_BRDCAST2;

protected:
    InterV_check_BRDCAST2(const ConnT_check_BRDCAST2 &connector, const Interaction& interaction, const vector<PortValue *> &values);
    Inter_check_BRDCAST2 mInteraction;
    vector<PortValue *> &portValues();
    vector<PortValue *> mPortValues;

    void commonRecycle(const Interaction& interaction, const vector<PortValue *> &values);

}; 

inline
const vector<PortValue *> &InterV_check_BRDCAST2::portValues() const {
    return this->mPortValues;
}

inline
bool InterV_check_BRDCAST2::hasPortValues() const {
    return (!this->mPortValues.empty());
}

inline
vector<PortValue *> &InterV_check_BRDCAST2::portValues() {
    return mPortValues;
}

#endif // INCLUDE_CHECK_INTERV__CHECK__BRDCAST2_HPP_
