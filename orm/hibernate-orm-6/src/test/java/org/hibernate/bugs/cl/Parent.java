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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.REMOVE;

/**
 * @author Alexander Schwartz
 */
@Entity(name = "parent")
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "parent", cascade = { REMOVE }, orphanRemoval = true)
    private List<Child> children = new LinkedList<>();

    @OneToMany(mappedBy = "parent", cascade = { REMOVE }, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LazyChild> lazyChildren = new LinkedList<>();

    public Set<String> getEventsListeners() {
        return eventsListeners;
    }

    public void setEventsListeners(Set<String> eventsListeners) {
        this.eventsListeners = eventsListeners;
    }

    @ElementCollection
    @Column(name="LISTENER")
    @CollectionTable(name="REALM_EVENTS_LISTENERS", joinColumns={ @JoinColumn(name="ID") })
    protected Set<String> eventsListeners = new HashSet<>();

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

    public int countChildren() {
        return children == null ? 0 : children.size();
    }

    public List<LazyChild> getLazyChildren() {
        return lazyChildren;
    }

    public void setLazyChildren(List<LazyChild> lazyChildren) {
        this.lazyChildren = lazyChildren;
    }
}
