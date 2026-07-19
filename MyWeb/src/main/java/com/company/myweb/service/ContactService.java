package com.company.myweb.service;

import com.company.myweb.config.MyWebProperties;
import com.company.myweb.constant.ProjectConstant;
import com.company.myweb.model.Contact;
import com.company.myweb.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.company.myweb.constant.ProjectConstant.ANSI_GREEN;
import static com.company.myweb.constant.ProjectConstant.ANSI_RESET;

@Slf4j
@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final MyWebProperties myWebProperties;

    @Autowired
    public ContactService(ContactRepository contactRepository, MyWebProperties myWebProperties) {
        this.contactRepository = contactRepository;
        this.myWebProperties = myWebProperties;
    }

    public Contact saveContact(Contact contact) {
        //name, mobile, email, subject, message are set via form; contactId, createdAt, createdBy, updatedAt, updatedBy are left to be set
        log.info(ANSI_GREEN + "Contact field set via form " + contact.toString() + ANSI_RESET); //Contact field set via formContact(super=BaseEntity(createdAt=null, createdBy=null, updatedAt=null, updatedBy=null), contactId=0, name=abc, mobile=1234567890, email=a@a, subject=abc, message=abc, status=null)

        contact.setStatus(ProjectConstant.STATUS_OPEN); //1) set status OPEN
        Contact savedContact = contactRepository.save(contact); //2) persist to db. JPA built-in method, rarely returns null either INSERT or UPDATE

        //contactId, createdAt, createdBy, updatedBy contactId are now set;
        log.info(ANSI_GREEN + "contact obj after JPA " + contact + ANSI_RESET); //contact obj after JPAContact(super=BaseEntity(createdAt=2025-06-12T23:00:39.439602100, createdBy=anonymousUser, updatedAt=2025-06-12T23:00:39.439602100, updatedBy=anonymousUser), contactId=19, name=abc, mobile=1234567890, email=a@a, subject=abc, message=abc, status=OPEN)
        log.info(ANSI_GREEN + "savedContact obj after JPA " + savedContact + ANSI_RESET); //savedContact obj after JPAContact(super=BaseEntity(createdAt=2025-06-12T23:00:39.439602100, createdBy=anonymousUser, updatedAt=2025-06-12T23:00:39.439602100, updatedBy=anonymousUser), contactId=19, name=abc, mobile=1234567890, email=a@a, subject=abc, message=abc, status=OPEN)
        log.info(ANSI_GREEN + "Are contact and savedContact the same? " + (contact == savedContact) + ANSI_RESET); //true

        return savedContact;
    }

    /*Pagination related method*/

    /**
     * This method is to take the required parameters for creating Pageable and once Pageable is created, pass it to contactRepository to create Page<Contat>
     * object
     */
    public Page<Contact> findContactWithOpenStatus(int currentPageNum, String sortField, String sortDir) {
        int sizePerPage = myWebProperties.getPaginationPageSize();
        //1) create Pageable instance
        Pageable pageable = PageRequest.of(
                currentPageNum - 1,                                          //1. Zero-based page index | Spring pages are 0-based, so subtract 1 from user input
                sizePerPage,                                                            //2. Number of items per page
                sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,       //3. Sort direction
                sortField                                                     //4. Field to sort by
        );
        //2) JPQL method return Page
        return contactRepository.findByStatusWithPageableAtQuery(ProjectConstant.STATUS_OPEN, pageable);    //5. define the matching message
    }

    /**
     * This method is to change the status of contact object from OPEN to CLOSED and to update the createdBy and updatedBy manually since it is JPQL query
     * that AuditorAware does not automatically handle BaseEntity class fields
     */
    // Using @Query method
    public boolean updateContactStatus(int id, Authentication authentication) {
        return contactRepository.updateStatusById(ProjectConstant.STATUS_CLOSED, authentication.getName(), id) > 0; //[email]
    }
}
