package cn.lanyj.keeper.models;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public abstract class BaseModel implements Comparable<BaseModel>, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7827394921964328120L;

	@Id
	@GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator="UUID")
    @Column(name = "id", nullable=false, unique=true, updatable=false)
    private String id;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private ZonedDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = ZonedDateTime.now();
    }

    @Override
    public int compareTo(BaseModel o) {
        return this.getId().compareTo(o.getId());
    }

    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        if(this.getId() == null && ((BaseModel) other).getId() == null) {
        	return true;
        }
        return this.getId().equals(((BaseModel) other).getId());
    }

    public int hashCode() {
    	if(getId() == null) {
    		return -1;
    	}
    	return getId().hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        id = _id;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
}