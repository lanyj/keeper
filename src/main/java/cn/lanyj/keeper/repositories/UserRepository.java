package cn.lanyj.keeper.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.lanyj.keeper.models.User;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
	
	public User findUserById(String id);
	
	public User findUserByEmail(String email);
	
}
