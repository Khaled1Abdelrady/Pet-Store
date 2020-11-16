package data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;


public class PetProvider extends ContentProvider {

    PetDbHelper db;
    //create the UriMatcher object and the two variables to select the right option according to the URL.
    private static final UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
    private static final int Pets=100;
    private static final int Pets_ID=101;
    static {
        matcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,Pets);
        matcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",Pets_ID);
    }

    //create the database helper object
    @Override
    public boolean onCreate() {
        db=new PetDbHelper(getContext());
        return true;
    }

    //read the data from the database an show it.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase database = db.getReadableDatabase();

        Cursor cursor=null;

        int match = matcher.match(uri);

        switch (match) {
            case Pets:
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case Pets_ID:

                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //to update if their are any change had happened on the data
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    //insert a new pet
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = matcher.match(uri);

        switch (match) {
            case Pets:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //set the values for insert fun.
    private Uri insertPet(Uri uri, ContentValues values) {

        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // Check that the gender is valid
        Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        //get an instance of the database and check if the process done successfully or not.
        SQLiteDatabase database=db.getWritableDatabase();
        long ID= database.insert(PetContract.PetEntry.TABLE_NAME,null,values);
        if (ID==-1) {
            Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
            return null;
        }
        Toast.makeText(getContext(), "Pet saved", Toast.LENGTH_SHORT).show();
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, ID);
    }

    //to update one item or more.
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        //to specify if the URL for one item or not.
        final int match = matcher.match(uri);

        switch (match) {
            //update all items.
            case Pets:
                return updatePet(uri, contentValues, selection, selectionArgs);
            //update items with a specify ID.
            case Pets_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    //set the values for update fun.
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //check if their are no problems with the values.
        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }
        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }
        if (values.size() == 0) {
            return 0;
        }

        //get an instance of the database
       SQLiteDatabase database=db.getWritableDatabase();
       int NumberOfRows= database.update(PetContract.PetEntry.TABLE_NAME,values,selection,selectionArgs);
       getContext().getContentResolver().notifyChange(uri,null);
       return NumberOfRows;
    }

    //to delete one item or more.
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //get an instance of the database
        SQLiteDatabase database = db.getWritableDatabase();
        //var to know the #of the affected items(rows).
        int NumberOfItems;
        //to specify if the URL for one item or not.
        final int match = matcher.match(uri);

        switch (match) {
            //delete all items.
            case Pets:
                NumberOfItems=database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                 if(NumberOfItems!=0)
                     getContext().getContentResolver().notifyChange(uri,null);
                 return NumberOfItems;
            //delete items with a specify ID.
            case Pets_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                NumberOfItems=database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                if(NumberOfItems!=0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return NumberOfItems;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    //to know if the data is for one item or for a list.
    @Override
    public String getType(Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case Pets:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case Pets_ID
                    :
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}