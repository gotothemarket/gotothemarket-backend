// repository/MemberRepository.java
package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {}