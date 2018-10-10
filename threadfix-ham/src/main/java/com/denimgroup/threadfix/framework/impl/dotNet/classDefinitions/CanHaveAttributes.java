////////////////////////////////////////////////////////////////////////
//
//     Copyright (C) 2018 Applied Visions - http://securedecisions.com
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

package com.denimgroup.threadfix.framework.impl.dotNet.classDefinitions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.CollectionUtils.map;

public class CanHaveAttributes {
    private Map<String, List<CSharpAttribute>> attributes = map();

    public void addAttribute(CSharpAttribute attribute) {
        String attributeName = attribute.getName();
        List<CSharpAttribute> storedAttributes = attributes.get(attributeName);

        if (storedAttributes == null) {
            storedAttributes = list();
            attributes.put(attribute.getName(), storedAttributes);
        }

        storedAttributes.add(attribute);
    }

    public CSharpAttribute getAttribute(String name) {
        List<CSharpAttribute> attributesWithName = attributes.get(name);
        if (attributesWithName == null) {
            return null;
        }

        return attributesWithName.get(0);
    }

    public List<CSharpAttribute> getAttributes() {
        List<CSharpAttribute> allAttributes = list();
        for (List<CSharpAttribute> attributeList : attributes.values()) {
            allAttributes.addAll(attributeList);
        }
        return allAttributes;
    }

    public List<CSharpAttribute> getAttributes(String... names) {
        List<CSharpAttribute> discoveredAttributes = list();
        for (String name : names) {
            List<CSharpAttribute> attributesWithName = attributes.get(name);
            if (attributesWithName == null) {
                continue;
            }

            discoveredAttributes.addAll(attributesWithName);
        }
        return discoveredAttributes;
    }
}
