package com.mogen.im.service.message.repository;

import com.mogen.im.service.message.entity.MessageBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageBodyRepository extends JpaRepository<MessageBody,Long> {
}
