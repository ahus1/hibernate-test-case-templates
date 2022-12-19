package org.hibernate.bugs.cl;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Schwartz
 */
@Entity()
public class MyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public void setRedirectUris(Set<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    @ElementCollection
    @Column(name = "val")
    @CollectionTable(name = "REDIRECT_URIS", joinColumns = {@JoinColumn(name = "ID")})
    protected Set<String> redirectUris = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<String> getRedirectUris() {
        return redirectUris;
    }

}
