package com.mogen.im.service.group.repostiory;

import com.mogen.im.common.enums.GroupMuteType;
import com.mogen.im.common.enums.GroupStatus;
import com.mogen.im.service.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group,Integer> {

    @Modifying
    @Query("UPDATE Group SET status = :status,sequence = :seq WHERE id = :id")
    int updateStatusById(GroupStatus status,Integer id,long seq);


    @Modifying
    @Query("UPDATE Group SET ownerId = :userId,sequence = :seq WHERE id = :id")
    int updateOwnerIdById(String userId,Integer id,long seq);


    @Modifying
    @Query("UPDATE Group SET mute = :muteType,sequence = :seq  WHERE id = :id")
    int updateMuteById(GroupMuteType muteType,Integer id,long seq);


    @Query("SELECT * FROM GROUP WHERE id IN (:groupId) AND sequence < :maxSeq LIMIT :limit ORDER BY sequence")
    List<Group> findByMaxSeqGroupIdInLimit(List<Integer> groupId,Long maxSeq,Integer limit);

    @Query("SELECT MAX(sequence) FROM Group WHERE id IN (:ids)")
    Long findMaxSequenceByIdIn(List<Integer> ids);
}
