package com.mogen.im.service.message.repository;

import com.mogen.im.service.message.entity.MessageHistory;
import com.mogen.im.service.message.entity.id.MessageHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageHistoryRepository extends JpaRepository<MessageHistory,MessageHistoryId> {
}
