package org.hibernate.bugs.cl;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;
import org.hibernate.usertype.DynamicParameterizedType;

import java.util.Objects;
import java.util.Properties;

/**
 * @author Alexander Schwartz
 */
public class MyTypeJavaDescriptor extends AbstractTypeDescriptor<Object> implements DynamicParameterizedType {

    protected MyTypeJavaDescriptor() {
        super(Object.class, new MutableMutabilityPlan<Object>() {
            @Override
            protected Object deepCopyNotNull(Object value) {
                MyType myType = new MyType();
                myType.setMutableState(((MyType) value).getMutableState());
                return myType;
            }
        });
    }

    @Override
    public Object fromString(String string) {
        MyType myType = new MyType();
        myType.setMutableState(string);
        return myType;
    }

    @Override
    public <X> X unwrap(Object value, Class<X> type, WrapperOptions options) {
        return (X) ((MyType) value).getMutableState();
    }

    @Override
    public <X> Object wrap(X value, WrapperOptions options) {
        MyType myType = new MyType();
        myType.setMutableState((String) value);
        return myType;
    }

    @Override
    public boolean areEqual(Object one, Object another) {
        return Objects.equals(one, another);
    }

    @Override
    public void setParameterValues(Properties parameters) {
    }
}
