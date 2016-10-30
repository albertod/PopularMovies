package albertodimartino.com.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import albertodimartino.com.popularmovies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold movies.
        final String SQL_CREATE_FAVORITE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.MOVIE_PROVIDER_ID+ " TEXT UNIQUE NOT NULL, " +
                MovieEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.RELEASE_DATE + " TEXT , " +
                MovieEntry.OVERVIEW + " TEXT, " +
                MovieEntry.POSTER_PATH + " TEXT, " +
                MovieEntry.VOTE_AVERAGE + " REAL " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXIST " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
