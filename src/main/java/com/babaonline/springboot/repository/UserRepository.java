package com.babaonline.springboot.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.babaonline.springboot.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

	@Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.email = ?2")
	@Modifying
	public void updateFailedAttempts(int failAttempts, String email);

}
