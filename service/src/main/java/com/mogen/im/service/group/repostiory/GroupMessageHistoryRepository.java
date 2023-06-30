package com.mogen.im.service.group.repostiory;

import com.mogen.im.service.group.entity.GroupMessageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageHistoryRepository extends JpaRepository<GroupMessageHistory,Long> {
}
