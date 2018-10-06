package cn.lanyj.keeper.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.lanyj.keeper.models.Email;

public interface EmailRepository extends JpaRepository<Email, String> {
	
	@Query("SELECT s FROM Email s WHERE TIMESTAMPDIFF(DAY, s.createdAt, NOW()) >= 7")
	public Page<Email> getNeededToCleanEmails(Pageable pageable);
	
	public List<Email> getEmailBySentIsFalse(Pageable pageable);
	
}
