package com.denimgroup.threadfix.framework.impl.django.python.runtime;

import com.denimgroup.threadfix.framework.impl.django.python.schema.AbstractPythonStatement;

import javax.annotation.Nonnull;
import java.util.List;

import static com.denimgroup.threadfix.CollectionUtils.list;

public class PythonVariable implements PythonValue {

    String localName;
    PythonValue value;
    PythonValue owner;
    AbstractPythonStatement sourceLocation;

    public PythonVariable() {

    }

    public PythonVariable(String localName) {
        this.localName = localName;
    }

    public PythonVariable(String localName, PythonValue value) {
        this.localName = localName;
        this.value = value;
    }

    public PythonVariable(AbstractPythonStatement source) {
        this.localName = source.getName();
        this.sourceLocation = source;
    }

    public PythonVariable(String localName, AbstractPythonStatement source) {
        this.localName = localName;
        this.sourceLocation = source;
    }

    public boolean isType(@Nonnull Class<?> valueType) {
        return value != null && valueType.isAssignableFrom(value.getClass());
    }

    public PythonValue getValue() {
        return value;
    }

    public String getLocalName() {
        return localName;
    }

    public PythonValue getOwner() {
        return owner;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public void setValue(PythonValue value) {
        this.value = value;
    }

    public void setOwner(PythonValue owner) {
        this.owner = owner;
    }

    @Override
    public List<PythonValue> getSubValues() {
        if (value != null) {
            return list(value);
        } else {
            return list();
        }
    }

    @Override
    public void resolveSubValue(PythonValue previousValue, PythonValue newValue) {
        if (value == previousValue) {
            value = newValue;
        }
    }

    @Override
    public void resolveSourceLocation(AbstractPythonStatement source) {
        sourceLocation = source;
    }

    @Override
    public AbstractPythonStatement getSourceLocation() {
        return sourceLocation;
    }

    @Override
    public PythonValue clone() {
        PythonVariable clone = new PythonVariable(this.localName, this.value);
        clone.sourceLocation = this.sourceLocation;
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (localName != null) {
            result.append(localName);
            result.append("(=");
            result.append(this.value);
            result.append(')');
        } else {
            result.append(this.value);
        }

        return result.toString();
    }
}
