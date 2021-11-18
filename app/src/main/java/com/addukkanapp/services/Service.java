package com.addukkanapp.services;

import com.addukkanapp.models.ALLOrderDataModel;
import com.addukkanapp.models.ALLProductDataModel;
import com.addukkanapp.models.AddOrderModel;
import com.addukkanapp.models.AdminMessageDataModel;
import com.addukkanapp.models.AttrDataModel;
import com.addukkanapp.models.BrandDataModel;
import com.addukkanapp.models.AddCartDataModel;
import com.addukkanapp.models.CartDataModel;
import com.addukkanapp.models.CompanyDataModel;
import com.addukkanapp.models.CountryDataModel;
import com.addukkanapp.models.CouponDataModel;
import com.addukkanapp.models.CreateRoomModel;
import com.addukkanapp.models.DoctorsDataModel;
import com.addukkanapp.models.FavouriteProductDataModel;
import com.addukkanapp.models.FilterModel;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.models.MessageDataModel;
import com.addukkanapp.models.NotificationCountModel;
import com.addukkanapp.models.NotificationDataModel;
import com.addukkanapp.models.PlaceGeocodeData;
import com.addukkanapp.models.PlaceMapDetailsData;
import com.addukkanapp.models.ProductDataModel;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.RoomDataModel;
import com.addukkanapp.models.SettingModel;
import com.addukkanapp.models.SingleAdminMessageDataModel;
import com.addukkanapp.models.SingleMessageDataModel;
import com.addukkanapp.models.SingleOrderModel;
import com.addukkanapp.models.SliderDataModel;
import com.addukkanapp.models.SpecialDataModel;
import com.addukkanapp.models.StatusResponse;
import com.addukkanapp.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);


    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone,
                          @Field("password") String password);


    @FormUrlEncoded
    @POST("api/client-register")
    Call<UserModel> signUp(@Field("name") String name,
                           @Field("phone_code") String phone_code,
                           @Field("phone") String phone,
                           @Field("password") String password,
                           @Field("software_type") String software_type,
                           @Field("country_code") String country_code

    );

    @FormUrlEncoded
    @POST("api/update-client-profile")
    Call<UserModel> updateProfile(@Header("Authorization") String bearer_token,
                                  @Field("user_id") int user_id,
                                  @Field("name") String name,
                                  @Field("phone_code") String phone_code,
                                  @Field("phone") String phone,
                                  @Field("password") String password,
                                  @Field("software_type") String software_type,
                                  @Field("country_code") String country_code
    );

    @GET("api/setting-country")
    Call<CountryDataModel> getCountries(@Header("lang") String lang);


    @FormUrlEncoded
    @POST("api/update-firebase")
    Call<ResponseModel> updateFirebaseToken(@Header("Authorization") String token,
                                            @Field("user_id") int user_id,
                                            @Field("phone_token") String phone_token,
                                            @Field("software_type") String software_type

    );

    @FormUrlEncoded
    @POST("api/logout")
    Call<ResponseModel> logout(@Header("Authorization") String token,
                               @Field("user_id") int user_id,
                               @Field("phone_token") String phone_token,
                               @Field("software_type") String software_type

    );

    @GET("api/slider")
    Call<SliderDataModel> get_slider(
            @Header("lang") String lang,
            @Query("type") String type,
            @Query("country_code") String country_code);

    @GET("api/main-departments")
    Call<MainCategoryDataModel> get_category(
            @Header("lang") String lang

    );

    @GET("api/main-department-sub-dep-product")
    Call<MainCategoryDataModel> get_categorySubProduct(
            @Header("lang") String lang,
            @Query("user_id") String user_id,
            @Query("country_code") String country_code

    );

    @GET("api/recently-arrived")
    Call<ALLProductDataModel> getRecentlyArrived(
            @Header("lang") String lang,
            @Query("user_id") String user_id,
            @Query("country_code") String country_code,
            @Query("pagination") String pagination

    );

    @GET("api/best-seller")
    Call<ALLProductDataModel> getMostSell(
            @Header("lang") String lang,
            @Query("user_id") String user_id,
            @Query("country_code") String country_code,
            @Query("pagination") String pagination

    );

    @GET("api/side-menu")
    Call<MainCategoryDataModel> getSideMenu(
            @Header("lang") String lang

    );

    @GET("api/offers")
    Call<ALLProductDataModel> getOffers(
            @Header("lang") String lang,
            @Query("user_id") String user_id,
            @Query("country_code") String country_code,
            @Query("pagination") String pagination

    );

    @GET("api/my-favorites")
    Call<FavouriteProductDataModel> getFavoutite(
            @Header("Authorization") String Authorization,
            @Header("lang") String lang,
            @Query("user_id") String user_id,
            @Query("country_code") String country_code,
            @Query("pagination") String pagination

    );

    @FormUrlEncoded
    @POST("api/action-favorite")
    Call<ResponseModel> addFavoriteProduct(
            @Header("Authorization") String Authorization,
            @Field("user_id") String user_id,
            @Field("product_id") String product_id)


            ;

    @GET("api/count-unread")
    Call<NotificationCountModel> getNotificationCount(@Header("Authorization") String bearer_token,
                                                      @Query("user_id") int user_id
    );

    @GET("api/my-notification")
    Call<NotificationDataModel> getNotifications(@Header("Authorization") String bearer_token,
                                                 @Header("lang") String lang,
                                                 @Query("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("api/delete-notification")
    Call<ResponseModel> deleteNotification(@Header("Authorization") String bearer_token,
                                           @Field("notification_id") int notification_id


    );

    @GET("api/setting")
    Call<SettingModel> getSetting(
            @Header("lang") String lang

    );

    @POST("api/search")
    Call<ALLProductDataModel> Filter(
            @Header("lang") String lang,
            @Body FilterModel filterModel);

    @GET("api/companies")
    Call<CompanyDataModel> getCompany(
            @Header("lang") String lang,
            @Query("search_key") String search_key);

    @GET("api/brands")
    Call<BrandDataModel> getBrands(
            @Header("lang") String lang,
            @Query("search_key") String search_key);

    @FormUrlEncoded
    @POST("api/contact-us")
    Call<StatusResponse> contactUs(@Field("name") String name,
                                   @Field("email") String email,
                                   @Field("phone") String phone,
                                   @Field("message") String message);

    @Multipart
    @POST("api/send-prescription")
    Call<ResponseModel> addRosheta(@Header("Authorization") String bearer_token,
                                   @Part("user_id") RequestBody user_part,

                                   @Part MultipartBody.Part logo


    );

    @POST("api/add-cart")
    Call<CartDataModel> createCart(
            @Header("Authorization") String bearer_token,
            @Body AddCartDataModel addCartDataModel);

    @GET("api/specializations")
    Call<SpecialDataModel> getSpecial(@Header("lang") String lang);

    @GET("api/doctors")
    Call<DoctorsDataModel> getDoctorsFilter(@Header("lang") String lang,
                                            @Query("search_key") String search_key,
                                            @Query("specialization_id") String specialization_id
    );

    @GET("api/one-doctor")
    Call<UserModel> getDoctorById(@Query("doctor_id") int doctor_id
    );

    @GET("api/get-room-betwwen-user")
    Call<CreateRoomModel> createRoom(@Header("Authorization") String bearer_token,
                                     @Query("first_user_id") int first_user_id,
                                     @Query("second_user_id") int second_user_id


    );

    @GET("api/user-rooms")
    Call<RoomDataModel> getRoom(@Header("Authorization") String user_token,
                                @Query("user_id") int user_id
    );

    @GET("api/room-messages")
    Call<MessageDataModel> getChatMessages(@Header("Authorization") String bearer_token,
                                           @Query(value = "pagination") String pagination,
                                           @Query(value = "per_page") int per_page,
                                           @Query(value = "page") int page,
                                           @Query(value = "room_id") int room_id,
                                           @Query(value = "user_id") int user_id

    );


    @GET("api/admin-room-messages")
    Call<AdminMessageDataModel> getAdminChatMessages(@Header("Authorization") String bearer_token,
                                                     @Query(value = "pagination") String pagination,
                                                     @Query(value = "per_page") int per_page,
                                                     @Query(value = "page") int page,
                                                     @Query(value = "user_id") int user_id

    );


    @FormUrlEncoded
    @POST("api/send-admin-chat-message")
    Call<SingleAdminMessageDataModel> sendAdminChatMessage(@Header("Authorization") String bearer_token,
                                                           @Field("admin_room_id") int room_id,
                                                           @Field("user_id") int from_user_id,
                                                           @Field("admin_id") int to_admin_id,
                                                           @Field("type") String type,
                                                           @Field("message") String message


    );

    @Multipart
    @POST("api/send-admin-chat-message")
    Call<SingleAdminMessageDataModel> sendAdminChatAttachment(@Header("Authorization") String user_token,
                                                              @Part("admin_room_id") RequestBody admin_room_id,
                                                              @Part("user_id") RequestBody from_user_id,
                                                              @Part("admin_id") RequestBody to_admin_id,
                                                              @Part("type") RequestBody message_type,
                                                              @Part MultipartBody.Part attachment
    );


    @Multipart
    @POST("api/send-chat-message")
    Call<SingleMessageDataModel> sendChatAttachment(@Header("Authorization") String user_token,
                                                    @Part("user_room_id") RequestBody room_id,
                                                    @Part("from_user_id") RequestBody from_user_id,
                                                    @Part("to_user_id") RequestBody to_user_id,
                                                    @Part("type") RequestBody message_type,
                                                    @Part MultipartBody.Part attachment
    );


    @FormUrlEncoded
    @POST("api/send-chat-message")
    Call<SingleMessageDataModel> sendChatMessage(@Header("Authorization") String bearer_token,
                                                 @Field("user_room_id") int room_id,
                                                 @Field("from_user_id") int from_user_id,
                                                 @Field("to_user_id") int to_user_id,
                                                 @Field("type") String type,
                                                 @Field("message") String message


    );


    @GET("api/my-cart")
    Call<CartDataModel> getMyCart(
            @Header("Authorization") String Authorization,
            @Header("lang") String lang,
            @Query("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("api/change-item-amount")
    Call<CartDataModel> incrementDecrementCart(
            @Header("Authorization") String bearer_token,
            @Field("country_code") String country_code,
            @Field("user_id") String user_id,
            @Field("id") String id,
            @Field("cart_id") String cart_id,
            @Field("amount") String amount,
            @Field("operation") String operation
    );

    @FormUrlEncoded
    @POST("api/delete-item-cart")
    Call<CartDataModel> deleteItemCart(
            @Header("Authorization") String bearer_token,

            @Field("id") String id,
            @Field("cart_id") String cart_id
    );

    @FormUrlEncoded
    @POST("api/active-coupon")
    Call<CouponDataModel> checkCoupon(
            @Header("Authorization") String bearer_token,

            @Field("user_id") String user_id,
            @Field(value = "code") String code
    );

    @POST("api/create-order")
    Call<SingleOrderModel> addOrder(
            @Header("Authorization") String bearer_token,
            @Body AddOrderModel addCartDataModel);

    @GET("api/user-orders")
    Call<ALLOrderDataModel> getMyOrder(
            @Header("Authorization") String Authorization,
            @Header("lang") String lang,
            @Query("user_id") String user_id
    );

    @GET("api/one-order")
    Call<SingleOrderModel> getSingleOrder(
            @Header("Authorization") String Authorization,
            @Header("lang") String lang,
            @Query("order_id") String order_id
    );

    @FormUrlEncoded
    @POST("api/scan-prescription")
    Call<ResponseModel> scanOrder(@Header("Authorization") String user_token,
                                  @Field("user_id") String user_id,
                                  @Field("code") String code,
                                  @Field("country_code") String country_code


    );

    @GET("api/one-product")
    Call<ProductDataModel> getSingleProduct(@Header("lang") String lang,
                                            @Query("id") String id,
                                            @Query("user_id") String user_id,
                                            @Query("country_code") String country_code


    );

    @GET("api/one-product-attribute")
    Call<AttrDataModel> getFeature(@Header("lang") String lang,
                                   @Query("product_id") String product_id,
                                   @Query("attribute_id") String attribute_id


    );

    @FormUrlEncoded
    @POST("api/update-client-profile")
    Call<UserModel> updateProfile(@Header("Authorization") String bearer_token,
                                  @Field("user_id") int user_id,
                                  @Field("country_code") String country_code
    );
}