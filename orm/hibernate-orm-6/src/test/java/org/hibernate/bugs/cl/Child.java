package org.hibernate.bugs.cl;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Alexander Schwartz
 */
@Entity
@Table(name = "child")
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Parent getParent() {
        return parent;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Parent parent;

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
