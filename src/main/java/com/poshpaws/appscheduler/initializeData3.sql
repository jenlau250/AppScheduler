START TRANSACTION;

INSERT INTO barber (barberId, barberName, barberPhone, barberEmail, notes, active, hireDate, createDate, createdBy, lastUpdate, lastUpdateBy) 
	#VALUES (NULL, 'Marvin', '415-123-1234', 'email@email.com', 'Good with poodles', 1, '2020-01-04 10:06:25', CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test'
    VALUES (NULL, 'Sam', '415-123-1234', 'email@email.com', 'Barber note', 1, '2020-01-04 10:06:25', CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test'
);
SET @barber_id = LAST_INSERT_ID();

INSERT INTO customer (customerId, customerName, customerPhone, customerEmail, notes, active, createDate, createdBy, lastUpdate, lastUpdateBy)
     #VALUES (NULL, 'Jenny', '415-423-1234', 'jenny@gmail.com', 'customer note1', 1, CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');
     VALUES (NULL, 'Leslie', '415-888-1234', 'email@gmail.com', 'customer note', 1, CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');
SET @customer_id = LAST_INSERT_ID();

INSERT INTO pet (petId, customerId, petName, petType, petDescription, active, createDate, createdBy, lastUpdate, lastUpdateBy)  # generate ID by inserting NULL
	#VALUES (NULL, @customer_id, 'Bailey', 'Kitten', 'Maine Coon', 1, CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test'
    VALUES (NULL, @customer_id, 'Finn', 'Dog', 'Poodle', 1, CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test'
    );
SET @pet_id = LAST_INSERT_ID();

INSERT INTO appointment (appointmentId, title, start, end, description, type, createDate, createdBy, lastUpdate, lastUpdateBy, customerId, petId, barberId)
     VALUES (NULL, 'Standard Appt', '2020-02-11 12:00:00', '2020-01-11 12:15:00', 'Description', 'Standard Service', CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test',
     @customer_id, @pet_id, @barber_id);   # use ID in second table
SET @appointment_id = LAST_INSERT_ID();
     
COMMIT;

