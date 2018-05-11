package org.hdu.back.mapper;

import org.apache.ibatis.annotations.Param;
import org.hdu.back.mapper.base.BaseMapper;
import org.hdu.back.model.ProxyEntity;
import java.util.Date;
import java.util.List;

public interface ProxyEntityMapper extends BaseMapper<ProxyEntity> {

    List<ProxyEntity> getAllEnables();

    List<ProxyEntity> getNewEnables(@Param(value="lastMaxCreateTime")Date lastMaxCreateTime);

    void batchInsert(@Param(value = "proxyEntityList") List<ProxyEntity> proxyEntityList);
}