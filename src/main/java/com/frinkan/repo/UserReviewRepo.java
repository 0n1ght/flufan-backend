package com.frinkan.repo;

import com.frinkan.entity.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReviewRepo extends JpaRepository<UserReview, Long> {
    List<UserReview> findByProfileId(Long profileId);
}
