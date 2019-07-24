package com.microsoft.ocp.utils

import java.sql.{Date, Timestamp}
import java.time.Instant

import com.microsoft.ocp.models.{DestinationNoIds, PatientRecordsDestination, PatientRecordsSource}

/**
  * Imperial2MetricsConverter
  *
  * This is used to convert imperial values in the PatientRecordsSource objects to metric units in the
  * PatientRecordsDestination class.
  *
  * It is used in the map() function transformations in the spark driver class
  *
  */
object Imperial2MetricsConverter {

  /**
    * Converts imperial to metric values
    *
    * @param src Source Medical Record
    * @return
    */
  def transform(src: PatientRecordsSource): PatientRecordsDestination = {

    val weightInKilograms = src.WeightPounds / 0.454; // convert the weight from pounds to kilos
    val heightInCentimeters = src.HeightInches * 2.54; // converts the height from inches to centimeters

    val destinationEMR = new PatientRecordsDestination(src.PatientId, src.FirstName, src.LastName,
      weightInKilograms, heightInCentimeters, Date.valueOf(src.DateOfBirth), Timestamp.valueOf(src.DateCreated));

    return destinationEMR;
  }

  /**
    * Removes the Row Identifier from the Destination Record
    *
    * @param origin
    * @return
    */
  def toDestinationWithoutIds(origin: PatientRecordsDestination): DestinationNoIds = {

    return new DestinationNoIds(origin.FirstName, origin.LastName,
      origin.WeightKilograms, origin.HeightCentimeters, origin.DateOfBirth, Timestamp.from(Instant.now))
  }
}
