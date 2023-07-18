package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface CourseBaseInfoService {

    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    public CourseBaseInfoDto creatCourseBase(Long companyId, AddCourseDto dto);

//    public CourseBaseInfoDto getCourseById(Long courseId);
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    public CourseBaseInfoDto modifyCourseBase(Long companyId, EditCourseDto editCourseDto);

    public void deleteCourse( Long id);
}
