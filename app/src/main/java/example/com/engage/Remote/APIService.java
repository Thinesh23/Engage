package example.com.engage.Remote;


import example.com.engage.Model.DataMessage;
import example.com.engage.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAArkAUmYs:APA91bEdYGi47vo7u2eqri6plpjAsCeBp0MHnOoQewoYXrGFrl3c3DHWxNRab8nUwnlyY-fn89v0cVbEjWvyJqrrxFlhh-t1REXVO_QoUrAWbI94xIrkg1lZNLh-5TLhVO6pZ2qpqAyQ"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);

}
