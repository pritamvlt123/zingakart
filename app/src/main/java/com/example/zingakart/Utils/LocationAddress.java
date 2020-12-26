package com.example.zingakart.Utils;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.zingakart.Helper.CustomRequest;
import com.example.zingakart.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2/15/2016.
 */
public class LocationAddress {
    public static String lat, lng, Address0 = "", Address1 = "", Address2 = "", City = "", State = "", locality = "", Country = "", PIN = "", AddressFo = "";
    Context mContext;

    public LocationAddress(Context context) {
        this.mContext = context;
    }

    public void getAddress(final double latitude, final double longitude) {
        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true";
        CustomRequest customRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {
                        try {
                            if (jsonObj != null) {
                                String Status = jsonObj.getString("status");
                                if (Status.equalsIgnoreCase("OK")) {
                                    JSONArray Results = jsonObj.getJSONArray("results");
                                    JSONObject zero = Results.getJSONObject(0);
                                    JSONArray address_components = zero.getJSONArray("address_components");
                                    JSONObject geometry = zero.getJSONObject("geometry");
                                    JSONObject location = geometry.getJSONObject("location");
                                    lat = location.getString("lat");
                                    lng = location.getString("lng");
                                    String formatted_address = zero.getString("formatted_address");

                                    for (int i = 0; i < address_components.length(); i++) {
                                        JSONObject zero2 = address_components.getJSONObject(i);
                                        String long_name = zero2.getString("long_name");
                                        JSONArray mtypes = zero2.getJSONArray("types");
                                        for (int j = 0; j < mtypes.length(); j++) {
                                            String Type = mtypes.getString(j);
                                            if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
                                                if (Type.equals("street_number")) {
                                                    Address0 = long_name + ",";
                                                } else if (Type.equalsIgnoreCase("route")) {
                                                    Address0 = Address1 + long_name;
                                                } else if (Type.equalsIgnoreCase("sublocality_level_1")) {
                                                    AddressFo = long_name;
                                                } else if (Type.equalsIgnoreCase("locality")) {
                                                    City = long_name;
                                                } else if (Type.equalsIgnoreCase("country")) {
                                                    Country = long_name;
                                                } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                                                    State = long_name;
                                                } else if (Type.equalsIgnoreCase("sublocality_level_2")) {
                                                    locality = long_name;
                                                } else if (Type.equalsIgnoreCase("postal_code")) {
                                                    PIN = long_name;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(customRequest);
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getCity() {
        return City;
    }

    public String getState() {
        return State;
    }

    public String getLocality() {
        return locality;
    }

    public String getCounty() {
        return Country;
    }

    public String getPIN() {
        return PIN;

    }

    public String getAddFo() {
        return AddressFo;

    }


}

