package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.AddCourseTeacherDto;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CourseTeacherService {

    public List<CourseTeacherDto> list(@PathVariable Long courseId);

    public CourseTeacherDto addTeacher(Long companyId,@RequestBody AddCourseTeacherDto dto);

    public CourseTeacher updateTeacher(Long companyId,@RequestBody CourseTeacher courseTeacher);

    public void  deleteTeacher(Long companyId,@PathVariable Long courseId, @PathVariable Long teacherId);
}
