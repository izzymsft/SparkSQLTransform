INSERT INTO dbo.PatientRecordsSource 
(FirstName, LastName, WeightPounds, HeightInches, DateOfBirth, DateCreated) 
VALUES
('Israel', 'Ekpo', 220, 68, '1981-06-28', CURRENT_TIMESTAMP),
('Joshua', 'Gabriel', 85, 60, '2012-01-31', CURRENT_TIMESTAMP),
('Angel', 'David', 335, 50, '2000-12-25', CURRENT_TIMESTAMP),
('Jacob', 'Sampson', 225, 55, '1999-12-31', CURRENT_TIMESTAMP),
('Uduak', 'Moses', 128, 49, '1949-09-15', CURRENT_TIMESTAMP);


INSERT INTO dbo.PatientRecordsDestination 
(FirstName, LastName, WeightKilograms, HeightCentimeters, DateOfBirth, DateCreated) 
VALUES
('Israel', 'Ekpo', 100, 68, '1981-06-28', CURRENT_TIMESTAMP),
('Uduak', 'Moses', 200, 125, '1949-09-15', CURRENT_TIMESTAMP);


--- SELECT * FROM dbo.PatientRecordsSource;

--- SELECT * FROM dbo.PatientRecordsDestination;
