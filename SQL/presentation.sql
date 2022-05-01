SET GLOBAL max_connections = 100000;

-- Clear DB
DELETE FROM Location;
DELETE FROM OpeningTime;
DELETE FROM StorageLocation;
DELETE FROM DistributionCentre;
DELETE FROM VaccinationCentre;
DELETE FROM Transporter;
DELETE FROM TransporterLocation;
DELETE FROM Store;
DELETE FROM Vaccine;
DELETE FROM VaccineInStorage;
DELETE FROM Van;
DELETE FROM Manufacturer;
DELETE FROM Factory;
DELETE FROM VaccineLifespan;
DELETE FROM MedicalCondition;
DELETE FROM Person;
DELETE FROM Booking;
DELETE FROM PersonMedicalCondition;
DELETE FROM VaccineExemption;
DELETE FROM VaccineReceived;
DELETE FROM Simulation;

-- Simulaton values
INSERT INTO Simulation(simulationID, actualBookingRate, actualAttendanceRate, predictedVaccinationRate) VALUES (1, 0.6, 0.9, 0.8);

-- Create vaccines
INSERT INTO Vaccine(vaccineID, `name`, dosesNeeded, daysBetweenDoses, minimumAge, maximumAge) VALUES (1, 'Moderna', 1, 1, 18, 100);
INSERT INTO VaccineLifespan(vaccineLifespanID, vaccineID, lifespan, lowestTemperature, highestTemperature) VALUES (1, 1, 50, -10, 0);

INSERT INTO Vaccine(vaccineID, `name`, dosesNeeded, daysBetweenDoses, minimumAge, maximumAge) VALUES (2, 'Pfizer', 1, 3, 90, 90);
INSERT INTO VaccineLifespan(vaccineLifespanID, vaccineID, lifespan, lowestTemperature, highestTemperature) VALUES (2, 1, 100, -6, 0);
INSERT INTO VaccineLifespan(vaccineLifespanID, vaccineID, lifespan, lowestTemperature, highestTemperature) VALUES (3, 1, 30, 0, 2);

INSERT INTO Vaccine(vaccineID, `name`, dosesNeeded, daysBetweenDoses, minimumAge, maximumAge) VALUES (3, 'Astrazeneca', 2, 100, 16, 100);
INSERT INTO VaccineLifespan(vaccineLifespanID, vaccineID, lifespan, lowestTemperature, highestTemperature) VALUES (4, 1, 20, -1, 1);
INSERT INTO VaccineLifespan(vaccineLifespanID, vaccineID, lifespan, lowestTemperature, highestTemperature) VALUES (5, 1, 10, -2, 4);

