package org.hzero.boot.oauth.domain.repository;

import java.util.List;

import org.hzero.boot.oauth.domain.entity.BasePasswordHistory;
import org.hzero.mybatis.base.BaseRepository;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
public interface BasePasswordHistoryRepository extends BaseRepository<BasePasswordHistory> {

    List<String> selectUserHistoryPassword(Long userId);

}
