package com.company.myweb.controller.authenticated;

import com.company.myweb.model.Address;
import com.company.myweb.model.Person;
import com.company.myweb.model.Profile;
import com.company.myweb.repository.PersonRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import static com.company.myweb.constant.ProjectConstant.ANSI_GREEN;
import static com.company.myweb.constant.ProjectConstant.ANSI_RESET;

@Slf4j
@Controller
public class ProfilePageController {

    private final PersonRepository personRepository;

    @Autowired
    public ProfilePageController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * 顯示個人資料頁 — 把 session 內 Person 的欄位複製到 Profile DTO 送給前端顯示
     */
    @GetMapping("/profilePage")
    public String displayProfile(Model model, HttpSession session) {
        // 1) 從 session 取 Person，把基本欄位複製到 Profile
        Person person = (Person) session.getAttribute("loggedInPerson");
        Profile profile = new Profile();
        profile.setName(person.getName());
        profile.setMobile(person.getMobile());
        profile.setEmail(person.getEmail());

        // 2) 若已有 Address，把地址欄位也複製過去（新使用者首次註冊時 Address 為 null）
        if (person.getAddress() != null) {
            profile.setAddress1(person.getAddress().getAddress1());
            profile.setAddress2(person.getAddress().getAddress2());
            profile.setCity(person.getAddress().getCity());
            profile.setZipcode(person.getAddress().getZipCode());
        }
        // 3) 送 Profile DTO 給前端表單綁定，用來顯示與後續更新
        model.addAttribute("profile", profile);
        return "authenticated/profile";
    }

    /**
     * 更新個人資料 — 把 Profile DTO 的欄位複製回 Person 並存檔
     */
    @PostMapping("/updateProfile")
    public String updateProfile(@Valid @ModelAttribute("profile") Profile profile, BindingResult result, HttpSession session) {
        // 1) Bean Validation 檢查失敗回原表單
        if (result.hasErrors()) return "authenticated/profile";

        // 2) 從 session 取 Person
        Person person = (Person) session.getAttribute("loggedInPerson");

        // 3) 檢查新 email 是否已被別的 Person 使用（排除自己）→ 是則綁欄位錯誤回表單
        if (personRepository.existsByEmailAndPersonIdNot(profile.getEmail(), person.getPersonId())) {
            result.rejectValue(
                    "email",
                    "profile.email.exists",
                    "此 Email（" + profile.getEmail() + "）已被其他帳號使用"
            );
            return "authenticated/profile";
        }

        // 保存舊 email 供之後比對是否改動
        String oldEmail = person.getEmail();

        // 4) 更新 Person 基本欄位
        person.setName(profile.getName());
        person.setMobile(profile.getMobile());
        person.setEmail(profile.getEmail());

        // 5) 若使用者首次填地址，new 一個 Address（先前註冊時未建立）
        if (person.getAddress() == null) person.setAddress(new Address());

        // 6) 更新 Person 關聯的 Address 欄位
        person.getAddress().setAddress1(profile.getAddress1());
        person.getAddress().setAddress2(profile.getAddress2());
        person.getAddress().setCity(profile.getCity());
        person.getAddress().setZipCode(profile.getZipcode());

        // 7) 若 email 有變 → 在 save 前先更新 SecurityContext 的 Authentication
        //    這樣 AuditorAware 取 authentication.getName() 時拿到「新」email，updatedBy 才會記錄正確
        if (!oldEmail.equals(profile.getEmail())) {
            updateAuthentication(person);
        }

        // AuditorAware 會自動處理 UPDATE 時的 updatedBy / updatedAt
        Person savedPerson = personRepository.save(person);
        log.info(ANSI_GREEN + Thread.currentThread().getStackTrace()[1].getMethodName() + " savedPerson: " + savedPerson + ANSI_RESET);
        log.info(ANSI_GREEN + Thread.currentThread().getStackTrace()[1].getMethodName() + " savedPerson.getAddress(): " + savedPerson.getAddress() + ANSI_RESET);

        // 8) 更新 session 內的 loggedInPerson，保持資料一致
        session.setAttribute("loggedInPerson", savedPerson);
        return "redirect:/profilePage?updated=true";
    }

    /**
     * 若 email 變更，同步更新 SecurityContext 內的 Authentication（因為 authentication.getName() = 舊 email）
     * 否則 AuditorAware 之後拿到的還是舊 email
     */
    private void updateAuthentication(Person person) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        // 1) 用新 email 建 UserDetails
        UserDetails newUserDetails = User.builder()
                .username(person.getEmail())
                .password(person.getPassword())
                .authorities(currentAuth.getAuthorities())
                .build();

        // 2) 用新 UserDetails 建新 Authentication
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                currentAuth.getCredentials(),
                currentAuth.getAuthorities()
        );

        // 3) 覆蓋 SecurityContext 內的 Authentication
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
