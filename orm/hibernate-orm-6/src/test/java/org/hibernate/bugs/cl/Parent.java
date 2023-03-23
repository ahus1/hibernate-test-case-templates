package org.hibernate.bugs.cl;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.REMOVE;

/**
 * @author Alexander Schwartz
 */
@Entity(name = "parent")
@Table(name = "parent")
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    @OneToMany(mappedBy = "parent", cascade = { REMOVE }, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<Child> children = new ArrayList<>();

    @ElementCollection
    @Column(name="LISTENER")
    @CollectionTable(name="REALM_EVENTS_LISTENERS", joinColumns={ @JoinColumn(name="ID") })
    protected Set<String> eventsListeners;

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
