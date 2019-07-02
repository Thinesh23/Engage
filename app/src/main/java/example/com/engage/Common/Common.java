package example.com.engage.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;

import example.com.engage.Model.Event;
import example.com.engage.Model.User;
import example.com.engage.Remote.APIService;
import example.com.engage.Remote.GoogleRetrofitClient;
import example.com.engage.Remote.IGoogleService;
import example.com.engage.Remote.RetrofitClient;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Common {
    public static User currentUser;

    public static String PHONE_TEXT = "userPhone";

    public static final String INTENT_EVENT_ID = "eventId";
    public static final String INTENT_USER_ID = "userId";

    public static String restaurantSelected = "";

    private static final String BASE_URL = "https://fcm.googleapis.com";
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com";

    public static APIService getFCMService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGoogleService getGoogleMapAPI() {
        return GoogleRetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static BigDecimal formatCurrency(String amount, Locale locale) throws java.text.ParseException{
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        if (format instanceof DecimalFormat)
            ((DecimalFormat) format).setParseBigDecimal(true);
        return (BigDecimal)format.parse(amount.replace("[^\\d.,]", ""));
    }
}
