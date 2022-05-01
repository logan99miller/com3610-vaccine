-- DROP DATABASE vaccine_system;
CREATE DATABASE vaccine_system;
CREATE TABLE Location(
	locationID int NOT NULL AUTO_INCREMENT,
    latitude decimal(8,6),
    longitude decimal(8,6),
    PRIMARY KEY (locationID));
CREATE TABLE OpeningTime(
	openingTimeID int NOT NULL AUTO_INCREMENT,
    locationID int NOT NULL,
    `day` varchar(255) NOT NULL,
    startTime time NOT NULL,
    endTime time NOT NULL,
    PRIMARY KEY (openingTimeID),
    FOREIGN KEY (locationID) REFERENCES Location(locationID) ON DELETE CASCADE);
CREATE TABLE StorageLocation(
	storageLocationID int NOT NULL AUTO_INCREMENT,
    locationID int NOT NULL,
    PRIMARY KEY (storageLocationID),
    FOREIGN KEY (locationID) REFERENCES Location(locationID) ON DELETE CASCADE);
CREATE TABLE DistributionCentre(
	distributionCentreID int NOT NULL AUTO_INCREMENT,
    storageLocationID int NOT NULL,
    PRIMARY KEY (distributionCentreID),
    FOREIGN KEY (storageLocationID) REFERENCES StorageLocation(storageLocationID) ON DELETE CASCADE);
CREATE TABLE VaccinationCentre(
	vaccinationCentreID int NOT NULL AUTO_INCREMENT,
    storageLocationID int NOT NULL,
    `name` varchar(255),
    vaccinesPerHour int NOT NULL,
    PRIMARY KEY (vaccinationCentreID),
    FOREIGN KEY (storageLocationID) REFERENCES StorageLocation(storageLocationID) ON DELETE CASCADE);
CREATE TABLE Transporter(
	transporterID int NOT NULL AUTO_INCREMENT,
    `name` varchar(255),
    PRIMARY KEY (transporterID));
CREATE TABLE TransporterLocation(
	transporterLocationID int NOT NULL AUTO_INCREMENT,
    locationID int NOT NULL,
    transporterID int NOT NULL,
    PRIMARY KEY (transporterLocationID),
    FOREIGN KEY (locationID) REFERENCES Location(locationID) ON DELETE CASCADE,
    FOREIGN KEY (transporterID) REFERENCES Transporter(transporterID) ON DELETE CASCADE);
CREATE TABLE Store(
	storeID int NOT NULL AUTO_INCREMENT,
    storageLocationID int NOT NULL,
    temperature int NOT NULL,
    capacity int NOT NULL,
    PRIMARY KEY (storeID),
    FOREIGN KEY (storageLocationID) REFERENCES StorageLocation(storageLocationID) ON DELETE CASCADE);
CREATE TABLE Vaccine(
	vaccineID int NOT NULL AUTO_INCREMENT,
    `name` varchar(255),
    dosesNeeded int NOT NULL,
    daysBetweenDoses int NOT NUll,
    minimumAge int NOT NULL,
    maximumAge int NOT NULL,
    PRIMARY KEY (vaccineID));
CREATE TABLE VaccineInStorage(
	vaccineInStorageID int NOT NULL AUTO_INCREMENT,
	vaccineID int NOT NULL,
    storeID int NOT NULL,
    stockLevel int NOT NULL,
    creationDate datetime NOT NULL,
    expirationDate datetime NOT NULL,
    PRIMARY KEY (vaccineInStorageID),
    FOREIGN KEY (vaccineID) REFERENCES Vaccine(vaccineID) ON DELETE CASCADE,
    FOREIGN KEY (storeID) REFERENCES Store(storeID) ON DELETE CASCADE);
CREATE TABLE Van(
	vanID int NOT NULL AUTO_INCREMENT,
    deliveryStage varchar(255) NOT NULL,
    totalTime int NOT NULL,
    remainingTime int NOT NULL,
    storageLocationID int NOT NULL,
    originID int,
    destinationID int,
    transporterLocationID int NOT NULL,
    PRIMARY KEY (vanID),
    FOREIGN KEY (storageLocationID) REFERENCES StorageLocation(storageLocationID) ON DELETE CASCADE,
    FOREIGN KEY (originID) REFERENCES Location(locationID) ON DELETE CASCADE,
    FOREIGN KEY (destinationID) REFERENCES Location(locationID) ON DELETE CASCADE,
    FOREIGN KEY (transporterLocationID) REFERENCES TransporterLocation(transporterLocationID) ON DELETE CASCADE);
