package com.mogen.im.service.user.repository;

import com.mogen.im.service.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    List<User> findByUserPidInAndAppId(List<String> userPids, Integer appId);

    Optional<User> findByUserPidAndAppId(String userPid,Integer appId);

    int deleteByUserPidAndAppId(String userPid,Integer appId);
}
