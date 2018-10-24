package com.antoineriche.privateinstructor.utils;

import com.antoineriche.privateinstructor.beans.Course;

import java.util.List;

public class CourseUtils {

    public static double extractMoneySum(List<Course> pCourses){
        return pCourses.stream().filter(c -> c.getState() != Course.CANCELED).mapToDouble(c -> c.getMoney()).sum();
    }
}
