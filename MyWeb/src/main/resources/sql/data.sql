-- news data
insert into `news` (`title`, `released_date`, `content`, `created_at`, `created_by`) VALUES
('AI 引領教育革新', '2025-05-01', 'AI 正在改變學生的學習方式與教師的教學模式。', NOW(), 'admin'),
('全球市場上漲', '2025-05-02', '經濟數據公布後，全球股市出現明顯漲幅。', NOW(), 'admin'),
('新版 Java 正式發布', '2025-05-03', 'Java 22 帶來多項重要的效能改進。', NOW(), 'tech_writer'),
('SpaceX 發射任務成功', '2025-05-04', 'SpaceX 再次成功執行前往國際太空站的載人任務。', NOW(), 'space_admin'),
('奧運籌備工作展開', '2025-05-05', '主辦城市正加緊準備這項全球體育盛事。', NOW(), 'sports_team');

-- contact_msg data
insert into `contact_msg` (`name`, `mobile`, `email`, `subject`, `message`, `status`, `created_at`, `created_by`) VALUES
('Adam', '2176436587', 'zadam@gmail.com', '應徵教師職缺', '希望應徵教師職位', 'OPEN', CURDATE(), 'DBA'),
('Zara', '3412654387', 'zarabaig@hotmail.com', '課程報名', '想要報名課程', 'OPEN', CURDATE(), 'DBA'),
('Marques', '8547643673', 'kmarques@yahoo.com', '課程意見', '想提供開發課程的意見', 'OPEN', CURDATE(), 'DBA'),
('Shyam', '4365328776', 'gshyam@gmail.com', '入學問題', '想進一步了解入學流程', 'OPEN', CURDATE(), 'DBA'),
('John', '5465765453', 'doejohn@gmail.com', '假期問題', '想詢問即將到來的假期安排', 'OPEN', CURDATE(), 'DBA'),
('Taniya Bell', '3987463827', 'belltaniya@gmail.com', '學生獎學金', '請問孩子可以申請獎學金嗎？', 'OPEN', CURDATE(), 'DBA'),
('Willie Lara', '4568764801', '476lara@gmail.com', '申請入學', '我的孩子想申請入學', 'OPEN', CURDATE(), 'DBA'),
('Jonathan Parsons', '4321768902', 'jonathan.parsons@gmail.com', '課程回饋', '音樂課程內容很不錯', 'OPEN', CURDATE(), 'DBA'),
('Cloe Rubio', '9854438719', 'rubio987@gmail.com', '更正出生日期', '孩子的出生日期需要更正', 'OPEN', CURDATE(), 'DBA'),
('Camilla Stein', '6545433254', 'camillas@gmail.com', '交通接送問題', '學校有提供交通接送嗎？', 'OPEN', CURDATE(), 'DBA'),
('Lizeth Gross', '4678783434', 'grossliz@yahoo.com', '學習進度報告', '請提供學習進度報告', 'OPEN', CURDATE(), 'DBA'),
('Yael Howe', '1243563254', 'howeyael@gmail.com', '證書問題', '需要紙本證書', 'OPEN', CURDATE(), 'DBA'),
('Ian Moreno', '2312231223', 'moreno.ian@gmail.com', '餐飲回饋', '餐點品質仍有改善空間', 'OPEN', CURDATE(), 'DBA'),
('Desirae Ibarra', '3445235667', 'ibarrades@gmail.com', '交通狀況反映', '學校周邊交通需要加強管制', 'OPEN', CURDATE(), 'DBA'),
('Oswaldo Jarvis', '4556121265', 'jarvissmile@hotmail.com', '校外教學', '想了解校外教學的詳細資訊', 'OPEN', CURDATE(), 'DBA'),
('Miah Perkins', '2367784512', 'perkinsmiah@hotmail.com', '疫苗接種協助', '想了解校內疫苗接種站資訊', 'OPEN', CURDATE(), 'DBA'),
('Zion Bolton', '8990678900', 'boltzion@gmail.com', '課程費用', '請提供音樂課程的費用資訊', 'OPEN', CURDATE(), 'DBA'),
('Dominik Tanner', '4556127834', 'tannerdominik@gmail.com', '活動時程', '請提供暑期活動時程', 'OPEN', CURDATE(), 'DBA');

-- roles
insert into `roles` (`role_name`, `created_at`, `created_by`)
VALUES
    ('ADMIN', CURDATE(), 'DBA'),
    ('STUDENT', CURDATE(), 'DBA');

-- plans
insert into `plan` (`name`, `created_at`, `created_by`) VALUES
('免費方案', CURDATE(), 'DBA'),
('基礎方案', CURDATE(), 'DBA'),
('標準方案', CURDATE(), 'DBA');

-- courses
insert into `courses` (`name`, `fees`, `created_at`, `created_by`)
VALUES
    ('Spring Boot 基礎', '1000', NOW(), 'DBA'),
    ('Java 進階', '1500', NOW(), 'DBA'),
    ('Thymeleaf 網頁開發', '1200', NOW(), 'DBA'),
    ('Hibernate 與 JPA 核心實務', '1300', NOW(), 'DBA'),
    ('RESTful API 設計', '1400', NOW(), 'DBA');

-- admin & student
insert into `person` (`name`, `email`, `mobile`, `password`, `role_id`, `created_at`, `created_by`)
VALUES
    ('管理員', 'admin@gmail.com', '3443434343', '$2a$10$XhU4UcSxDPb5G0I0fT/CZ.Lfj2VW2fkLkUP5cOEM.xM8EzyUQXaD2', 1, CURDATE(), 'DBA'),
    ('學生', 'student@gmail.com', '3443434343', '$2a$12$eRFpMHv.UUCq6lqII16TjuWUtJnSPTY0vWDr2t0gOtl2v2XvNb0Ii', 2, CURDATE(), 'DBA');

insert into `person` (`name`, `email`, `mobile`, `password`, `role_id`, `plan_id`, `created_at`, `created_by`)
VALUES
    ('Anthony', 'anthony@gmail.com', '0909963886', '$2a$12$EBrtxiTmAkBy9nLlKjeeJeYP.hAUSYhSHq0.3pegSWejyXk349Vq6', 2, 2, CURDATE(), 'DBA');

-- person_courses
insert into person_courses (person_id, course_id)
values (3, 3);
