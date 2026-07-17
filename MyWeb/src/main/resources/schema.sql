--spring boot auto loads schema.sql & data.sql to h2db

-- news
create TABLE IF NOT EXISTS `news` (
    `news_id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `released_date` DATE NOT NULL,
    `content` TEXT,
    `created_at` TIMESTAMP NOT NULL,
    `created_by` VARCHAR(100) NOT NULL,
    `updated_at` TIMESTAMP DEFAULT NULL,
    `updated_by` VARCHAR(100) DEFAULT NULL
);

-- contact_msg
create TABLE IF NOT EXISTS `contact_msg` (
  `contact_id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `mobile` VARCHAR(10) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `subject` VARCHAR(100) NOT NULL,
  `message` VARCHAR(500) NOT NULL,
  `status` VARCHAR(10) NOT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `created_by` VARCHAR(50) NOT NULL,
  `updated_at` TIMESTAMP DEFAULT NULL,
  `updated_by` VARCHAR(50) DEFAULT NULL
);

-- roles
create TABLE IF NOT EXISTS `roles` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50) NOT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `created_by` VARCHAR(50) NOT NULL,
  `updated_at` TIMESTAMP DEFAULT NULL,
  `updated_by` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
);

-- address
create TABLE IF NOT EXISTS `address` (
  `address_id` INT NOT NULL AUTO_INCREMENT,
  `address1` VARCHAR(200) NOT NULL,
  `address2` VARCHAR(200) DEFAULT NULL,
  `city` VARCHAR(50) NOT NULL,
  `zip_code` INT NOT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `created_by` VARCHAR(50) NOT NULL,
  `updated_at` TIMESTAMP DEFAULT NULL,
  `updated_by` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`address_id`)
);

-- plan
create TABLE IF NOT EXISTS `plan` (
  `plan_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `created_by` VARCHAR(50) NOT NULL,
  `updated_at` TIMESTAMP DEFAULT NULL,
  `updated_by` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`plan_id`)
);

-- courses
create TABLE IF NOT EXISTS `courses` (
  `course_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `fees` varchar(10) NOT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `created_by` varchar(50) NOT NULL,
  `updated_at` TIMESTAMP DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
   PRIMARY KEY (`course_id`)
);

-- person
create TABLE IF NOT EXISTS `person` (
  `person_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `mobile` VARCHAR(20) NOT NULL,
  `password` VARCHAR(200) NOT NULL,
  `role_id` INT NOT NULL,
  `address_id` INT NULL,
  `plan_id` INT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `created_by` VARCHAR(50) NOT NULL,
  `updated_at` TIMESTAMP DEFAULT NULL,
  `updated_by` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`person_id`),
  CONSTRAINT `unique_email` UNIQUE (`email`),
  FOREIGN KEY (`role_id`) REFERENCES `roles`(`role_id`),
  FOREIGN KEY (`address_id`) REFERENCES `address`(`address_id`),
  FOREIGN KEY (`plan_id`) REFERENCES `plan`(`plan_id`)
);

-- person_courses
create TABLE IF NOT EXISTS `person_courses` (
  `person_id` int NOT NULL,
  `course_id` int NOT NULL,
  FOREIGN KEY (person_id) REFERENCES person(person_id),
  FOREIGN KEY (course_id) REFERENCES courses(course_id),
   PRIMARY KEY (`person_id`,`course_id`)
-- student can not enroll the same course multiple times
);