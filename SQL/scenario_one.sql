-- SELECT * FROM Person WHERE personID = 2812;
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
INSERT INTO VaccineInStorage(vaccineInStorageID, vaccineID, storeID, stockLevel, creationDate, expirationDate) VALUES (2, 1, 3, 40, '2022-04-10', '2022-08-10');
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

INSERT INTO Location(locationID, longitude, latitude) VALUES (6, 53.388527, -1.471595);
INSERT INTO StorageLocation(storageLocationID, locationID) VALUES (5, 6);
INSERT INTO Store(storeID, storageLocationID, temperature, capacity) VALUES (6, 5, -5, 100);
INSERT INTO Van(vanID, deliveryStage, totalTime, remainingTime, originID, destinationID, storageLocationID, transporterLocationID) VALUES (2, 'waiting', 0, 0, 1, 1, 5, 1);

--  Create people
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1987-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1956-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1982-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1972-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1964-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2004-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2014-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1999-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1956-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1970-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1936-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1957-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1940-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1961-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1959-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1946-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2020-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1939-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1973-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1991-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1982-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1936-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1979-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1952-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2014-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1997-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1987-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2011-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2004-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2004-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1971-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1991-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2012-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1971-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1940-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1994-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1963-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1956-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1979-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2006-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1968-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1989-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1961-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1935-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1951-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2000-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1932-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1998-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1969-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1953-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2008-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2005-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1948-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1959-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1937-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1986-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1973-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1949-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2019-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1987-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1949-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1962-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1938-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1968-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1966-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1999-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2011-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1975-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1991-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2018-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1944-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1958-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2002-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1945-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1998-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1959-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1992-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1978-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1977-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1971-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1947-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1956-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1938-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1945-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1978-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2019-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1991-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2019-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1982-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2002-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1999-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1969-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1978-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1957-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2013-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2016-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1946-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '2013-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1956-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1969-01-01');
INSERT INTO Person (forename, surname, DoB) VALUES ('a', 'b', '1973-01-01');

-- INSERT INTO Person (forename, surname, DoB) VALUES ('Hanna', 'Davison', '1970-10-12');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Nicky', 'Benson', '2011-08-07');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Fred', 'Davies', '1998-10-12');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Karen', 'Hault', '2002-11-12');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('John', 'Bickerton', '2003-09-04');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Jack', 'Anfield', '1984-03-08');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Theo', 'Dayes', '1997-07-01');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Adam', 'Fletcher', '1963-11-10');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Richard', 'Whitehall', '1950-10-11');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Molly', 'Locke', '1980-03-21');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Rebecca', 'Richardson', '2001-11-12');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Sarah', 'Meadly', '1956-10-2');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Tom', 'Cameron', '1942-05-02');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Polly', 'Johnson', '1939-01-02');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Paige', 'Corbyn', '1950-10-12');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Jackie', 'Slater', '1971-08-02');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Tim', 'Bray', '1979-09-17');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Tracey', 'Murphy', '1999-10-04');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Emma', 'Mannign', '1984-02-06');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Beverly', 'West', '1988-11-11');
-- INSERT INTO Person (forename, surname, DoB) VALUES ('Frank', 'Sawyer', '2021-10-05');