package com.baiyi.caesar.service.datasource.impl;

import com.baiyi.caesar.domain.generator.caesar.DatasourceInstanceAssetRelation;
import com.baiyi.caesar.mapper.caesar.DatasourceInstanceAssetRelationMapper;
import com.baiyi.caesar.service.datasource.DsInstanceAssetRelationService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author baiyi
 * @Date 2021/6/19 9:58 下午
 * @Version 1.0
 */
@Service
public class DsInstanceAssetRelationServiceImpl implements DsInstanceAssetRelationService {

    @Resource
    private DatasourceInstanceAssetRelationMapper dsInstanceAssetRelationMapper;

    @Override
    public void deleteById(Integer id) {
        dsInstanceAssetRelationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void add(DatasourceInstanceAssetRelation relation) {
        dsInstanceAssetRelationMapper.insert(relation);
    }

    @Override
    public void save(DatasourceInstanceAssetRelation relation) {
        Example example = new Example(DatasourceInstanceAssetRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("instanceUuid", relation.getInstanceUuid())
                .andEqualTo("relationType", relation.getRelationType())
                .andEqualTo("sourceAssetId", relation.getSourceAssetId())
                .andEqualTo("targetAssetId", relation.getTargetAssetId());
        if (dsInstanceAssetRelationMapper.selectOneByExample(example) == null)
            add(relation);
    }

    @Override
    public List<DatasourceInstanceAssetRelation> queryTargetAsset(String instanceUuid, Integer sourceAssetId) {
        Example example = new Example(DatasourceInstanceAssetRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("instanceUuid", instanceUuid)
                .andEqualTo("sourceAssetId", sourceAssetId);
        return dsInstanceAssetRelationMapper.selectByExample(example);
    }

    @Override
    public List<DatasourceInstanceAssetRelation> queryByAssetId(Integer assetId) {
        Example example = new Example(DatasourceInstanceAssetRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sourceAssetId", assetId)
                .orEqualTo("targetAssetId", assetId);
        return dsInstanceAssetRelationMapper.selectByExample(example);
    }
}
