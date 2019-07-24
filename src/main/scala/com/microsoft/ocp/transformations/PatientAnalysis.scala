package com.microsoft.ocp.transformations

import java.util.Properties

import com.microsoft.ocp.models.{DestinationNoIds, PatientRecordsDestination, PatientRecordsSource}
import com.microsoft.ocp.utils.Imperial2MetricsConverter
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

// to compile scala code to uber jar, run the following command:
// mvn clean install
// to run uber jar locally with spark submit, do the following command:
// spark-submit --class com.microsoft.ocp.transformations.PatientAnalysis --master local[1] target/PatientTransformation-1.0.0-uber.jar
object PatientAnalysis {

  def main(args: Array[String]): Unit = {

    val maxRowsToDisplay: Int = 128

    val sparkConfig: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .set("spark.driver.memory", "4g")
      .setAppName("PatientAnalysis")

    val spark: SparkSession = SparkSession.builder()
      .config(sparkConfig)
      .getOrCreate()


    val destinationTable = "dbo.PatientRecordsDestination"
    val sourceTable: String = "dbo.PatientRecordsSource" // if we want all the records in the table
    // if we want just a subset of records from the table
    val jdbcPushDownQuery: String = "(SELECT * FROM dbo.PatientRecordsSource WHERE PatientId != 0) recentPatients".format(sourceTable)
    val jdbcURL: String = "jdbc:sqlserver://servername.database.windows.net:1433"
    val username: String = "username goes here"
    val password: String = "password goes here"
    val database: String = "tablename"

    // A list of all the options are available here
    // https://docs.microsoft.com/en-us/sql/connect/jdbc/setting-the-connection-properties?view=sql-server-2017
    val jdbcConnectionProps = new Properties()
    jdbcConnectionProps.put("user", username)
    jdbcConnectionProps.put("password",password)
    jdbcConnectionProps.put("database", database)
    jdbcConnectionProps.put("encrypt", "true")
    jdbcConnectionProps.put("trustServerCertificate", "false")
    jdbcConnectionProps.put("hostNameInCertificate", "*.database.windows.net")
    jdbcConnectionProps.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver") // please copy JDBC Driver jars to $SPARK_HOME/jars for local testing
    jdbcConnectionProps.put("loginTimeout", "30")

    // For additional examples similar to this one, please check this link
    // https://spark.apache.org/docs/latest/sql-data-sources-jdbc.html
    // https://docs.databricks.com/spark/latest/data-sources/sql-databases.html
    // This loads the
    val patientsDataframe: DataFrame = spark.read
      .jdbc(url = jdbcURL, table = jdbcPushDownQuery, properties = jdbcConnectionProps)


    // This is necessary so as to find encoders for types stored in a Dataset objects.
    // Primitive types (Int, String, etc) and Product types (case classes) are supported by importing spark.implicits._ from the spark session object
    import spark.implicits._

    // this creates a dataset with all the records from the query
    val sourceDataset: Dataset[PatientRecordsSource] = patientsDataframe.as[PatientRecordsSource]

    // Display the original records
    sourceDataset.show(maxRowsToDisplay)

    // If necessary, you can limit the number of records you want to work with with the Dataset.filter() operation using a lambda function
    val filteredSources: Dataset[PatientRecordsSource] = sourceDataset.filter(records => records.PatientId > 1); // get everyone except the first patient

    // Display the filtered records retrieved
    filteredSources.show(maxRowsToDisplay)

    // in this step we are going to convert the records from a dataset of PatientRecordsSource to PatientRecordsDestination objects
    // using the Dataset.map() transformation operation
    val destinationRecords: Dataset[PatientRecordsDestination] = filteredSources
      .map(patientRecord => Imperial2MetricsConverter.transform(patientRecord))

    // Display the records with ids
    destinationRecords.show(maxRowsToDisplay)

    // remove the ids before the insert
    val destinationRecordsWithoutIds: Dataset[DestinationNoIds] = destinationRecords.map(origin => Imperial2MetricsConverter.toDestinationWithoutIds(origin))

    // Display the records without the ids
    destinationRecordsWithoutIds.show(maxRowsToDisplay)

    // Save the records from the final data set to the database
    // when saving a DataFrame to a data source, if data/table already exists,
    // contents of the DataFrame are expected to be appended to existing data
    destinationRecordsWithoutIds.write.mode(SaveMode.Append)
      .jdbc(url = jdbcURL, table = destinationTable, connectionProperties = jdbcConnectionProps)

    // Stops the underlying spark content
    spark.stop()

  }
}
