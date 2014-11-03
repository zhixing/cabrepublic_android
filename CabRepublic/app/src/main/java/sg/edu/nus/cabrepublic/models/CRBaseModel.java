package sg.edu.nus.cabrepublic.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.JsonObject;

import sg.edu.nus.cabrepublic.utilities.CRDataManager;


/**
 * Created by zhixing on 14.11.04.
 */
public class CRBaseModel implements Serializable {
    public int id;

    public static int findModelIndex(ArrayList<CRBaseModel> models, int targetId) {
        int count = models.size();
        for (int i = 0; i < count; i++) {
            if (models.get(i).id == targetId) {
                return i;
            }
        }
        return -1;
    }

    protected int getIntFromJson(JsonObject object, String name) {
        if (object.get(name) != null) {
            return object.get(name).getAsInt();
        } else {
            return CRDataManager.EMPTY_INT;
        }
    }

    protected String getStringFromJson(JsonObject object, String name) {
        if (object.get(name) != null && !object.get(name).getAsString().equalsIgnoreCase("")) {
            return object.get(name).getAsString();
        } else {
            return CRDataManager.EMPTY_STRING;
        }
    }

    protected boolean getBooleanFromJson(JsonObject object, String name) {
        if (object.get(name) != null) {
            return object.get(name).getAsBoolean();
        } else {
            return false;
        }
    }
}
