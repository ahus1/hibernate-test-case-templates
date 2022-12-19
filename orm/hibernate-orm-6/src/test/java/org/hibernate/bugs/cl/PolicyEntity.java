package org.hibernate.bugs.cl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Schwartz
 */
@Entity(name = "policy")
public class PolicyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinTable(name = "ASSOCIATED_POLICY", joinColumns = @JoinColumn(name = "POLICY_ID"), inverseJoinColumns = @JoinColumn(name = "ASSOCIATED_POLICY_ID"))
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 20)
    private Set<PolicyEntity> associatedPolicies;

    @ManyToMany(mappedBy = "associatedPolicies")
    private Set<PolicyEntity> rootPolicies;

    public Set<PolicyEntity> getAssociatedPolicies() {
        if (associatedPolicies == null) {
            associatedPolicies = new HashSet<>();
        }
        return associatedPolicies;
    }

    public Set<PolicyEntity> getRootPolicies() {
        if (rootPolicies == null) {
            rootPolicies = new HashSet<>();
        }
        return rootPolicies;
    }

    public void addAssociatedPolicy(PolicyEntity policy) {
        getAssociatedPolicies().add(policy);
        policy.getRootPolicies().add(this);
    }

    public void removeAssociatedPolicy(PolicyEntity policy) {
        getAssociatedPolicies().remove(policy);
        policy.getRootPolicies().remove(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof PolicyEntity))
            return false;

        return
                id != null &&
                        id.equals(((PolicyEntity) o).id);
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Long getId() {
        return id;
    }
}
