package id.t12ue.kalimutu.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAY_Aiq_A:APA91bEiZHUe5EVR5GZzqQNhlKF9u05fCAlMsVIYRgEWPQ8QUNNerI1XCCV-k_6_G86mJcDdPMS15Wgr_stxxgyXREcLTdX9r0j4mJz3VqmZu8sDEFfsBY98frJis-ZIvvv4M04HAbnv" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

