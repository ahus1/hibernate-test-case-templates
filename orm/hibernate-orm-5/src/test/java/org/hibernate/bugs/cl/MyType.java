package org.hibernate.bugs.cl;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.DynamicParameterizedType;

import java.util.Objects;
import java.util.Properties;

/**
 * @author Alexander Schwartz
 */
public class MyType extends AbstractSingleColumnStandardBasicType<Object> implements DynamicParameterizedType  {
    private String mutableState;

    public String getMutableState() {
        return mutableState;
    }

    public MyType() {
        super(MyTypeSqlDescriptor.INSTANCE, new MyTypeJavaDescriptor());
    }

    public void setMutableState(String mutableState) {
        this.mutableState = mutableState;
    }

    @Override
    public String getName() {
        return "mytype";
    }

    @Override
    public void setParameterValues(Properties properties) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyType myType = (MyType) o;
        return mutableState.equals(myType.mutableState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mutableState);
    }

}
