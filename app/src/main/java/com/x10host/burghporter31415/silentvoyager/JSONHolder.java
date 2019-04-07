package com.x10host.burghporter31415.silentvoyager;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHolder {

    private final String arrayHolder = "names";
    private JSONObject object;

    public JSONHolder(JSONObject object) {
        this.object = object;
    }

    public String[] getAssocArray() throws JSONException {
        return this.object.getString(arrayHolder).split("\n");
    }

    public int getLastId() throws JSONException {
        return this.object.getString("last_id").isEmpty() ? -1 :
                Integer.parseInt(this.object.getString("last_id"));
    }

}
