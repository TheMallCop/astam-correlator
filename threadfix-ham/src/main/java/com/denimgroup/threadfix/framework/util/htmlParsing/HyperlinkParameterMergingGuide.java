////////////////////////////////////////////////////////////////////////
//
//     Copyright (C) 2017 Applied Visions - http://securedecisions.com
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     This material is based on research sponsored by the Department of Homeland
//     Security (DHS) Science and Technology Directorate, Cyber Security Division
//     (DHS S&T/CSD) via contract number HHSP233201600058C.
//
//     Contributor(s):
//              Secure Decisions, a division of Applied Visions, Inc
//
////////////////////////////////////////////////////////////////////////

package com.denimgroup.threadfix.framework.util.htmlParsing;

import com.denimgroup.threadfix.data.entities.RouteParameter;
import com.denimgroup.threadfix.data.interfaces.Endpoint;
import com.denimgroup.threadfix.framework.util.PathInvariantStringMap;

import java.util.List;
import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.CollectionUtils.map;

//  We don't have access to modify the data in an Endpoint; instead, provide
//  a guide as to which parameters to add
public class HyperlinkParameterMergingGuide {

    PathInvariantStringMap<Map<String, List<RouteParameter>>> addedParameters = new PathInvariantStringMap<Map<String, List<RouteParameter>>>();
    PathInvariantStringMap<Map<String, List<RouteParameter>>> removedParameters = new PathInvariantStringMap<Map<String, List<RouteParameter>>>();
    PathInvariantStringMap<Map<String, List<RouteParameter>>> discoveredHttpMethods = new PathInvariantStringMap<Map<String, List<RouteParameter>>>();
    List<Endpoint> unreferencedEndpoints = list();

    public List<RouteParameter> findAddedParameters(Endpoint endpoint, String method) {
        Map<String, List<RouteParameter>> addedEndpointParamsMap = addedParameters.get(endpoint.getUrlPath());
        if (addedEndpointParamsMap == null) {
            return null;
        }

        return addedEndpointParamsMap.get(method);
    }

    public List<RouteParameter> findRemovedParameters(Endpoint endpoint, String method) {
        Map<String, List<RouteParameter>> removedEndpointParamsMap = removedParameters.get(endpoint.getUrlPath());
        if (removedEndpointParamsMap == null) {
            return null;
        }

        return removedEndpointParamsMap.get(method);
    }

    public Map<String, List<RouteParameter>> findDiscoveredHttpMethods(Endpoint forEndpoint) {
        return discoveredHttpMethods.get(forEndpoint.getUrlPath());
    }

    public List<Endpoint> getUnreferencedEndpoints(Endpoint forEndpoint) {
        return unreferencedEndpoints;
    }

    public boolean hasData() {
        return  addedParameters.size() > 0 ||
                removedParameters.size() > 0 ||
                discoveredHttpMethods.size() > 0 ||
                unreferencedEndpoints.size() > 0;
    }

}

