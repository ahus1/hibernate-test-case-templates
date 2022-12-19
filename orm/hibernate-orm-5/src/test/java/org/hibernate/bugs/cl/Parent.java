package org.hibernate.bugs.cl;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;

/**
 * @author Alexander Schwartz
 */
@Entity
public class Parent {
    private Long id;

    @OneToMany(mappedBy = "parent", cascade = {PERSIST}, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    private List<Child> children = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public int countChildren() {
        return children == null ? 0 : children.size();
    }
}
