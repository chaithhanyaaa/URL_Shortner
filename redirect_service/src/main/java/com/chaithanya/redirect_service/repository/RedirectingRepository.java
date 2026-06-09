package com.chaithanya.redirect_service.repository;

import com.chaithanya.redirect_service.entity.RedirectStats;
import com.chaithanya.redirect_service.entity.RedirectStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedirectingRepository extends JpaRepository<RedirectStats,String>
{
    Optional<RedirectStats> findByShortCode(String shortCode);


}




