package org.hibernate.bugs.cl;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.util.Collection;
import java.util.LinkedList;

import static javax.persistence.CascadeType.REMOVE;

/**
 * @author Alexander Schwartz
 */
@Entity(name = "parent")
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "parent", cascade = { REMOVE }, fetch = FetchType.LAZY)
    private Collection<Child> children = new LinkedList<>();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addChild(Child child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Child child) {
        children.remove(child);
        child.setParent(null);
    }

}
