package com.example.hasanzian.inventoryapp.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hasanzian.inventoryapp.R;
import com.example.hasanzian.inventoryapp.activity.MainActivity;
import com.example.hasanzian.inventoryapp.data.InventoryContract;
import com.example.hasanzian.inventoryapp.helper.InventoryDbHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;

import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_IMAGE_LOCATION;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRICE;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_QUANTITY;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.CONTENT_URI;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.TABLE_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry._ID;

/**
 * Utils contains helper methods
 */
public class Utils {
    private static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * Helper method for Products Table
     *
     * @param mContext get the calling context
     * @param product  product name as string
     * @param price    price as string
     * @param quantity quantity as string
     * @param supplier supplier name
     * @param phone    ic_phone of supplier
     * @param filePath file path
     * @return Uri of newly inserted item
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Uri insertProducts(Context mContext, String product, String price, String quantity, String supplier, String phone, Uri filePath, boolean isDemo) {
        //get param as values and set to corresponding column
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, product);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, Integer.parseInt(price));
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, Integer.parseInt(quantity));
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, supplier);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, phone);

        if (isDemo) {
            String destinationFilePath = filePath.toString();
            values.put(COLUMN_IMAGE_LOCATION, destinationFilePath);
        } else {
            String destinationFilePath = Utils.saveFileToAppFolderUsingLocation(mContext, Utils.getPath(mContext, filePath), isDemo).getAbsolutePath();
            values.put(COLUMN_IMAGE_LOCATION, destinationFilePath);
        }

        //insert new value via content resolver
        Uri newRowID = mContext.getContentResolver().insert(CONTENT_URI, values);
        // Error
        if (newRowID == null) {
            Toast.makeText(mContext, R.string.error_in_data_insertion_str, Toast.LENGTH_SHORT).show();
            return null;
        } else {
            // when insert is successful
            Toast.makeText(mContext, mContext.getString(R.string.data_insertion_sucessful_str), Toast.LENGTH_SHORT).show();
            return newRowID;
        }
    }

    /**
     * update current item with new value
     *
     * @param mContext get the calling context
     * @param product  product name as string
     * @param price    price as string
     * @param quantity quantity as string
     * @param supplier supplier name
     * @param phone    ic_phone of supplier
     * @param filePath
     * @return number of row affected
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static int updateProducts(Uri currentUri, Context mContext, String product, String price, String quantity, String supplier, String phone, Uri filePath, boolean isDemo) {
        //get param as values and set to corresponding column
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, product);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, Integer.parseInt(price));
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, Integer.parseInt(quantity));
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, supplier);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, phone);
        String destinationFilePath = Utils.saveFileToAppFolderUsingLocation(mContext, Utils.getPath(mContext, filePath), isDemo).getAbsolutePath();
        values.put(COLUMN_IMAGE_LOCATION, destinationFilePath);

        //insert new value via content resolver
        int rowsAffected = mContext.getContentResolver().update(currentUri, values, null, null);
        // Error
        if (rowsAffected == 0) {
            //updated fail
            Toast.makeText(mContext, mContext.getString(R.string.editor_update_item_failed), Toast.LENGTH_SHORT).show();
        } else
            //updated successful
            Toast.makeText(mContext, R.string.editor_update_item_successful, Toast.LENGTH_SHORT).show();
        return rowsAffected;

    }


    /**
     * method to check edit text box is empty or not
     *
     * @param etText takes edit text
     * @return true if edit text is left empty else return false
     */
    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    /**
     * greenCheck take current context
     * and returns a drawable
     *
     * @param mContext of current activity
     * @return a green drawable  with check
     */
    public static Drawable greenCheck(Context mContext) {
        Drawable myIcon = mContext.getResources().getDrawable(R.drawable.ic_check_circle);
        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
        return myIcon;
    }


    /**
     * use for sale button
     * decrease current quantity to 1
     *
     * @param currentUri      for current item uri
     * @param context         current context
     * @param currentQuantity current quantity of item
     */
    public static void quantityUpdate(Uri currentUri, Context context, int currentQuantity) {
        // checking for zero or negative
        if (currentQuantity > 0) {
            currentQuantity -= 1;
            // -1 current quantity and add new value to db
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, currentQuantity);

            //insert new value via content resolver
            int rowsAffected = context.getContentResolver().update(currentUri, values, null, null);
            // Error
            if (rowsAffected == 0) {
                //updated fail
                Toast.makeText(context, context.getString(R.string.sale_update_item_failed), Toast.LENGTH_SHORT).show();
            } else
                //updated successful
                Toast.makeText(context, R.string.sale_update_item_successful, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, context.getString(R.string.editor_update_zero_item_failed), Toast.LENGTH_SHORT).show();
        }


    }

    public static Cursor readItem(Context context) {
        InventoryDbHelper dbHelper = new InventoryDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String projection[] = {_ID, COLUMN_PRODUCT_NAME, COLUMN_PRICE, COLUMN_QUANTITY, COLUMN_SUPPLIER_NAME, COLUMN_SUPPLIER_PHONE_NUMBER, COLUMN_IMAGE_LOCATION

        };
        return db.query(TABLE_NAME, projection, null, null, null, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            Log.v(LOG_TAG, "isDocumentUri");
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                Log.v(LOG_TAG, "isExternalStorageDocumentUri");
                final String docId = DocumentsContract.getDocumentId(uri);
                Log.v(LOG_TAG, "Doc id: " + docId);
                final String[] split = docId.split(":");
                final String type = split[0];
                Log.v(LOG_TAG, "type: " + type);

                if ("primary".equalsIgnoreCase(type) || "053E-15EF".equalsIgnoreCase(type)) {
                    Log.v(LOG_TAG, Environment.getExternalStorageState() + "/" + split[1]);
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static File saveFileToAppFolderUsingLocation(Context context, String fileLocation, boolean isDemoMode) {

        if (isDemoMode) {
            return null;
        } else {
            Long currentTime = Calendar.getInstance().getTimeInMillis();
            File destinationImage;
            File selectedImage = new File(fileLocation);
            File filesDir = context.getFilesDir();
            String destinationImageName = "/" + currentTime.toString() + ".png";
            destinationImage = new File(filesDir, destinationImageName);
            Log.v(LOG_TAG, "Selected image path: " + fileLocation);
            Log.v(LOG_TAG, "destination image path: " + destinationImage.toString());
            if (selectedImage.exists()) {
                FileChannel src = null;
                FileChannel dst = null;
                try {
                    src = new FileInputStream(selectedImage).getChannel();
                    dst = new FileOutputStream(destinationImage).getChannel();
                } catch (FileNotFoundException e) {
                    Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                try {
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return destinationImage;
        }


    }

    public static File getFileFromLocation(String location) {
        if (new File(location).exists()) {
            return new File(location);
        }
        return null;
    }


    public static Bitmap decodeSampledBitmapFromFile(File imageFile, int requestedHeight, int requestedWidth) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        options.inJustDecodeBounds = false;
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > requestedHeight || width > requestedWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= requestedHeight && (halfWidth / inSampleSize) >= requestedWidth) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inPurgeable = true;
        bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        return bitmap;
    }


    public static void deleteAllOldFiles(Context context) {
        File dir = new File(context.getFilesDir().getPath());
        Log.d("dir", "" + dir.getAbsolutePath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                new File(dir, aChildren).delete();
            }
        }

    }

    public static String createDemoPic(MainActivity activity) {
        // save demo picture in data folder
        Bitmap bm;
        bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.warehouse);

        // saving to path /data/user/0/com.example.hasanzian.inventoryapp/files/... .png
        String dir = activity.getFilesDir().getPath();
        Log.d("dir", "" + dir);

        Long currentTime = Calendar.getInstance().getTimeInMillis();
        String destinationImageName = "/" + currentTime.toString() + ".png";


        //creating new file with name "demo.png"
        File file = new File(dir, destinationImageName);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        try {
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStream.close();
            Log.d("demo", "" + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
