package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.AddCourseTeacherDto;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacherDto> list(@PathVariable Long courseId){
        List<CourseTeacherDto> list = courseTeacherService.list(courseId);
        return list;
    }
    @PostMapping("/courseTeacher")
    public CourseTeacherDto addTeacher(@RequestBody AddCourseTeacherDto dto){
        Long companyId = 1232141425L;
        return courseTeacherService.addTeacher(companyId,dto);
    }


    @PutMapping("/courseTeacher")
    public CourseTeacher updateTeacher(@RequestBody CourseTeacher courseTeacher){
        Long companyId = 1232141425L;
        return courseTeacherService.updateTeacher(companyId,courseTeacher);
    }

    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void  deleteTeacher(@PathVariable Long courseId, @PathVariable Long teacherId){
        Long companyId = 1232141425L;
        courseTeacherService.deleteTeacher(companyId,courseId,teacherId);
    }

}
