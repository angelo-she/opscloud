package com.baiyi.opscloud.service.project.impl;

import com.baiyi.opscloud.domain.DataTable;
import com.baiyi.opscloud.domain.generator.opscloud.Project;
import com.baiyi.opscloud.domain.param.project.ProjectParam;
import com.baiyi.opscloud.mapper.ProjectMapper;
import com.baiyi.opscloud.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @Author 修远
 * @Date 2023/5/12 5:30 PM
 * @Since 1.0
 */

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;

    @Override
    public Project getById(Integer id) {
        return projectMapper.selectByPrimaryKey(id);
    }

    @Override
    public Project getByKey(String projectKey) {
        Example example = new Example(Project.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectKey", projectKey);
        return projectMapper.selectOneByExample(example);
    }

    @Override
    public Project getByName(String name) {
        Example example = new Example(Project.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", name);
        return projectMapper.selectOneByExample(example);
    }

    @Override
    public void add(Project project) {
        projectMapper.insert(project);
    }

    @Override
    public void update(Project project) {
        projectMapper.updateByPrimaryKey(project);
    }

    @Override
    public void deleteById(Integer id) {
        projectMapper.deleteByPrimaryKey(id);
    }

    @Override
    public DataTable<Project> queryPageByParam(ProjectParam.ProjectPageQuery pageQuery) {
        return null;
    }
}
