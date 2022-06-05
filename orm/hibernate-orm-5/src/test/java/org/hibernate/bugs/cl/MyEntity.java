package org.hibernate.bugs.cl;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Alexander Schwartz
 */
@Entity
@Table(name = "MYTABLE")
@TypeDefs({@TypeDef(name = "mytype", typeClass = MyType.class)})
public class MyEntity {
    private Long id;

    @Type(type = "myType")
    private MyType myType;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public MyType getMyType() {
        return myType;
    }

    public void setMyType(MyType myType) {
        this.myType = myType;
    }
}
