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

package com.denimgroup.threadfix.framework.impl.django.python.runtime;

import com.denimgroup.threadfix.framework.impl.django.python.schema.AbstractPythonStatement;
import com.denimgroup.threadfix.framework.impl.django.python.schema.PythonClass;

import java.util.List;
import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.map;

public class PythonObject implements PythonValue {

    String memberPath;
    PythonClass classType;
    AbstractPythonStatement sourceLocation;

    Map<String, PythonVariable> memberMap = map();

    public PythonObject() {

    }

    public PythonObject(String memberPath) {
        this.memberPath = memberPath;
    }

    public PythonObject(PythonClass classType) {
        this.classType = classType;
    }

    public PythonObject(PythonClass classType, String memberPath) {
        this.classType = classType;
        this.memberPath = memberPath;
    }

    public PythonClass getClassType() {
        return classType;
    }

    public String getMemberPath() {
        return memberPath;
    }

    public void setClassType(PythonClass classType) {
        this.classType = classType;
    }

    public void setMemberPath(String memberPath) {
        this.memberPath = memberPath;
    }

    @Override
    public void resolveSubValue(PythonValue previousValue, PythonValue newValue) {

    }

    @Override
    public void resolveSourceLocation(AbstractPythonStatement source) {
        sourceLocation = source;
    }

    @Override
    public AbstractPythonStatement getSourceLocation() {
        if (sourceLocation != null) {
            return sourceLocation;
        } else {
            return classType;
        }
    }

    public boolean hasMemberValue(String name) {
        return memberMap.containsKey(name);
    }

    /**
     * Assigns the member variable to the given value, which
     * is resolved to its sub-values if the value is a PythonVariable.
     * @param name
     * @param value
     */
    public void setMemberValue(String name, PythonValue value) {
        setMemberValue(name, value, null);
    }

    public void setMemberValue(String name, PythonValue value, AbstractPythonStatement source) {
        PythonVariable targetVar;
        if (memberMap.containsKey(name)) {
            targetVar = memberMap.get(name);
        } else {
            if (value instanceof PythonVariable) {
                targetVar = (PythonVariable)value.clone();
                targetVar.setOwner(this);
                targetVar.setLocalName(name);
                memberMap.put(name, targetVar);
            } else {
                targetVar = new PythonVariable(name, value);
                targetVar.setOwner(this);
                memberMap.put(name, targetVar);
            }
        }

        targetVar.resolveSourceLocation(source);
        targetVar.setValue(value);
    }


    /**
     * Assigns the member variable to the given value as-is.
     * @param name
     * @param value
     */
    public void setRawMemberValue(String name, PythonValue value) {
        setRawMemberValue(name, value, null);
    }

    public void setRawMemberValue(String name, PythonValue value, AbstractPythonStatement source) {
        PythonVariable targetVar;
        if (memberMap.containsKey(name)) {
            targetVar = memberMap.get(name);
        } else {
            targetVar = new PythonVariable(name);
            targetVar.setOwner(this);
            memberMap.put(name, targetVar);
        }
        targetVar.setRawValue(value);
        targetVar.resolveSourceLocation(source);
    }

    public void setMemberValue(String name, AbstractPythonStatement source) {
        PythonVariable targetVar;
        if (memberMap.containsKey(name)) {
            targetVar = memberMap.get(name);
        } else {
            targetVar = new PythonVariable(name);
            targetVar.setOwner(this);
            memberMap.put(name, targetVar);
        }

        targetVar.resolveSourceLocation(source);
    }

    public PythonVariable getMemberVariable(String name) {
        if (memberMap.containsKey(name)) {
            return memberMap.get(name);
        } else {
            return null;
        }
    }

    public PythonValue getMemberValue(String name) {
        PythonVariable var = getMemberVariable(name);
        if (var != null) {
            return var.getValue();
        } else {
            return null;
        }
    }

    public <T extends PythonValue> T getMemberValue(String name, Class<?> type) {
        PythonVariable var = getMemberVariable(name);
        PythonValue varValue = null;
        if (var != null) {
            varValue = var.getValue();
        }

        if (varValue != null && type.isAssignableFrom(varValue.getClass())) {
            return (T)varValue;
        } else {
            return null;
        }
    }

    @Override
    public List<PythonValue> getSubValues() {
        return null;
    }

    @Override
    public PythonValue clone() {
        PythonObject clone = new PythonObject();
        clone.memberPath = this.memberPath;
        clone.classType = this.classType;
        clone.sourceLocation = this.sourceLocation;
        for (Map.Entry<String, PythonVariable> entry : this.memberMap.entrySet()) {
            clone.setMemberValue(entry.getKey(), entry.getValue().clone());
        }
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (memberPath != null || classType != null) {
            result.append(memberPath);
            if (classType != null) {
                result.append(" (");
                result.append(classType.getName());
                result.append(')');
            }
        } else {
            result.append("<Unresolved Object>");
        }

        return result.toString();
    }
}
