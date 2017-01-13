package com.napster.primitive.utils;

/**
 * Created by napster on 05/01/17.
 */

public interface Constants {
    String FCM_TOKEN = "fcm_token";
    String SERVICE_REQUEST = "service_request";

    int API_UPLOAD_FILE = 1;
    int API_QUEUE_FILE = 2;


    /**
     * Database Constants are declared below
     *
     *    Table Structure
     *    +-----------+----------------+-------------+--------------+
     *    | FILE_KEY  | PROCESSED 1/0  | ORG_LOC_URI | PROC_LOC_URI |
     *    +-----------+----------------+-------------+--------------+
     */

    String DB_NAME = "primdb";
    int DB_VERSION = 1;

    String TBL_SUBMISSIONS = "submissions";
    String COL_FILE_KEY = "filekey";
    String COL_PROCESSED = "processed";
    String COL_ORG_LOC_URI = "original_local_uri";
    String COL_PROC_LOC_URI = "processed_local_uri";

    String CREATE_TABLE_SUBMISSIONS = "CREATE TABLE "
            + TBL_SUBMISSIONS + " ( "
            + COL_FILE_KEY + " TEXT PRIMARY KEY, "
            + COL_PROCESSED + " INTEGER(1) DEFAULT 0, "
            + COL_ORG_LOC_URI + " TEXT, "
            + COL_PROC_LOC_URI + " TEXT )";

    String DROP_TABLE_SUBMISSIONS = "DROP TABLE IF EXISTS " + TBL_SUBMISSIONS;

    String SELECT_SUBMISSIONS = "SELECT * FROM " + TBL_SUBMISSIONS;
}
