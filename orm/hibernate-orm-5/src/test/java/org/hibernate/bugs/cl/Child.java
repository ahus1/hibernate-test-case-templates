package org.hibernate.bugs.cl;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Alexander Schwartz
 */
@Entity
@Table(name = "child")
public class Child {

    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    public Parent getParent() {
        return parent;
    }

    private Parent parent;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Child )) return false;
        return id != null && id.equals(((Child) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
