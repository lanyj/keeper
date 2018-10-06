package cn.lanyj.keeper.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "userCache")
public class User extends BaseModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7667733995376083727L;
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;
    
    private String role = ROLE_USER;
    
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="owner")
    private List<Password> passwords;
    
    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public void addPassword(Password password) {
		this.passwords.add(password);
	}
	
	public List<Password> getPasswords() {
		return passwords;
	}
    
}
