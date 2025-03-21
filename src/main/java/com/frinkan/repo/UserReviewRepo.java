package com.frinkan.repo;

import com.frinkan.entity.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserReviewRepo extends JpaRepository<UserReview, Long> {
    List<UserReview> findByProfileId(Long profileId); // Pobiera recenzje dla danego profilu
}
