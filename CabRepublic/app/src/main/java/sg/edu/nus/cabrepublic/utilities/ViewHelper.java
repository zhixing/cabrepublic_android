package sg.edu.nus.cabrepublic.utilities;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import sg.edu.nus.cabrepublic.LoginActivity;
import sg.edu.nus.cabrepublic.R;

/**
 * Created by zhixing on 14.11.04.
 */
public class ViewHelper {
    private static ViewHelper sharedHelper;

    private ViewHelper() {

    }

    public static ViewHelper getInstance() {
        if (sharedHelper == null) {
            sharedHelper = new ViewHelper();
        }
        return sharedHelper;
    }

    public void toastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public void handleInvalidAccessToken(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        CRDataManager.getInstance().logout(context);
        intent.putExtra("RELOGIN", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.umeng_fb_slide_in_from_left, R.anim.umeng_fb_slide_out_from_right);
        ViewHelper.getInstance().toastMessage(context, "You might have logged in in other device before. Please login again");
    }

    public void handleRequestFailure(Context context, int errCode, String errReason) {
        switch (errCode) {
            case CRDataManager.NETWORK_FAILURE: {
                toastMessage(context, "Sorry, network failure. Please try again later.");
                break;
            }

            case CRDataManager.REASONED_ERROR: {
                toastMessage(context, errReason);
                break;
            }

            case CRDataManager.UNAUTHORIZED: {
                toastMessage(context, "You're not authorized. Please re-login.");
                handleInvalidAccessToken(context);
                break;
            }

            case CRDataManager.NOT_FOUND: {
                toastMessage(context, "The request url is not found.");
                break;
            }

            case CRDataManager.INTERNAL_ERROR: {
                toastMessage(context, "Oops.. the server encounters an error");
                break;
            }

            case CRDataManager.UNKNOWN: {
                toastMessage(context, "Server is under maintenance. Please try again later.");
                break;
            }
        }
    }
}
