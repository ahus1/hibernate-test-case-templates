package org.hibernate.bugs;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKey;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Table(name="REALM")
@Entity
public class RealmEntity {
    @Id
    @Column(name="ID", length = 36)
    @Access(AccessType.PROPERTY) // we do this because relationships often fetch id, but not entity.  This avoids an extra SQL
    protected String id;

    @Column(name="NAME", unique = true)
    protected String name;

    @OneToMany(cascade ={CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "realm", fetch = FetchType.EAGER)
    Collection<RealmAttributeEntity> attributes = new LinkedList<>();

    @ElementCollection
    @MapKeyColumn(name="NAME")
    @Column(name="VALUE")
    @CollectionTable(name="REALM_SMTP_CONFIG", joinColumns={ @JoinColumn(name="REALM_ID") })
    protected Map<String, String> smtpConfig;

    @ElementCollection
    @Column(name="GROUP_ID")
    @CollectionTable(name="REALM_DEFAULT_GROUPS", joinColumns={ @JoinColumn(name="REALM_ID") })
    protected Set<String> defaultGroupIds;

    @ElementCollection
    @Column(name="VALUE")
    @CollectionTable(name="REALM_EVENTS_LISTENERS", joinColumns={ @JoinColumn(name="REALM_ID") })
    protected Set<String> eventsListeners;
    
    @ElementCollection
    @Column(name="VALUE")
    @CollectionTable(name="REALM_ENABLED_EVENT_TYPES", joinColumns={ @JoinColumn(name="REALM_ID") })
    protected Set<String> enabledEventTypes;
    
    @OneToMany(fetch = FetchType.LAZY, cascade ={CascadeType.ALL}, orphanRemoval = true, mappedBy = "realm")
    Set<ComponentEntity> components = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getSmtpConfig() {
        if (smtpConfig == null) {
            smtpConfig = new HashMap<>();
        }
        return smtpConfig;
    }

    public void setSmtpConfig(Map<String, String> smtpConfig) {
        this.smtpConfig = smtpConfig;
    }

    public Set<String> getDefaultGroupIds() {
        if (defaultGroupIds == null) {
            defaultGroupIds = new HashSet<>();
        }
        return defaultGroupIds;
    }

    public void setDefaultGroupIds(Set<String> defaultGroups) {
        this.defaultGroupIds = defaultGroups;
    }

    public Set<String> getEventsListeners() {
        if (eventsListeners == null) {
            eventsListeners = new HashSet<>();
        }
        return eventsListeners;
    }

    public void setEventsListeners(Set<String> eventsListeners) {
        this.eventsListeners = eventsListeners;
    }
    
    public Set<String> getEnabledEventTypes() {
        if (enabledEventTypes == null) {
            enabledEventTypes = new HashSet<>();
        }
        return enabledEventTypes;
    }

    public void setEnabledEventTypes(Set<String> enabledEventTypes) {
        this.enabledEventTypes = enabledEventTypes;
    }
    
    public Collection<RealmAttributeEntity> getAttributes() {
        if (attributes == null) {
            attributes = new LinkedList<>();
        }
        return attributes;
    }

    public void setAttributes(Collection<RealmAttributeEntity> attributes) {
        this.attributes = attributes;
    }

    public Set<ComponentEntity> getComponents() {
        if (components == null) {
            components = new HashSet<>();
        }
        return components;
    }

    public void setComponents(Set<ComponentEntity> components) {
        this.components = components;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof RealmEntity)) return false;

        RealmEntity that = (RealmEntity) o;

        if (!id.equals(that.getId())) return false;

        return true;
    }

    public String toString() {
        return "Realm{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

