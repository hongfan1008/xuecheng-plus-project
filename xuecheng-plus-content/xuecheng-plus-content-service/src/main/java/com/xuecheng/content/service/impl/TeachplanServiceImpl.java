package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }
    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {

        Long id = teachplanDto.getId();
        if (id == null){
            //新增
            Teachplan teachplan = new Teachplan();
            int teachplanCount = getTeacherplanMaxOrderBy(teachplanDto.getCourseId(), teachplanDto.getParentid());
            teachplan.setOrderby(teachplanCount+1);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplan.setCreateDate(LocalDateTime.now());
            int insert = teachplanMapper.insert(teachplan);
            if (insert <=0){
                XueChengPlusException.cast("添加失败");
            }

        }else {
            //更新
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplan.setChangeDate(LocalDateTime.now());
            int i = teachplanMapper.updateById(teachplan);
            if (i<=0){
                XueChengPlusException.cast("修改失败");
            }
        }

    }

    @Override
    public void deleteTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null){
            XueChengPlusException.cast("删除的课程计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if (grade==1){
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid,teachplan.getId());
            List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
            if (teachplans != null && teachplans.size()>0){
                XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
            }else {
                int i = teachplanMapper.deleteById(teachplan);
                if (i<=0){
                    XueChengPlusException.cast("删除课程计划失败");
                }

            }
        }else {
            int i = teachplanMapper.deleteById(teachplan);
            if (i<=0){
                XueChengPlusException.cast("删除课程计划失败");
            }
            //删除媒体
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId,teachplanId);
            teachplanMediaMapper.delete(queryWrapper);
        }



    }

    @Override
    public void movedownTeachplan(String type, Long teachplanId) {
        if (StringUtils.isEmpty(type)){
            XueChengPlusException.cast("移动类型不能为空");
        }
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null){
            XueChengPlusException.cast("移动的课程计划不存在呢");
        }
        Long courseId = teachplan.getCourseId();

        List<TeachplanDto> teachplanDtoList = teachplanMapper.selectTreeNodes(courseId);
        if ("movedown".equals(type)){
            movedown(teachplan,teachplanDtoList);
        }else if ("moveup".equals(type)){
            moveup(teachplan,teachplanDtoList);
        }else {
            XueChengPlusException.cast("移动类型不支持");
        }

    }

    public void movedown(Teachplan teachplan,List<TeachplanDto> teachplanDtoList){
        Integer grade = teachplan.getGrade();
        Long teachplanId = teachplan.getId();
        Long parentid = teachplan.getParentid();

        if (grade == 1){
            TeachplanDto dto1 = teachplanDtoList.stream().filter(t -> t.getId().equals(teachplanId)).findFirst().get();
            int i = teachplanDtoList.indexOf(dto1);

            if (i+1>=teachplanDtoList.size()){
                return;
            }
            TeachplanDto dto2 = teachplanDtoList.get(i + 1);

            Teachplan newteachplan = new Teachplan();
            BeanUtils.copyProperties(dto1, newteachplan);
            newteachplan.setChangeDate(LocalDateTime.now());
            newteachplan.setOrderby(dto2.getOrderby());
            teachplanMapper.updateById(newteachplan);


            BeanUtils.copyProperties(dto2, newteachplan);
            newteachplan.setOrderby(dto1.getOrderby());
            newteachplan.setChangeDate(LocalDateTime.now());
            teachplanMapper.updateById(newteachplan);
        }
        if (grade == 2){
            TeachplanDto dto = teachplanDtoList.stream().filter(t -> t.getId().equals(parentid)).findFirst().get();
            List<TeachplanDto> teachPlanTreeNodes = dto.getTeachPlanTreeNodes();

            TeachplanDto dto1 = teachPlanTreeNodes.stream().filter(t -> t.getId().equals(teachplanId)).findFirst().get();
            int i = teachPlanTreeNodes.indexOf(dto1);
            if (i+1 >= teachPlanTreeNodes.size()){
                return;
            }
            TeachplanDto dto2 = teachPlanTreeNodes.get(i + 1);

            Teachplan newteachplan = new Teachplan();

            BeanUtils.copyProperties(dto1, newteachplan);
            newteachplan.setChangeDate(LocalDateTime.now());
            newteachplan.setOrderby(dto2.getOrderby());
            teachplanMapper.updateById(newteachplan);

            BeanUtils.copyProperties(dto2, newteachplan);
            newteachplan.setOrderby(dto1.getOrderby());
            newteachplan.setChangeDate(LocalDateTime.now());
            teachplanMapper.updateById(newteachplan);

        }

    }
    public void moveup(Teachplan teachplan,List<TeachplanDto> teachplanDtoList){
        Integer grade = teachplan.getGrade();
        Long teachplanId = teachplan.getId();
        Long parentid = teachplan.getParentid();

        if (grade == 1){
            TeachplanDto dto1 = teachplanDtoList.stream().filter(t -> t.getId().equals(teachplanId)).findFirst().get();
            int i = teachplanDtoList.indexOf(dto1);

            if (i-1<0){
                return;
            }
            TeachplanDto dto2 = teachplanDtoList.get(i-1);

            Teachplan newteachplan = new Teachplan();
            BeanUtils.copyProperties(dto1, newteachplan);
            newteachplan.setChangeDate(LocalDateTime.now());
            newteachplan.setOrderby(dto2.getOrderby());
            teachplanMapper.updateById(newteachplan);


            BeanUtils.copyProperties(dto2, newteachplan);
            newteachplan.setOrderby(dto1.getOrderby());
            newteachplan.setChangeDate(LocalDateTime.now());
            teachplanMapper.updateById(newteachplan);
        }
        if (grade == 2){
            TeachplanDto dto = teachplanDtoList.stream().filter(t -> t.getId().equals(parentid)).findFirst().get();
            List<TeachplanDto> teachPlanTreeNodes = dto.getTeachPlanTreeNodes();

            TeachplanDto dto1 = teachPlanTreeNodes.stream().filter(t -> t.getId().equals(teachplanId)).findFirst().get();
            int i = teachPlanTreeNodes.indexOf(dto1);
            if (i-1<0){
                return;
            }
            TeachplanDto dto2 = teachPlanTreeNodes.get(i - 1);

            Teachplan newteachplan = new Teachplan();

            BeanUtils.copyProperties(dto1, newteachplan);
            newteachplan.setChangeDate(LocalDateTime.now());
            newteachplan.setOrderby(dto2.getOrderby());
            teachplanMapper.updateById(newteachplan);

            BeanUtils.copyProperties(dto2, newteachplan);
            newteachplan.setOrderby(dto1.getOrderby());
            newteachplan.setChangeDate(LocalDateTime.now());
            teachplanMapper.updateById(newteachplan);

        }

    }

    /**
     * @description 获取最新的排序号
     * @param courseId  课程id
     * @param parentId  父课程计划id
     * @return int 最新排序号
     * @author Mr.M
     * @date 2022/9/9 13:43
     */
    private int getTeachplanCount(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }

    /**
     * @deprecated 获取最大的排序号
     * @param courseId
     * @param parentId
     * @return
     */
    private int getTeacherplanMaxOrderBy(long courseId,long parentId){
        Integer integer = teachplanMapper.selectMaxOrderBy(courseId, parentId);
        if (integer == null){
            integer = 0;
        }
        return integer;
    }
}
