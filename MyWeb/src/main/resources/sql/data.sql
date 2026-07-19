-- news data
insert into `news` (`title`, `released_date`, `content`, `created_at`, `created_by`) VALUES
('AI Revolution in Education', '2025-05-01', 'AI is transforming the way students learn and teachers teach.', NOW(), 'admin'),
('Global Markets Rally', '2025-05-02', 'Stock markets saw significant gains after economic data release.', NOW(), 'admin'),
('New Java Version Released', '2025-05-03', 'Java 22 introduces major performance enhancements.', NOW(), 'tech_writer'),
('SpaceX Launch Successful', '2025-05-04', 'SpaceX launched another crewed mission to the ISS.', NOW(), 'space_admin'),
('Olympics Preparation Underway', '2025-05-05', 'Host cities ramp up efforts ahead of the global event.', NOW(), 'sports_team');

-- contact_msg data
insert into `contact_msg` (`name`, `mobile`, `email`, `subject`, `message`, `status`, `created_at`, `created_by`) VALUES
('Adam', '2176436587', 'zadam@gmail.com', 'Regarding a job', 'Wanted to join as teacher', 'OPEN', CURDATE(), 'DBA'),
('Zara', '3412654387', 'zarabaig@hotmail.com', 'Course Admission', 'Wanted to join a course', 'OPEN', CURDATE(), 'DBA'),
('Marques', '8547643673', 'kmarques@yahoo.com', 'Course Review', 'Review of Development course', 'OPEN', CURDATE(), 'DBA'),
('Shyam', '4365328776', 'gshyam@gmail.com', 'Admission Query', 'Need to talk about admission', 'OPEN', CURDATE(), 'DBA'),
('John', '5465765453', 'doejohn@gmail.com', 'Holiday Query', 'Query on upcoming holidays', 'OPEN', CURDATE(), 'DBA'),
('Taniya Bell', '3987463827', 'belltaniya@gmail.com', 'Child Scholarship', 'Can my child get scholarship?', 'OPEN', CURDATE(), 'DBA'),
('Willie Lara', '4568764801', '476lara@gmail.com', 'Need Admission', 'My son need an admission', 'OPEN', CURDATE(), 'DBA'),
('Jonathan Parsons', '4321768902', 'jonathan.parsons@gmail.com', 'Course feedback', 'Music course is good', 'OPEN', CURDATE(), 'DBA'),
('Cloe Rubio', '9854438719', 'rubio987@gmail.com', 'Correct Date of Birth', 'My Child DOB needs to be corrected', 'OPEN', CURDATE(), 'DBA'),
('Camilla Stein', '6545433254', 'camillas@gmail.com', 'Transport Query', 'Is Transport provided?', 'OPEN', CURDATE(), 'DBA'),
('Lizeth Gross', '4678783434', 'grossliz@yahoo.com', 'Progress report', 'Please send progress report', 'OPEN', CURDATE(), 'DBA'),
('Yael Howe', '1243563254', 'howeyael@gmail.com', 'Certificate Query', 'Need Certificate hard copy', 'OPEN', CURDATE(), 'DBA'),
('Ian Moreno', '2312231223', 'moreno.ian@gmail.com', 'Food feedback', 'Food quality can be improved', 'OPEN', CURDATE(), 'DBA'),
('Desirae Ibarra', '3445235667', 'ibarrades@gmail.com', 'Traffic Complaint', 'Traffic around school can be controlled', 'OPEN', CURDATE(), 'DBA'),
('Oswaldo Jarvis', '4556121265', 'jarvissmile@hotmail.com', 'Study Tour', 'Study tour details needed', 'OPEN', CURDATE(), 'DBA'),
('Miah Perkins', '2367784512', 'perkinsmiah@hotmail.com', 'Vaccination Support', 'Vaccination center in the school', 'OPEN', CURDATE(), 'DBA'),
('Zion Bolton', '8990678900', 'boltzion@gmail.com', 'Course fees', 'Pls share fees of music course', 'OPEN', CURDATE(), 'DBA'),
('Dominik Tanner', '4556127834', 'tannerdominik@gmail.com', 'Games schedule', 'Provide Summer games schedule', 'OPEN', CURDATE(), 'DBA');

-- roles
insert into `roles` (`role_name`, `created_at`, `created_by`)
VALUES
    ('ADMIN', CURDATE(), 'DBA'),
    ('STUDENT', CURDATE(), 'DBA');

-- plans
insert into `plan` (`name`, `created_at`, `created_by`) VALUES
('Free', CURDATE(), 'DBA'),
('Basic', CURDATE(), 'DBA'),
('Standard', CURDATE(), 'DBA');

-- courses
insert into `courses` (`name`, `fees`, `created_at`, `created_by`)
VALUES
    ('Spring Boot Basics', '1000', NOW(), 'DBA'),
    ('Advanced Java', '1500', NOW(), 'DBA'),
    ('Web Development with Thymeleaf', '1200', NOW(), 'DBA'),
    ('Hibernate & JPA Essentials', '1300', NOW(), 'DBA'),
    ('RESTful API Design', '1400', NOW(), 'DBA');

-- admin & student
insert into `person` (`name`, `email`, `mobile`, `password`, `role_id`, `created_at`, `created_by`)
VALUES
    ('Admin', 'admin@gmail.com', '3443434343', '$2a$10$XhU4UcSxDPb5G0I0fT/CZ.Lfj2VW2fkLkUP5cOEM.xM8EzyUQXaD2', 1, CURDATE(), 'DBA'),
    ('Student', 'student@gmail.com', '3443434343', '$2a$12$eRFpMHv.UUCq6lqII16TjuWUtJnSPTY0vWDr2t0gOtl2v2XvNb0Ii', 2, CURDATE(), 'DBA');

insert into `person` (`name`, `email`, `mobile`, `password`, `role_id`, `plan_id`, `created_at`, `created_by`)
VALUES
    ('Anthony', 'anthony@gmail.com', '0909963886', '$2a$12$EBrtxiTmAkBy9nLlKjeeJeYP.hAUSYhSHq0.3pegSWejyXk349Vq6', 2, 2, CURDATE(), 'DBA');

-- person_courses
insert into person_courses (person_id, course_id)
values (3, 3);