package com.example.requipassignment.repository;

import com.example.requipassignment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPrimaryMobile(String primaryMobile);
    boolean existsByAadhaar(String aadhaar);
    boolean existsByPan(String pan);
}
