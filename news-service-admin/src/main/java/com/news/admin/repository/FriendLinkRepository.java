package com.news.admin.repository;

import com.news.model.user.mo.FriendLinkMO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 保存友情链接持久化到MongoDB
 * @create 2022-06-22-9:24
 */
@Repository
public interface FriendLinkRepository extends MongoRepository<FriendLinkMO, String> {

    /**
     * 自定义Dao方法
     * 同Spring Data JPA一样Spring Data mongodb也提供自定义方法的规则，
     * 如下： 按 findByXXX，ﬁndByXXXAndYYY、countByXXXAndYYY 等规则定义方法，实现查询操作。
     */

    //用户查看友情链接，只显示状态为'保留'（is_delete：0）的链接信息
    public List<FriendLinkMO> getAllByIsDelete(Integer isDelete);

}
