package org.hibernate.bugs.cl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Alexander Schwartz
 */
@Entity
@IdClass(Child.Key.class)
public class Child {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID")
    private Parent parent;

    @Id
    @Column(name="_NAME")
    protected String name;

    @Nationalized
    @Column(name = "_VALUE")
    protected String value;

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Child )) return false;
        return parent != null && parent.equals(((Child) o).getParent());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static class Key implements Serializable, Comparable<Key> {

        protected Parent parent;

        protected String name;

        public Key() {
        }

        public Key(Parent parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public Parent getParent() {
            return parent;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (parent != null ? !parent.getId().equals(key.parent != null ? key.parent.getId() : null) : key.parent != null) return false;
            if (name != null ? !name.equals(key.name != null ? key.name : null) : key.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = parent != null ? parent.getId().hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }

        private final static Comparator<Key> COMPARATOR = Comparator.comparing(Key::getName)
                .thenComparing(c -> c.getParent() == null ? null : c.getParent().getId());

        @Override
        public int compareTo(Key other) {
            return COMPARATOR.compare(this, other);
        }
    }

}
