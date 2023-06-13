package com.mogen.im.service.group.repostiory;

import com.mogen.im.common.enums.GroupMuteType;
import com.mogen.im.common.enums.GroupStatus;
import com.mogen.im.service.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group,Integer> {

    @Modifying
    int updateStatusById(GroupStatus status,Integer id);


    @Modifying
    int updateOwnerIdById(String userId,Integer id);


    @Modifying
    int updateMuteById(GroupMuteType muteType,Integer id);
}
