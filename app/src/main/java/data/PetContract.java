package data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class PetContract {
    //create the URL to get the right database(tables).
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";

    //this class contain all the attributes you may need on this app(dealing with database).
   public abstract static class PetEntry implements BaseColumns{

       //these two attributes to know the types when using "getType" fun.
       public static final String CONTENT_LIST_TYPE =
               ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

       public static final String CONTENT_ITEM_TYPE =
               ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

       //columns names
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);
        public static String TABLE_NAME="Pets";
        public static String _ID=BaseColumns._ID;
        public static String COLUMN_PET_NAME="name";
        public static String COLUMN_PET_BREED="breed";
        public static String COLUMN_PET_GENDER="gender";
        public static String COLUMN_PET_WEIGHT="weight";

        //to deal with the gender Spinner
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

       public static boolean isValidGender(int gender) {
           if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
               return true;
           }
           return false;
       }
    }

}