-- Create factories
INSERT INTO Location(locationID, longitude, latitude) VALUES (1, 53.386178, -1.497288);
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (1, 1, 'monday', '8:00', '16:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (2, 1, 'tuesday', '8:00', '16:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (3, 1, 'wednesday', '8:00', '16:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (4, 1, 'thursday', '8:00', '16:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (5, 1, 'friday', '8:00', '13:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (6, 1, 'saturday', '8:00', '8:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (7, 1, 'sunday', '8:00', '8:00'); 
INSERT INTO StorageLocation(storageLocationID, locationID) VALUES (1, 1);
INSERT INTO Store(storeID, storageLocationID, temperature, capacity) VALUES (1, 1, -5, 1000);
INSERT INTO Manufacturer(manufacturerID, `name`, vaccineID) VALUES (1, 'All Star Productions', 1);
INSERT INTO Factory(factoryID, storageLocationID, manufacturerID, vaccinesPerMin) VALUES (1, 1, 1, 15);

-- Create VCs
INSERT INTO Location(locationID, longitude, latitude) VALUES (2, 53.376467, -1.467414);
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (15, 2, 'monday', '9:00', '13:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (16, 2, 'tuesday', '9:00', '13:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (17, 2, 'wednesday', '9:00', '13:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (18, 2, 'thursday', '9:00', '13:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (19, 2, 'friday', '9:00', '13:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (20, 2, 'saturday', '0:00', '0:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (21, 2, 'sunday', '0:00', '0:00'); 
INSERT INTO StorageLocation(storageLocationID, locationID) VALUES (2, 2);
INSERT INTO Store(storeID, storageLocationID, temperature, capacity) VALUES (2, 2, -5, 500);
INSERT INTO VaccineInStorage(vaccineInStorageID, vaccineID, storeID, stockLevel, creationDate, expirationDate) VALUES (1, 1, 2, 40, '2022-04-10', '2022-08-10');
INSERT INTO VaccinationCentre(vaccinationCentreID, storageLocationID, `name`, vaccinesPerHour) VALUES (1, 2, "Chemist Co", 8);

-- Create DCs
INSERT INTO Location(locationID, longitude, latitude) VALUES (3, 53.360244, -1.558230);
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (22, 3, 'monday', '9:00', '17:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (23, 3, 'tuesday', '9:00', '17:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (24, 3, 'wednesday', '9:00', '17:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (25, 3, 'thursday', '9:00', '17:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (26, 3, 'friday', '9:00', '17:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (27, 3, 'saturday', '0:00', '0:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (28, 3, 'sunday', '0:00', '0:00'); 
INSERT INTO StorageLocation(storageLocationID, locationID) VALUES (3, 3);
INSERT INTO Store(storeID, storageLocationID, temperature, capacity) VALUES (3, 3, -5, 2000);
INSERT INTO DistributionCentre(distributionCentreID, storageLocationID) VALUES (1, 3);

-- Create TLs & vans
INSERT INTO Location(locationID, longitude, latitude) VALUES (4, 53.388527, -1.471595);
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (8, 4, 'monday', '9:00', '17:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (9, 4, 'tuesday', '9:00', '17:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (10, 4, 'wednesday', '9:00', '17:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (11, 4, 'thursday', '9:00', '17:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (12, 4, 'friday', '9:00', '17:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (13, 4, 'saturday', '0:00', '0:00'); 
INSERT INTO OpeningTime(openingTimeID, locationID, `day`, startTime, endTime) VALUES (14, 4, 'sunday', '0:00', '0:00');
INSERT INTO Transporter(transporterID, `name`) VALUES (1, 'Speedy Deliveries');
INSERT INTO TransporterLocation(transporterLocationID, locationID, transporterID) VALUES (1, 4, 1);

INSERT INTO Location(locationID, longitude, latitude) VALUES (5, 53.388527, -1.471595);
INSERT INTO StorageLocation(storageLocationID, locationID) VALUES (4, 5);
INSERT INTO Store(storeID, storageLocationID, temperature, capacity) VALUES (4, 4, -5, 100);
INSERT INTO Van(vanID, deliveryStage, totalTime, remainingTime, originID, destinationID, storageLocationID, transporterLocationID) VALUES (1, 'waiting', 0, 0, 1, 1, 4, 1);

--  Create people
INSERT INTO Person (personID, forename, surname, DoB) VALUES (1, 'Geordie', 'Trafford', '1992-6-01');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (2, 'Zavia', 'Adele', '1978-7-02');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (3, 'Sarina', 'Fredric', '1977-8-04');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (4, 'Eula', 'Faithe', '1971-9-06');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (5 ,'Lavender', 'Merit', '1947-10-08');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (6, 'Ely', 'Darcey', '1956-11-10');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (7, 'Lynsay', 'Dora', '1938-12-12');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (8, 'Iona', 'Barclay', '1945-1-14');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (9, 'Denis', 'Page', '1978-2-16');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (10, 'Kynaston', 'Astrid', '2019-3-18');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (11, 'Magdalena', 'Bridger', '1991-4-20');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (12, 'Alycia', 'Jo-Anne', '2019-5-22');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (13, 'Humbert', 'Percy', '1982-6-24');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (14, 'Marilynn', 'Royston', '2002-7-26');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (15, 'Bertie', 'Delmar', '1999-8-28');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (16, 'Alea', 'Leanna', '1969-9-30');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (17, 'Rearden', 'Alban', '1978-10-03');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (18, 'Lisa', 'Walt', '1957-11-06');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (19, 'Greyson', 'Rowen', '2013-12-09');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (20, 'Merlyn', 'Larrie', '2016-01-12');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (21, 'Mick', 'Dwain', '1946-02-15');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (22, 'Nikki', 'Rowley', '2013-03-18');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (23, 'Charlie', 'Audie', '1956-04-21');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (24, 'Ember', 'Fraser', '1969-05-24');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (25, 'Magdalena', 'Deemer', '1973-06-27');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (26, 'Hanna', 'Davison', '1970-10-12');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (27, 'Nicky', 'Benson', '2011-08-07');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (28, 'Fred', 'Davies', '1998-10-12');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (29, 'Karen', 'Hault', '2002-11-12');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (30, 'John', 'Bickerton', '2003-09-04');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (31, 'Jack', 'Anfield', '1984-03-08');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (32, 'Theo', 'Dayes', '1997-07-01');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (33, 'Adam', 'Fletcher', '1963-11-10');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (34, 'Richard', 'Whitehall', '1950-10-11');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (35, 'Molly', 'Locke', '1980-03-21');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (36, 'Rebecca', 'Richardson', '2001-11-12');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (37, 'Sarah', 'Meadly', '1956-10-2');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (38, 'Tom', 'Cameron', '1942-05-02');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (39, 'Polly', 'Johnson', '1939-01-02');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (40, 'Paige', 'Corbyn', '1950-10-12');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (41, 'Jackie', 'Slater', '1971-08-02');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (42, 'Tim', 'Bray', '1979-09-17');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (43, 'Tracey', 'Murphy', '1999-10-04');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (44, 'Emma', 'Mannign', '1984-02-06');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (45, 'Beverly', 'West', '1988-11-11');
INSERT INTO Person (personID, forename, surname, DoB) VALUES (46, 'Frank', 'Sawyer', '2021-10-05');

INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (1, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (2, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (3, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (4, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (5, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (6, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (7, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (8, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (9, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (10, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (11, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (12, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (13, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (14, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (15, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (16, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (17, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (18, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (19, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (20, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (21, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (22, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (23, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (24, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (25, 1, '2022-04-20');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (26, 1, '2022-04-21');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (27, 1, '2022-04-21');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (28, 1, '2022-04-21');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (29, 1, '2022-04-21');
INSERT VaccineReceived(personID, vaccineID, `date`) VALUES (30, 1, '2022-04-21');

INSERT Booking(vaccineID, personID, `date`) VALUES (1, 31, '2022-05-23');
INSERT Booking(vaccineID, personID, `date`) VALUES (1, 32, '2022-05-23');
INSERT Booking(vaccineID, personID, `date`) VALUES (1, 33, '2022-05-23');
INSERT Booking(vaccineID, personID, `date`) VALUES (1, 34, '2022-05-23');
INSERT Booking(vaccineID, personID, `date`) VALUES (1, 35, '2022-05-24');
INSERT Booking(vaccineID, personID, `date`) VALUES (1, 36, '2022-05-24');
INSERT Booking(vaccineID, personID, `date`) VALUES (1, 37, '2022-05-24');
INSERT Booking(vaccineID, personID, `date`) VALUES (1, 38, '2022-05-24');
INSERT Booking(vaccineID, personID, `date`) VALUES (1, 39, '2022-05-24');
INSERT Booking(vaccineID, personID, `date`) VALUES (1, 40, '2022-05-24');
INSERT Booking(vaccineID, personID, `date`) VALUES (1, 31, '2022-05-24');
