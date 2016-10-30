package albertodimartino.com.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    // Codes for UriMatcher
    static final int FAVORITE_MOVIE = 100;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case FAVORITE_MOVIE:{
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.MovieEntry.TABLE_NAME,
                                projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITE_MOVIE:{
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                    break;
                }
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
            }
            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArguments) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIE:{
                int rowDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArguments);
                if (rowDeleted != 0 || selection != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return rowDeleted;
                } else
                    throw new android.database.SQLException("Failed to delete row: " + uri);
            }
            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIE:{
                int rowUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
                if (rowUpdated != 0 || whereClause != null) {
                    return rowUpdated;
                } else {
                    throw new android.database.SQLException("Failed to update row: " + uri);
                }
            }
            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }
    }

    // Helpers
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_FAVORITE_MOVIE, FAVORITE_MOVIE);
        return matcher;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
