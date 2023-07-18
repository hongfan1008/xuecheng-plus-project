package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.util.BeanCopyUtil;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.AddCourseTeacherDto;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotEmpty;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Autowired
    private CourseBaseMapper courseBaseMapper;


    public List<CourseTeacherDto> list(@PathVariable Long courseId){
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        List<CourseTeacherDto> list = BeanCopyUtil.copyListProperties(courseTeachers, CourseTeacherDto::new);
        return list;
    }

    @Override
    public CourseTeacherDto addTeacher(Long companyId,AddCourseTeacherDto dto) {
        Long courseId = dto.getCourseId();
        CourseTeacherDto teacherDto = new CourseTeacherDto();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null){
            XueChengPlusException.cast("添加教师的课程不存在");
        }
        if (!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能向本机构课程中添加教师");
        }

        Long id = dto.getId();
        if ( id== null){
            CourseTeacher courseTeacher = new CourseTeacher();
            BeanCopyUtil.copyProperties(dto,courseTeacher);
            courseTeacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (insert <= 0){
                XueChengPlusException.cast("添加教师失败");
            }

            BeanUtils.copyProperties(courseTeacher,teacherDto);
            return teacherDto;
        }else {
            CourseTeacher teacher = courseTeacherMapper.selectById(id);
            if (teacher == null){
                XueChengPlusException.cast("教师不存在");
            }
            BeanCopyUtil.copyProperties(dto,teacher);
            int i = courseTeacherMapper.updateById(teacher);
            if (i<=0){
                XueChengPlusException.cast("教师信息修改失败");
            }
            BeanUtils.copyProperties(teacher,teacherDto);
            return teacherDto;
        }

    }

    @Override
    public CourseTeacher updateTeacher(Long companyId, CourseTeacher courseTeacher) {
        CourseTeacher teacher = courseTeacherMapper.selectById(courseTeacher.getId());
        if (teacher == null){
            XueChengPlusException.cast("教师不存在");
        }


        teacher = courseTeacher;
        int i = courseTeacherMapper.updateById(teacher);
        if (i<=0){
            XueChengPlusException.cast("教师信息修改失败");
        }
        return teacher;
    }

    @Override
    public void deleteTeacher(Long companyId, Long courseId, Long teacherId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null){
            XueChengPlusException.cast("删除教师的课程不存在");
        }
        if (!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能删除本机构的课程");
        }

        int i = courseTeacherMapper.deleteById(teacherId);
        if (i<=0){
            XueChengPlusException.cast("删除失败");
        }
    }


}
