LOCK TABLES `barber` WRITE;
/*!40000 ALTER TABLE `barber` DISABLE KEYS */;
INSERT INTO `barber` VALUES 
	(1,'Marvin Adams','415-123-1234','marvin@email.com','Good with poodles','1','2009-03-01 00:00:00','2020-01-27 01:28:10','test','2020-02-13 18:05:42','test'),
    (2,'Sam Lee','415-823-4823','sam@email.com','','1','2009-03-01 00:00:00','2020-01-27 01:31:03','test','2020-02-13 18:05:42','test'),
    (3,'Harry Fong','415-912-2938','harry@email.com','','1','2015-02-04 00:00:00','2020-01-27 01:33:09','test','2020-02-13 18:05:42','test'),
    (4,'Joe Lawson','902-923-1238','joe@email.com','On leave through 4/30/20','0','2017-01-26 00:00:00','2020-02-03 19:05:51','test','2020-02-13 18:05:41','test'),
    (5,'Ashley Kutcher','723-293-2938','ashley@email.com',NULL,'1','2019-01-01 00:00:00','2020-02-13 18:04:08',NULL,'2020-02-13 18:05:41',NULL);
/*!40000 ALTER TABLE `barber` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES 
	(1,'Jenny Tao','415-423-1234','jenny@gmail.com','',1,'2020-01-27 01:28:10','test','2020-02-10 02:13:43','test'),
    (2,'Leslie Smith','415-888-1234','leslie@gmail.com','Prefer to schedule with Marvin',1,'2020-01-27 01:31:03','test','2020-02-13 18:07:11','test'),
    (3,'Brad Pitt','415-888-1234','brad@gmail.com','',1,'2020-01-27 01:33:09','test','2020-02-13 18:07:11','test'),
    (4,'Mary Daly','415-923-2939','mary@gmail.com',NULL,1,'2020-01-27 04:23:04','test','2020-02-13 18:07:10','test'),
    (5,'Steve Durkin','415-123-1234','stevie@gmail.com','',1,'2020-01-27 03:35:04','test','2020-02-13 18:07:11','test');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `pet` WRITE;
/*!40000 ALTER TABLE `pet` DISABLE KEYS */;
INSERT INTO `pet` VALUES 
	(1,'Bailey','Kitten','Maine Coon','2020-01-27 01:28:10','test','2020-01-31 06:06:41','test',1),
    (2,'Finn','Dog','Poodle','2020-01-27 01:31:03','test','2020-01-27 01:31:03','test',2),
    (3,'Madison','Dog','Bernase Mountain Dog','2020-01-27 01:33:10','test','2020-01-30 04:41:58','test',3),
    (4,'Jack','Dog','Large mix breed','2020-01-27 01:36:35','test','2020-02-10 07:08:53','test',2),
    (5,'Jones','Cat','Maine Coone','2020-01-30 04:42:22','test','2020-02-13 18:08:21','test',4),
    (6,'Britt','Dog','Extremely treat motivated','2020-02-10 00:16:52','test','2020-02-10 00:16:52','test',5),
    (7,'Bags','Dog','Scared of salad spinners','2020-02-10 00:17:21','test','2020-02-10 00:17:21','test',5);
/*!40000 ALTER TABLE `pet` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES user
	(12345,'admin','pwadmin',1,'owner','2019-05-24 00:00:00','2019-05-24 07:38:06','admin2'),
    (23455,'test','test',1,'owner','2019-05-23 00:00:00','2019-05-26 03:43:27','admin'),
    (53455,'user','pwuser',1,'owner','2019-04-23 00:00:00','2020-02-13 03:43:45','admin');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `appointment` WRITE;
/*!40000 ALTER TABLE `appointment` DISABLE KEYS */;
INSERT INTO `appointment` VALUES 
	(1,'2020-01-11 15:00:00','2020-01-11 18:00:00',NULL,'Bath & Haircut','Client asked for Marvin',NULL,NULL,'2020-01-27 01:28:11','test','2020-02-13 17:58:45','test',1,1,1),
	(2,'2020-02-11 22:45:00','2020-02-11 23:00:00',NULL,'Bath & Haircut','Description',NULL,NULL,'2020-01-27 01:31:04','test','2020-02-13 17:58:45','test',2,1,2),
    (3,'2020-01-24 15:00:00','2020-01-24 15:30:00',NULL,'Brush','Description',NULL,NULL,'2020-01-27 01:33:10','test','2020-02-13 18:12:59','user',5,3,30),
    (4,'2020-01-15 15:00:00','2020-01-15 18:00:00',NULL,'Bath & Haircut',NULL,NULL,NULL,'2020-02-13 18:10:11','test','2020-02-13 18:10:54','test',4,3,16),
    (5,'2020-01-01 23:30:00','2020-01-02 00:15:00',NULL,'Daycare','',NULL,NULL,'2020-01-31 08:08:15','test','2020-02-13 18:10:54','test',2,2,4);
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
UNLOCK TABLES;
