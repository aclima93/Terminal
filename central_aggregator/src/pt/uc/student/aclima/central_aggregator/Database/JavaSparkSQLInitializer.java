
package pt.uc.student.aclima.central_aggregator.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import java.io.Serializable;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.AnalysisException;

import static org.apache.spark.sql.functions.col;

import pt.uc.student.aclima.central_aggregator.Database.Entries.PeriodicMeasurement;

/**
 * Created by aclima on 14/03/2017.
 */

public class JavaSparkSQLInitializer {

    private static final String APP_NAME = "Central Aggregator";
    private static final String testFile = "CPU_data_example.json.json";

    public static void main(String[] args) throws AnalysisException {
        // $example on:init_session$
        SparkSession spark = SparkSession
                .builder()
                .appName(APP_NAME)
                .config("spark.some.config.option", "some-value")
                .getOrCreate();
        // $example off:init_session$

        runBasicDataFrameExample(spark);
        runDatasetCreationExample(spark);

        spark.stop();
    }

    private static void runBasicDataFrameExample(SparkSession spark) throws AnalysisException {

        // read json data into dataframe
        Dataset<Row> df = spark.read().json(testFile);

        // Displays the content of the DataFrame to stdout
        df.show();

        /*
        // Print the schema in a tree format
        df.printSchema();

        // Register the DataFrame as a SQL temporary view
        df.createOrReplaceTempView("people");

        Dataset<Row> sqlDF = spark.sql("SELECT * FROM people");
        sqlDF.show();

        // Register the DataFrame as a global temporary view
        df.createGlobalTempView("people");

        // Global temporary view is tied to a system preserved database `global_temp`
        spark.sql("SELECT * FROM global_temp.people").show();
        */

    }

    private static void runDatasetCreationExample(SparkSession spark) {

        // Encoders are created for Java beans
        Encoder<PeriodicMeasurement> encoder = Encoders.bean(PeriodicMeasurement.class);

        // DataFrames can be converted to a Dataset by providing a class. Mapping based on name
        Dataset<PeriodicMeasurement> peopleDS = spark.read().json(testFile).as(encoder);
        peopleDS.show();
    }

}