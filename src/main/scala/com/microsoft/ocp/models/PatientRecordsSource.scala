package com.microsoft.ocp.models

import java.sql.Date
import java.sql.Timestamp


/**
  * CREATE TABLE dbo.PatientRecordsSource           --- Table for Storing Patient Scores
*(
    *PatientId int IDENTITY (1,1) NOT NULL,      --- Primary Key for the Score
    *FirstName VARCHAR(40) NOT NULL,             --- First Name of the Patient
    *LastName VARCHAR(40) NOT NULL,              --- Last Name of the Patient
    *WeightPounds DECIMAL(5,2) NOT NULL,         --- The Last Weight of the Patient
    *HeightInches DECIMAL(5,2) NOT NULL,         --- The Last Height of the Patient
    *DateOfBirth date NOT NULL,                  --- The Patients DOB
    *DateCreated datetime2 NOT NULL              --- The timestamp for the record creation
*);
  */
case class PatientRecordsSource(PatientId: Int, FirstName: String, LastName: String,
                                WeightPounds: BigDecimal, HeightInches: BigDecimal,
                                DateOfBirth: String, DateCreated: String)
