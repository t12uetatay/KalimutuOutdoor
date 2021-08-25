package id.t12ue.kalimutu.helpers;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class Cons {

    public static String KEY_USER="USERS";
    public static String KEY_TRANSACTION="TRANSACTIONS";
    public static String KEY_BANK="BANKS";
    public static String KEY_PACKAGE="PACKAGES";
    public static String KEY_PRODUCT="PRODUCTS";
    public static String KEY_NOTIFICATION="NOTIFICATIONS";
    public static String KEY_DETAIL="DETAILS";
    public static String KEY_CART="CARTS";
    public static String KEY_TOKEN="TOKENS";
    public static String KEY_IMAGE="IMAGES";
    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCBNM";

    public static String status(int sts){
        String status = null;
        if (sts==-2){
            status="Bukti pembayaran invalid";
        } else if (sts==-1){
            status="Pesanan dibatalkan";
        } else if (sts==0){
            status="Pesanan sedang disiapkan";
        } else if (sts==1){
            status="Menunggu verifikasi pemabayaran";
        } else if (sts==2){
            status="Pembayaran telah diverifikasi";
        } else if (sts==3){
            status="Pesanan telah diambil";
        } else if (sts==4){
            status="Pesanan telah dikembalikan";
        }

        return status;
    }


    public static String dateFormat(String dt){
        String s=null;
        if (!dt.equals("-")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatID = new SimpleDateFormat("dd-MMM-yyyy");
            TimeZone tz = TimeZone.getTimeZone("Asia/Jakarta");
            formatID.setTimeZone(tz);
            try {
                s = formatID.format(sdf.parse(dt));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            s="-";
        }

        return s;
    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }


    public static String currencyId(double nominal){
        Locale localID = new Locale("in", "ID");
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(localID);

        return rupiah.format(nominal);
    }

    public static String getRandom() {
        int sizeOfRandomString=10;
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString()+".jpg";
    }

    /*public static List<Credit> creditList(){
        List<Credit> list = new ArrayList<>();
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.vivekkaushik.datepicker:datePickerTimeline:0.0.4", "https://github.com/101Loop/DatePickerTimeline"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "io.reactivex.rxjava2:rxjava:2.2.12", "https://github.com/ReactiveX/RxJava"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.tbruyelle.rxpermissions2:rxpermissions:0.9.5@aar", "https://github.com/tbruyelle/RxPermissions"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.zhihu.android:matisse:0.5.3-beta3", "https://github.com/zhihu/Matisse"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.github.bumptech.glide:glide:4.11.0", "https://github.com/bumptech/glide"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.github.bumptech.glide:compiler:4.11.0", "https://github.com/bumptech/glide"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.theartofdev.edmodo:android-image-cropper:2.8.0", "https://github.com/ArthurHub/Android-Image-Cropper"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "de.hdodenhof:circleimageview:3.1.0", "https://github.com/hdodenhof/CircleImageView"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.squareup.retrofit2:retrofit:2.3.0", "https://github.com/square/retrofit"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.squareup.retrofit2:converter-gson:2.3.0", "https://github.com/square/retrofit"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.schibstedspain.android:leku:8.0.0", "https://github.com/AdevintaSpain/Leku"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.google.zxing:core:3.3.0", "https://github.com/zxing/zxing"));
        list.add(new Credit(true, R.drawable.ic_barcode_scanner, "com.journeyapps:zxing-android-embedded:4.2.0", "https://github.com/journeyapps/zxing-android-embedded"));
        list.add(new Credit(false, R.drawable.ic_barcode_scanner, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_bell_active, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_bell_inactive, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_book_active, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_book_inactive, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_clock, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_credit_card, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_dashboard_active, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_dashboard_inactive, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_home_active, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_home_inactive, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_plus, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_remove, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_search, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_soccer_field, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_star, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_user_active, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.ic_user_inactive, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.photo, "www.flaticon.com", "https://www.flaticon.com/"));
        list.add(new Credit(false, R.drawable.user, "www.flaticon.com", "https://www.flaticon.com/"));
        return list;
    }*/


}
