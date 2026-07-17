package com.company.MyWeb.controller.authenticated;

import com.company.MyWeb.model.Courses;
import com.company.MyWeb.model.Person;
import com.company.MyWeb.repository.CoursesRepository;
import com.company.MyWeb.repository.PersonRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final CoursesRepository coursesRepository;
    private final PersonRepository personRepository;

    @Autowired
    public StudentController(CoursesRepository coursesRepository, PersonRepository personRepository) {
        this.coursesRepository = coursesRepository;
        this.personRepository = personRepository;
    }

    /**
     * This method is to show all the courses a person is enrolled by sending the loggedInPerson object to view using session
     */
    @GetMapping("/viewEnrolledCourses")
    public String viewEnrolledCourses(HttpSession session, Model model) {
        Person person = (Person) session.getAttribute("loggedInPerson");
        model.addAttribute("person", person);
        return "authenticated/studentOnly/viewEnrolledCourses";
    }

    /**
     * This method is to show all available courses created by Admin for person to register using checked box
     * and to disable the checked box for the courses the person has previously registered. Hence sending courseList and a set of alreadyRegisterdCourses to the view
     */
    @GetMapping("/signUpCourses")
    public String signUpCourses(Model model, HttpSession session) {
        List<Courses> courseList = coursesRepository.findAll();
        model.addAttribute("courseList", courseList);
        Person person = (Person) session.getAttribute("loggedInPerson");
        model.addAttribute("alreadyRegisteredCourses", person.getCourses());
        return "/authenticated/studentOnly/signUpCourses";
    }

    /**
     * This method is to receive the courseId of the courses a user selected via @RequestParam. If not selected, redirect and display error message.
     * Store the courses into a person's courses' list using the list of courseIds from the form
     * update the loggInPerson for the change
     */
    @PostMapping("/purchaseSelectedCourse")
    public String purchaseSelectedCourses(@RequestParam(value = "selectedCourses", required = false) List<Integer> listOfCourseIds, HttpSession session, RedirectAttributes redirectAttributes) {
        if (listOfCourseIds == null || listOfCourseIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select at least one course");
            return "redirect:/student/signUpCourses";
        }
        //1) fetch the person from either session or Authentication
        Person person = (Person) session.getAttribute("loggedInPerson");

        //2) fetch the courses the person has selected and add them to person's course set
        for (Integer courseId : listOfCourseIds) {
            person.getCourses().add(coursesRepository.findById(courseId).get());
        }
        //3) persist to the person object
        Person updatedPerson = personRepository.save(person);

        //4) update the loggedInPerson session to be consistent
        session.setAttribute("loggedInPerson", updatedPerson);

        //5) redirect with a success query
        return "redirect:/student/signUpCourses?success";
    }

}
