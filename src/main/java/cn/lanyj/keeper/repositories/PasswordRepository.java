package cn.lanyj.keeper.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.lanyj.keeper.models.Password;
import cn.lanyj.keeper.models.User;

public interface PasswordRepository extends JpaRepository<Password, String>, JpaSpecificationExecutor<Password> {
	
	public Password findPasswordById(String id);
	
	public List<Password> findPasswordByOwner(User owner, Pageable pageable);
	
	public List<Password> findPasswordByOwnerAndUrlLike(User owner, String url, Pageable pageable);
	
}
