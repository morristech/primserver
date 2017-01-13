package com.napster.primitive.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by napster on 05/01/17.
 */

public class Utils {

    public static Bitmap getScaledBitmapFromUri(Context context, Uri uri, int size) {
        Bitmap output = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream i1 = resolver.openInputStream(uri);
            InputStream i2 = resolver.openInputStream(uri);

            BitmapFactory.Options oOne = new BitmapFactory.Options();
            BitmapFactory.Options oTwo = new BitmapFactory.Options();
            oOne.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(i1, null, oOne);
            int scale = 1;
            while (oOne.outWidth / scale / 2 >= size && oOne.outHeight / scale / 2 >= size)
                scale *= 2;
            oTwo.inSampleSize = scale;
            Bitmap b = BitmapFactory.decodeStream(i2, null, oTwo);

            float height = b.getHeight();
            float width = b.getWidth();

            int targetHeight = (int) ((height / width) * size);
            Bitmap scaled = Bitmap.createScaledBitmap(b, size, targetHeight, true);

            String path = null;
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if(cursor != null){
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
            if(path == null)
                path = uri.getPath();

            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Matrix matrix = null;
            if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                        matrix.setScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.setRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                        matrix.setRotate(180);
                        matrix.postScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_TRANSPOSE:
                        matrix.setRotate(90);
                        matrix.postScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.setRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_TRANSVERSE:
                        matrix.setRotate(-90);
                        matrix.postScale(-1, 1);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.setRotate(-90);
                        break;
                }
            }

            if (matrix == null)
                output = scaled;
            else {
                Bitmap rotated = Bitmap.createBitmap(scaled, 0, 0, scaled.getWidth(), scaled.getHeight(), matrix, true);
                scaled.recycle();
                output = rotated;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public static File getCachedFile(Context context, Bitmap bitmap) {
        File imageFileName = null;
        File imageFileFolder = new File(context.getCacheDir(),"Shortlyst");
        if( !imageFileFolder.exists() ){
            imageFileFolder.mkdir();
        }
        FileOutputStream out = null;
        imageFileName = new File(imageFileFolder, "temp-" + System.currentTimeMillis() + ".jpg");

        try {
            out = new FileOutputStream(imageFileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
        } catch (Exception e) {
            imageFileName = null;
            Log.e("UTILS", "Failed to save image", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imageFileName;
    }

}
