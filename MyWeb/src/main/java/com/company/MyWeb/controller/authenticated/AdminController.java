package com.company.MyWeb.controller.authenticated;

import com.company.MyWeb.model.Contact;
import com.company.MyWeb.model.Courses;
import com.company.MyWeb.model.Plan;
import com.company.MyWeb.model.Person;
import com.company.MyWeb.repository.CoursesRepository;
import com.company.MyWeb.repository.PlanRepository;
import com.company.MyWeb.repository.PersonRepository;
import com.company.MyWeb.service.ContactService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.company.MyWeb.constant.ProjectConstant.ANSI_GREEN;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ContactService contactService;
    private final PlanRepository planRepository;
    private final PersonRepository personRepository;
    private final CoursesRepository coursesRepository;

    @Autowired
    public AdminController(ContactService contactService, PlanRepository planRepository, PersonRepository personRepository, CoursesRepository coursesRepository) {
        this.contactService = contactService;
        this.planRepository = planRepository;
        this.personRepository = personRepository;
        this.coursesRepository = coursesRepository;
    }

    //contact messages
//    @GetMapping("/contactMessage")
//    public String displayMessage(Model model) {
//        List<Contact> contacts = contactService.findContactWithOpenStatus();
//        model.addAttribute("contactsWithOpenStatus", contacts);
//        return "contactMessage";
//    }

    /*************************************************** ViewContactMessage handler starts here ***************************************************/

    /**
     * This method is to create a Page object using the query and path parameter provided, and convert it to a list and send it the view along with other Page properties
     * to perform JPA pagination.
     * In order for pagination works, for every pagination function to work, it will route to this handler with the query parameter specifying the kind of
     * contact list will be sent to view for display
     */
    @GetMapping("/viewContactMessage/page/{currentPageNum}")
    public String viewContactMessage(Model model,
                                     @PathVariable("currentPageNum") int currentPageNum,
                                     @RequestParam("sortField") String sortField,
                                     @RequestParam("sortDir") String sortDir) {
        //1) Instantiate Page object with page-index, item-per-page, sort-dir, field-to-sort-by, and matched-field defined
        Page<Contact> pageOfContacts = contactService.findContactWithOpenStatus(currentPageNum, sortField, sortDir);

        //2) Convert it to a list with the property Page defined and send it to view
        List<Contact> listOfContactsPerPage = pageOfContacts.getContent();
        model.addAttribute("listOfContactsPerPage", listOfContactsPerPage);

        //3) Send the these properties to set up the display logic of pagination and header sorting icon
        model.addAttribute("currentPageNum", currentPageNum);
        model.addAttribute("totalPage", pageOfContacts.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "authenticated/adminOnly/contactMessage";
    }

    /**
     * This method is to change the contact status from OPEN to CLOSED based on the contactId
     */
    @GetMapping("/closeMessage")
    public String closeMessage(@RequestParam int contactId,
                               @RequestParam("currentPageNum") int currentPageNum,
                               @RequestParam("sortField") String sortField,
                               @RequestParam("sortDir") String sortDir, Authentication authentication) {
        //1) Set contact status to CLOSED
        boolean statusChangedToClosed = contactService.updateContactStatus(contactId, authentication);
        //remain on the same page and sortField and sortDir after closing
        return "redirect:/admin/viewContactMessage/page/" + currentPageNum
                + "?sortField=" + sortField
                + "&sortDir=" + sortDir;
    }

    /*************************************************** Plan handler starts here ***************************************************/

    /**
     * This method is called *before* each handler and the return value will be sent to UI for display.
     * It is designed to display the most up-to-date plan information on planPage
     * The returned list will be available in the model for all views rendered by this controller.
     */
    @ModelAttribute("planList")
    public List<Plan> displayAllPlans() {
        return planRepository.findAll();
    }

    /**
     * This method creates a plan object through a model to UI to be added
     */
    @GetMapping("/planPage")
    public String planPage(Model model) {
        model.addAttribute("plan", new Plan());
        return "authenticated/adminOnly/planPage";
    }

    /**
     * This method validates the input field of the plan object before save
     */
    @PostMapping("/addNewPlan")
    /*If a controller method has a non-primitive parameter (like Plan), Spring automatically Binds form fields to it, and if a view is returned (not redirect)*/
    public String addNewPlan(@Valid Plan plan, BindingResult result) {
        if (result.hasErrors()) return "authenticated/adminOnly/planPage";
        planRepository.save(plan);
        return "redirect:/admin/planPage";
    }

    /**
     * This method deletes a Plan.
     * Before deleting, we must detach the foreign key association from each related Person
     * by setting their `plan` field to null and saving the changes.
     * <p>
     * If we skip this step, the Person entries will still reference a non-existent Plan,
     * which will lead to a foreign key constraint violation at the database level.
     */
    @GetMapping("/deletePlan")
    public String deletePlan(@RequestParam int planId) {
        //1) retrieve the plan to be deleted based on the planId specified.
        Optional<Plan> planToBeDeleted = planRepository.findById(planId); //JpaRepository built-in method

        //2) Detaching the foreign key to the planToBeDeleted from each person by setting it null and update the result
        for (Person person : planToBeDeleted.get().getPersons()) {
            person.setPlan(null);
            personRepository.save(person); //this can be omitted
        }
        //3) delete the plan and return to the planPage
        planRepository.deleteById(planId);
        return "redirect:/admin/planPage";
    }

    /**
     * This method is to view particular Plan details based on the planId provided by the query parameter.
     * The detail page provides two main functionalities
     * 1. display a list of person who currently enrolled in the particular plan
     * 2. perform add/delete person operation based on person's username (email)
     */
    @GetMapping("/viewPlanDetail")
    public String viewPlanDetail(@RequestParam int planId, Model model, HttpSession session) {
        //1) Fetch the particular Plan based on query parameter planId provided from UI
        Optional<Plan> thePlanOptional = planRepository.findById(planId);

        //2) Send the particular plan to UI and display the enrolled persons
        model.addAttribute("thePlan", thePlanOptional.get());

        //3) Send empty person objet to UI and store the email entered by Admin
        model.addAttribute("person", new Person());

        //4) Store the particular plan in session for /addStudent to identify which
        session.setAttribute("thePlanInSession", thePlanOptional.get());

        return "authenticated/adminOnly/viewPlanDetail";
    }

    /**
     * This method is to add a person to the particular plan based on the username (email) if there is an email match in the database.
     * Display an error message otherwise
     */
    @PostMapping("/addStudent")
    public String addStudent(Person person, HttpSession session) {

        //1) fetch the person object that matches the email entered by admin
        Person thePerson = personRepository.readByEmail(person.getEmail()); //if not found, return null

        //2) fetch the particular plan object from session
        Plan thePlanInSession = (Plan) session.getAttribute("thePlanInSession");

        if (thePerson == null)
            return "redirect:/admin/viewPlanDetail?planId=" + thePlanInSession.getPlanId() + "&error=personNotFound";

        //3) attach foreign key relationship on the owning side
        thePerson.setPlan(thePlanInSession);

        /*this line is only necessary to keep the plan object consistent to the database*/
        thePlanInSession.getPersons().add(thePerson);

        //4) update the owning side to the database
        Person updatedPerson = personRepository.save(thePerson);
        log.info(ANSI_GREEN + Thread.currentThread().getStackTrace()[1].getMethodName() + " updatedPerson: " + updatedPerson);
        log.info(ANSI_GREEN + Thread.currentThread().getStackTrace()[1].getMethodName() + " thePlanInSession: " + thePlanInSession + " | getPersons(): " + thePlanInSession.getPersons());

        //5) redirect to viewPlanDetail with query parameter planId to display a list with the most up-to-date person information
        return "redirect:/admin/viewPlanDetail?planId=" + thePlanInSession.getPlanId();
    }

    /**
     * This method is to remove the student from the particular plan by detaching their relationship
     */
    @GetMapping("/removeStudent")
    public String removeStudent(@RequestParam int personId) {

        //1) fetch person based on the query parameter personId
        Optional<Person> thePersonToBeRemovedOptional = personRepository.findById(personId);
        Person thePersonToBeRemoved = thePersonToBeRemovedOptional.get();

        //2) fetch the plan this person to be removed from
        Plan thisPlanToBeRemovedFrom = thePersonToBeRemoved.getPlan();

        //3) detach the person from Plan by setting it null
        thePersonToBeRemoved.setPlan(null);

        /*this only acts as a safeguard to remove person from persons' list, Hibernate may handle it but not guaranteed*/
        thisPlanToBeRemovedFrom.getPersons().remove(thePersonToBeRemoved);

        //4) update the detached relationship in the database via the owning class Person
        personRepository.save(thePersonToBeRemoved);
        log.info(ANSI_GREEN + Thread.currentThread().getStackTrace()[1].getMethodName() + " thePersonToBeRemoved: " + thePersonToBeRemoved);
        log.info(ANSI_GREEN + Thread.currentThread().getStackTrace()[1].getMethodName() + " thisPlanToBeRemovedFrom: " + thisPlanToBeRemovedFrom + " | getPersons(): " + thisPlanToBeRemovedFrom.getPersons());

        //5) redirect to viewPlanDetail with query parameter planId to display a list with the most up-to-date person information
        return "redirect:/admin/viewPlanDetail?planId=" + thisPlanToBeRemovedFrom.getPlanId();
    }

    /*************************************************** Course handler starts here ***************************************************/

    /**
     * This method is to send a list of already created class to UI with the model named "courseList"
     */
    @ModelAttribute("courseList")
    public List<Courses> displayAllCourses() {
        return coursesRepository.findAll(Sort.by(Sort.Direction.ASC, "courseId")); //JPA dynamic sorting
    }

    /**
     * This method is to send a Course object to UI via the model to create a course object
     */
    @GetMapping("/coursePage")
    public String coursePage(Model model) {
        model.addAttribute("course", new Courses());
        return "authenticated/adminOnly/coursePage";
    }

    /**
     * Receive the Course object via form entered by user and validate the input
     * If passes the validation save it into the database
     */
    @PostMapping("/addNewCourse")
    public String addNewCourse(@Valid @ModelAttribute("course") Courses course, BindingResult result) {
        if (result.hasErrors()) return "authenticated/adminOnly/coursePage";
        coursesRepository.save(course);
        return "redirect:/admin/coursePage";
    }

    /**
     * Delete the course by query parameter courseId
     */
    @GetMapping("/deleteCourse")
    public String deleteCourse(@RequestParam int courseId) {
        //1) fetch the course by courseId
        Optional<Courses> courseToBeDeletedOptional = coursesRepository.findById(courseId);
        Courses courseToBeDeleted = courseToBeDeletedOptional.get();
        //2) detach the relationship from each person who already registered in the course
        for (Person person : courseToBeDeleted.getPersons()) {
            person.getCourses().remove(courseToBeDeleted);
            personRepository.save(person); //this can be omitted
        }
        //3) delete the course once persons are detached
        coursesRepository.deleteById(courseId);
        return "redirect:/admin/coursePage";
    }

    /**
     * This method is to provide course info in the course detail page. Therefore we will need to send the course object through the model to the UI
     * Since course detail page also provides the function of adding students to enroll the particular course, we will send a person object through model to UI
     * for user enter which specific student to enroll based on their email.
     */
    @GetMapping("/viewCourseDetail")
    public String viewCourseDetail(@RequestParam int courseId, Model model, HttpSession session) {
        //1) fetch the course object based on query parameter courseId
        Optional<Courses> courseDetailOptional = coursesRepository.findById(courseId);
        Courses courseDetail = courseDetailOptional.get();
        //2) send both course and person to UI
        model.addAttribute("course", courseDetail);
        model.addAttribute("person", new Person());
        //3) store particular course in session for other controller to perform adding and removing the persons
        session.setAttribute("courseInSession", courseDetail);
        return "authenticated/adminOnly/viewCourseDetail";
    }

    /**
     * This method is check whether the email entered by admin already exists in the database, if so establish the relationship between person and the course
     */
    @PostMapping("/addCourseStudent")
    public String addCourseStudent(Person person, HttpSession session) {
        //1) fetch the course to which the person wants to enroll in.
        Courses courseInSession = (Courses) session.getAttribute("courseInSession");
        //2) find the person based on the email entered by admin
        Person thePerson = personRepository.readByEmail(person.getEmail());
        //3) if not found redirect to the same page with error message
        if (thePerson == null)
            return "redirect:/admin/viewCourseDetail?courseId=" + courseInSession.getCourseId() + "&error=personNotFound";
        //4) Check if the person is already assigned to this course, if so display the error message
        if (courseInSession.getPersons().contains(thePerson)) {
            return "redirect:/admin/viewCourseDetail?courseId=" + courseInSession.getCourseId() + "&error=alreadyEnrolled";
        }
        //5) if the person is found, add the course reference to the person and persist
        thePerson.getCourses().add(courseInSession);
        courseInSession.getPersons().add(thePerson); //this can also be omitted
        personRepository.save(thePerson);
        return "redirect:/admin/viewCourseDetail?courseId=" + courseInSession.getCourseId();
    }

    /**
     * This method is to remove the person from the particular course given the personId and course object from session
     * Detach their relationship and perform save
     */
    @GetMapping("/deleteCourseStudent")
    public String deleteCourseStudent(@RequestParam int personId, HttpSession session) {
        //1) fetch the person object that is to be removed from the course
        Optional<Person> personTobeRemovedOptional = personRepository.findById(personId);
        Person personTobeRemoved = personTobeRemovedOptional.get();
        //2) fetch the course the person wants to be removed from through session
        Courses courseInSession = (Courses) session.getAttribute("courseInSession");
        //3) remove the course from person and update
        personTobeRemoved.getCourses().remove(courseInSession);
        personRepository.save(personTobeRemoved);
        return "redirect:/admin/viewCourseDetail?courseId=" + courseInSession.getCourseId();
    }

}
