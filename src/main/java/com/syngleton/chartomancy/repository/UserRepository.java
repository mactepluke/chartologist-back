package com.syngleton.chartomancy.repository;

import com.syngleton.chartomancy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    User findByEmailAndEnabled(String email, boolean b);
}