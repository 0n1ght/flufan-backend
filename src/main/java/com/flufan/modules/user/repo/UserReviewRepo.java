package com.flufan.modules.user.repo;

import com.flufan.modules.user.entity.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserReviewRepo extends JpaRepository<UserReview, Long> {

    List<UserReview> findByProfileId(Long profileId);

    Optional<UserReview> findById(Long userReviewId);
}
