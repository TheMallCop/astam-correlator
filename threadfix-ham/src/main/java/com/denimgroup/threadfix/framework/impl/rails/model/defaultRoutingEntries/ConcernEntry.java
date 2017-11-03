package com.denimgroup.threadfix.framework.impl.rails.model.defaultRoutingEntries;

import com.denimgroup.threadfix.framework.impl.rails.model.AbstractRailsRoutingEntry;
import com.denimgroup.threadfix.framework.impl.rails.model.PathHttpMethod;
import com.denimgroup.threadfix.framework.impl.rails.model.RailsRoutingEntry;
import com.denimgroup.threadfix.framework.impl.rails.model.RouteParameterValueType;

import java.util.Collection;

// http://guides.rubyonrails.org/routing.html#routing-concerns

//  Concerns allow you to declare common routes that can be reused inside other resources
//  and routes. Concerns are attached with hash syntax: 'concerns: [:concern1, :concern2, ..]'
public class ConcernEntry extends AbstractRailsRoutingEntry {

    String idSymbol = null;

    @Override
    public Collection<PathHttpMethod> getSubPaths() {
        return null;
    }

    @Override
    public String getControllerName() {
        return null;
    }

    @Override
    public String getActionMethodName() {
        return null;
    }

    @Override
    public void setControllerName(String controllerName) {

    }

    @Override
    public void setActionMethodName(String actionMethodName) {

    }

    @Override
    public RailsRoutingEntry cloneEntry() {
        ConcernEntry clone = new ConcernEntry();
        clone.idSymbol = idSymbol;
        cloneChildrenInto(clone);
        return clone;
    }

    public String getConcernIdSymbol() {
        return idSymbol;
    }

    @Override
    public void onParameter(String name, String value, RouteParameterValueType parameterType) {
        super.onParameter(name, value, parameterType);
        if (name == null) {
            idSymbol = value;
        }
    }

    @Override
    public String getPrimaryPath() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("concern :");
        if (idSymbol != null) {
            result.append(idSymbol);
        } else {
            result.append("<unknown symbol>");
        }

        return result.toString();
    }
}
