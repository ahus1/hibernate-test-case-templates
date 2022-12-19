package org.hibernate.bugs.cl;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;

/**
 * @author Alexander Schwartz
 */
@Entity(name = "parent")
@Table(name = "parent")
public class Parent {
    private Long id;

    @OneToMany(mappedBy = "parent", cascade = { PERSIST }, orphanRemoval = true, fetch = FetchType.EAGER)
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
