package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseTeacher;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class AddCourseTeacherDto {

    private Long id;
    @NotEmpty(message = "courseId不能为空")
    private Long courseId;
    @NotEmpty(message = "老师为空")
    private String teacherName;
    @NotEmpty(message = "职能为空")
    private String position;
    @NotEmpty(message = "简介为空")
    private String introduction;
}