CREATE TABLE Manufacturer(
	manufacturerID int NOT NULL AUTO_INCREMENT,
    `name` varchar(255),
    vaccineID int NOT NULL,
    PRIMARY KEY (manufacturerID),
    FOREIGN KEY (vaccineID) REFERENCES Vaccine(vaccineID) ON DELETE CASCADE);
CREATE TABLE Factory(
	factoryID int NOT NULL AUTO_INCREMENT,
    storageLocationID int NOT NULL,
    manufacturerID int NOT NULL,
    vaccinesPerMin int NOT NULL,
    PRIMARY KEY (factoryID),
    FOREIGN KEY (storageLocationID) REFERENCES StorageLocation(storageLocationID) ON DELETE CASCADE,
    FOREIGN KEY (manufacturerID) REFERENCES Manufacturer(manufacturerID) ON DELETE CASCADE);
CREATE TABLE VaccineLifespan(
	vaccineLifespanID int NOT NULL AUTO_INCREMENT,
    vaccineID int NOT NULL,
    lifespan int NOT NULL,
    lowestTemperature int NOT NULL,
    highestTemperature int NOT NULL,
    PRIMARY KEY (vaccineLifespanID),
	FOREIGN KEY (vaccineID) REFERENCES Vaccine(vaccineID) ON DELETE CASCADE);
CREATE TABLE MedicalCondition(
	medicalConditionID int NOT NULL AUTO_INCREMENT,
    `name` varchar(255),
    vulnerabilityLevel int NOT NULL,
    PRIMARY KEY (medicalConditionID));
CREATE TABLE Person(
	personID int NOT NULL AUTO_INCREMENT,
    forename varchar(255),
    surname varchar(255),
    DoB date NOT NULL,
    PRIMARY KEY (personID));
CREATE TABLE Booking(
	bookingID int NOT NULL AUTO_INCREMENT,
    personID int NOT NULL,
    vaccinationCentreID int NOT NULL,
    `date` datetime NOT NULL,
	PRIMARY KEY (bookingID),
    FOREIGN KEY (personID) REFERENCES Person(personID) ON DELETE CASCADE,
    FOREIGN KEY (vaccinationCentreID) REFERENCES VaccinationCentre(vaccinationCentreID) ON DELETE CASCADE);
CREATE TABLE PersonMedicalCondition(
	personMedicalConditionID int NOT NULL AUTO_INCREMENT,
	personID int NOT NULL,
    medicalConditionID int NOT NULL,
    PRIMARY KEY (personMedicalConditionID),
	FOREIGN KEY (personID) REFERENCES Person(personID) ON DELETE CASCADE,
    FOREIGN KEY (medicalConditionID) REFERENCES MedicalCondition(medicalConditionID) ON DELETE CASCADE);
CREATE TABLE VaccineExemption(
	vaccineID int NOT NULL,
    medicalConditionID int NOT NULL,
    PRIMARY KEY (vaccineID, medicalConditionID),
    FOREIGN KEY (vaccineID) REFERENCES Vaccine(vaccineID) ON DELETE CASCADE,
	FOREIGN KEY (medicalConditionID) REFERENCES MedicalCondition(medicalConditionID) ON DELETE CASCADE);
CREATE TABLE VaccineReceived(
	vaccineReceivedID int NOT NULL AUTO_INCREMENT,
    personID int NOT NULL,
    vaccineID int NOT NULL,
    `date` datetime NOT NULL,
    PRIMARY KEY (vaccineReceivedID),
	FOREIGN KEY (personID) REFERENCES Person(personID) ON DELETE CASCADE,
	FOREIGN KEY (vaccineID) REFERENCES Vaccine(vaccineID) ON DELETE CASCADE);
CREATE TABLE Simulation(
	simulationID int NOT NULL AUTO_INCREMENT,
	actualBookingRate decimal(4,4) NOT NULL,
    actualAttendanceRate decimal(4,4) NOT NULL,
    predictedVaccinationRate decimal(4,4) NOT NULL,
    PRIMARY KEY (simulationID));