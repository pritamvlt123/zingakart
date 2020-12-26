package com.example.zingakart.APIClient;

import com.example.zingakart.Model.LoginUserModel;
import com.example.zingakart.Model.OrderDetails;
import com.example.zingakart.Model.Orderadd;
import com.example.zingakart.Model.PinCodeModel;
import com.example.zingakart.Model.SelectedCategoryModel;
import com.example.zingakart.Model.SubCategoryModel;
import com.example.zingakart.Model.UserRegistration;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface User_Service {

    @Retry
    @GET("products/categories?")
    Call<List<SelectedCategoryModel>> getCategories
            (@Query("consumer_key") String consumer_key,
             @Query("consumer_secret")String consumer_secret,
             @Query("include")String include,
             @Query("per_page")String per_page);

    @Retry
    @GET("products/categories?")
    Call<List<SelectedCategoryModel>> getSecondLevelCat
            (@Query("consumer_key") String consumer_key,
             @Query("consumer_secret")String consumer_secret,
             @Query("parent")String parent);


    @Retry
    @GET("products?")
    Call<List<SubCategoryModel>>getSubCategory
            (@Query("consumer_key") String consumer_key,
             @Query("consumer_secret")String consumer_secret,
             @Query("category")String category);


    @Retry
    @FormUrlEncoded
    @POST("customers")
    Call<JsonObject>getUser
            (@Query("consumer_key") String consumer_key,
             @Query("consumer_secret")String consumer_secret,
             @Field("email") String email,
             @Field("password")String password,
             @Field("username")String username);


    @Retry
    @GET("customers")
    Call<List<LoginUserModel>>loginUser
            (@Query("consumer_key") String consumer_key,
             @Query("consumer_secret")String consumer_secret,
             @Query("email") String email,
             @Query("password")String password);

    @Retry
    @POST("api.php?")
    Call<PinCodeModel>checkPincode
            (@Query("consumer_key") String consumer_key,
             @Query("consumer_secret")String consumer_secret,
             @Query("pincode") String pincode);

    @Retry
    @POST("orders?")
    Call<Orderadd>createOrder
            (@Query("consumer_key") String consumer_key,
             @Query("consumer_secret")String consumer_secret,
             @Body String jsonObject);


    @Retry
    @GET("products/{p_id}/variations{v_id}?")
    Call<List<SubCategoryModel>>getVariations
            (@Query("consumer_key") String consumer_key,
             @Query("consumer_secret")String consumer_secret,
             @Query("category")String category,
             @Path("p_id")String id);

}
