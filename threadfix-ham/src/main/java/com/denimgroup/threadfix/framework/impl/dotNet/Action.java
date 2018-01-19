////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
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
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.framework.impl.dotNet;

import com.denimgroup.threadfix.data.entities.ModelField;
import com.denimgroup.threadfix.data.enums.ParameterDataType;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

import static com.denimgroup.threadfix.CollectionUtils.map;

/**
 * Created by mac on 6/26/14.
 */
class Action {
    @Nonnull
    String      name;
    @Nonnull
    Set<String> attributes;
    @Nonnull
    Integer     lineNumber;
    @Nonnull
    Integer     endLineNumber;
    @Nonnull
    Map<String, ParameterDataType> parameters = map();
    @Nonnull
    Set<ModelField> parametersWithTypes;

    String getMethod() {
        if (attributes.contains("HttpGet")) {
            return "GET";
        } else if (attributes.contains("HttpPost")) {
            return "POST";
        } else if (attributes.contains("HttpPatch")) {
            return "PATCH";
        } else if (attributes.contains("HttpPut")) {
            return "PUT";
        } else if (attributes.contains("HttpDelete")) {
            return "DELETE";
        } else {
            return "GET";
        }
    }

    static Action action(@Nonnull String name,
                         @Nonnull Set<String> attributes,
                         @Nonnull Integer lineNumber,
                         @Nonnull Integer endLineNumber,
                         @Nonnull Set<ModelField> parametersWithTypes) {
        Action action = new Action();
        action.name = name;
        action.attributes = attributes;
        action.lineNumber = lineNumber;
        action.parametersWithTypes = parametersWithTypes;
        action.endLineNumber = endLineNumber;

        for (ModelField field : parametersWithTypes) {
            if (field.getType().equals("Include")) {
                for (String s : StringUtils.split(field.getParameterKey(), ',')) {
                    action.parameters.put(s.trim(), ParameterDataType.getType(field.getType()));
                }
            } else {
                action.parameters.put(field.getParameterKey(), ParameterDataType.getType(field.getType()));
            }
        }

        return action;
    }

    @Override
    public String toString() {
        return name + ": " + getMethod() + parameters;
    }

}

