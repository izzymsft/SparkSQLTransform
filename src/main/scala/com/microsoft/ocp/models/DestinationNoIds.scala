package com.microsoft.ocp.models

import java.sql.{Date, Timestamp}

case class DestinationNoIds(FirstName: String, LastName: String, WeightKilograms: BigDecimal, HeightCentimeters: BigDecimal,
                            DateOfBirth: Date, DateCreated: Timestamp)
