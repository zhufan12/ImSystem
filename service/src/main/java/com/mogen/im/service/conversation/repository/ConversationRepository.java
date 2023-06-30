package com.mogen.im.service.conversation.repository;

import com.mogen.im.service.conversation.entity.Conversation;
import com.mogen.im.service.conversation.entity.ConversationId;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, ConversationId> {

    @Modifying
    @Query("UPDATE Conversation SET sequence = :messageSequence, readedSequence = :messageSequence, sequence = :seq" +
            "  WHERE appId = :#{#conversationId.appId} AND fromId = :#{#conversationId.fromId} " +
            "  AND toId = :#{#conversationId.toId} and type = :#{#conversationId.type} AND readedSequence < :messageSequence" +
            "")
    int readMark(@Param("conversationId") ConversationId conversationId, Long messageSequence,Long seq);


    @Query("SELECT * FROM Conversation WHERE fromId = :fromId AND appId = :appId AND sequence < :maxSeq LIMIT :limit ORDER BY sequence")
    List<Conversation> findByMaxSeqAndLimit(String fromId,Integer appId,Long maxSeq,Integer limit);

    @Query("SELECT MAX(sequence) FROM Conversation WHERE fromId = :fromId AND appId = :appId")
    Long findMaxSeqByFromIdAndAppId(String fromId,Integer appId);

}
