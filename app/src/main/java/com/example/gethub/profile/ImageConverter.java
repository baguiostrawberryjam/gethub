// Make sure this package name matches where you created the file
package com.example.gethub.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * A utility class to convert Bitmaps to byte arrays and vice-versa.
 * This is needed to store images in databases or pass them in a Parcelable.
 */
public class ImageConverter {

    /**
     * Converts a Bitmap image into a byte array (byte[]).
     *
     * @param bitmap The Bitmap to convert.
     * @return A byte array representing the image, or null if the bitmap was null.
     */
    public static byte[] toByteArray(Bitmap bitmap) {
        if (bitmap == null) return null;

        // 1. Resize the bitmap to a manageable size (e.g., max 500x500 pixels)
        // This is crucial for passing data via Intent without crashing
        Bitmap resizedBitmap = getResizedBitmap(bitmap, 500);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // 2. Compress to JPEG (smaller than PNG) and reduce quality to 70%
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);

        return stream.toByteArray();
    }

    private static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * Converts a byte array (byte[]) back into a Bitmap image.
     *
     * @param byteArray The byte array to convert.
     * @return A Bitmap image, or null if the array was null or empty.
     */
    public static Bitmap toBitmap(byte[] byteArray) {
        // If the array is null or empty, return null
        if (byteArray == null || byteArray.length == 0) {
            return null;
        }

        // Decode the byte array into a Bitmap and return it
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}