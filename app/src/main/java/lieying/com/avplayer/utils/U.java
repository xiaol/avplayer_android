package lieying.com.avplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class U {
    public static final String MESSAGE_RECEIVED_ACTION = "com.bu.yuyan.MESSAGE_RECEIVED_ACTION";
    public static Boolean ExploreFirstAppear = true;
    public final static int CAMERA = 1;
    public final static int PHOTO_ALBUM = 2;
    public final static int PHOTO_REQUEST_CUT = 3;
    public final static int PHOTO_SAVE = 4;
    public final static int NO = 1000;
    public final static int COMMIT = 1001;
    public final static int EDIT = 1002;
    public final static int LOCATION = 1003;
    public final static int MEDIA_MAX_DURATION = 100;
    public final static int MEDIA_CURRENT_DURATION = 101;
    public final static int MEDIA_FINSHED = 102;
    public final static int SHOW_PROGRESS = 103;
    public final static int RECORDER_STOP = 104;
    public final static int TIME_DECREASE = 105;
    public final static String STOP_OTHERS = "com.bu.yuyan.stopother";
    public final static int STATUS_IS_LOADING = 800;
    public final static int STATUS_IS_SUCCEEDED = 801;
    public final static int STATUS_IS_FAILED = 802;

    /**
     * @param handler
     * @param what
     * @param key
     * @param value
     */
    public static void sendMessage(Handler handler, int what, String key,
                                   String value) {
        Bundle buddle = new Bundle();
        buddle.putString(key, value);
        Message msg = new Message();
        msg.what = what;
        msg.setData(buddle);
        handler.sendMessage(msg);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String getPackageName() {
        return "com.bu.yuyan";
    }


    public static TranslateAnimation TranslateView(final View argView,
                                                   final float pX1, final float pX2, final float pY1, final float pY2,
                                                   long duration, long delay) {

        int left = argView.getLeft() + (int) pX2;
        int top = argView.getTop() + (int) pY2;
        int width = argView.getWidth();
        int height = argView.getHeight();
        argView.layout(left, top, left + width, top + height);
        TranslateAnimation animation = new TranslateAnimation(pX1 - pX2, pX1,
                pY1 - pY2, pY1);
        animation.setDuration((long) duration);
        animation.setStartOffset((long) delay);

        return animation;
    }

    public static Float getDesignScale(Context argContext) {
        SharedPreferences mySharedPreferences = argContext
                .getSharedPreferences("screen", Context.MODE_PRIVATE);
        int iHeight = mySharedPreferences.getInt("height", 0);
        float scale = 0;
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) argContext).getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
        float density = metric.densityDpi;
        if (density < 160) {
            scale = iHeight / 600.0f;
        }
        if (density >= 160 && density < 240) {
            scale = iHeight / 800.0f;
        }
        if (density >= 240) {
            scale = iHeight / 1080.0f;
        }
//		Log.i("tag", density + "density");
        return scale;
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = 1;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, float width, float height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    public static Bitmap GetProperBitmap(Context argContext, int argResId) {
        // We first get the raw bitmap with the original pixel size
        Bitmap rawBitmap = readBitMap(argContext, argResId);
        // Then we calculate the scaled bitmap pixel size according to the
        // device resolution
        float fWidth = rawBitmap.getWidth() * getDesignScale(argContext);
        float fHeight = rawBitmap.getHeight() * getDesignScale(argContext);
        Bitmap finalBitmap = zoomBitmap(rawBitmap, fWidth, fHeight);
        return finalBitmap;
    }


//    public static String ToSBC(String input) {
//        // 半角转全角：
//        char[] c = input.toCharArray();
//        for (int i = 0; i < c.length; i++) {
//            if (c[i] == 32) {
//                c[i] = (char) 12288;
//                continue;
//            }
//            if (c[i] < 127)
//                c[i] = (char) (c[i] + 65248);
//        }
//        return new String(c);
//    }

    /**
     * 全角转换为半角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static int setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        return result;
    }

    public static void ShowToast(final Context argContext, final String argText) {

        Handler mainHandler = new Handler(argContext.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(argContext, argText, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }; // This is your code
        mainHandler.post(myRunnable);
    }

    /**
     * 高斯模糊
     *
     * @param bmp
     * @return
     */
    public static Drawable BoxBlurFilter(Bitmap bmp) {
        /** 水平方向模糊度 */
        float hRadius = 10;
        /** 竖直方向模糊度 */
        float vRadius = 10;
        /** 模糊迭代度 */
        int iterations = 7;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }
//        blurFractional(inPixels, outPixels, width, height, hRadius);
//        blurFractional(outPixels, inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    public static void blur(int[] in, int[] out, int width, int height, float radius) {
        int widthMinus1 = width - 1;
        int r = (int) radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];
        for (int i = 0; i < 256 * tableSize; i++) {
            divide[i] = i / tableSize;
        }
        int inIndex = 0;
        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int i = -r; i <= r; i++) {
                int rgb = in[inIndex + clamp(i, 0,
                        width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }
            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];
                int i1 = x + r + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];
                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    public static void blurFractional(int[] in, int[] out, int width, int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;
        for (int y = 0; y < height; y++) {
            int outIndex = y;
            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];
                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }

    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }


}
