package app.client.munchbear.munchbearclient.model.store;

import app.client.munchbear.munchbearclient.model.order.make.MakeOrder;
import app.client.munchbear.munchbearclient.model.request.AddCreditCardRequest;
import app.client.munchbear.munchbearclient.model.request.CreateReservationRequestModel;
import app.client.munchbear.munchbearclient.model.request.FCMTokenRequest;
import app.client.munchbear.munchbearclient.model.request.FindDeliveryZoneRequest;
import app.client.munchbear.munchbearclient.model.request.LoginBaseRequestModel;
import app.client.munchbear.munchbearclient.model.request.LoginRequestModel;
import app.client.munchbear.munchbearclient.model.request.RegisterRequestModel;
import app.client.munchbear.munchbearclient.model.request.TokenRefreshModel;
import app.client.munchbear.munchbearclient.model.request.UpdateReservationRequest;
import app.client.munchbear.munchbearclient.model.response.AddCreditCardResponse;
import app.client.munchbear.munchbearclient.model.response.CardListResponse;
import app.client.munchbear.munchbearclient.model.response.CreateReservationResponse;
import app.client.munchbear.munchbearclient.model.request.SocLoginRequestModel;
import app.client.munchbear.munchbearclient.model.response.FindDeliveryZoneResponse;
import app.client.munchbear.munchbearclient.model.response.GetUserDataResponse;
import app.client.munchbear.munchbearclient.model.response.LoginResponse;
import app.client.munchbear.munchbearclient.model.response.MyOrdersResponse;
import app.client.munchbear.munchbearclient.model.response.MyReservationResponse;
import app.client.munchbear.munchbearclient.model.response.OrderDetailsResponse;
import app.client.munchbear.munchbearclient.model.response.PaymentMethodResponse;
import app.client.munchbear.munchbearclient.model.response.RestaurantListResponse;
import app.client.munchbear.munchbearclient.model.response.RestaurantMenuResponse;
import app.client.munchbear.munchbearclient.model.response.UpdateReservationResponse;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Roman on 7/5/2018.
 */
public interface Repository {

    @POST("/api/customer/v1/login")
    Single<Response<LoginResponse>> login (@Body LoginRequestModel loginRequestModel);

    @POST("/api/customer/v1/register")
    Single<Response<LoginResponse>> register (@Body RegisterRequestModel registerRequestModel);

    @GET("/api/customer/v1/restaurants/delivery")
    Single<Response<RestaurantListResponse>> restaurantDeliveryList();

    @GET("/api/customer/v1/restaurants")
    Single<Response<RestaurantListResponse>> restaurantInsideList();

    @POST("/api/customer/v1/auth/easy/facebook")
    Single<Response<LoginResponse>> facebookLogin(@Body SocLoginRequestModel socLoginRequestModel);

    @POST("/api/customer/v1/auth/easy/google")
    Single<Response<LoginResponse>> googleLogin(@Body SocLoginRequestModel socLoginRequestModel);

    @GET("/api/customer/v1/restaurants/{restaurant_id}/menu")
    Single<Response<RestaurantMenuResponse>> getRestaurantMenu(@Path("restaurant_id") int restaurantId,
                                                               @Header("Authorization") String accessToken);

    @POST("/api/customer/v1/restaurants/{restaurant_id}/reservations")
    Single<Response<CreateReservationResponse>> createReservation(@Path("restaurant_id") int restaurantId,
                                                                  @Header("Authorization") String accessToken,
                                                                  @Body CreateReservationRequestModel requestModel);

    @GET("/api/customer/v1/reservations/")
    Single<Response<MyReservationResponse>> getMyReservation(@Query("page") String nextPage,
                                                             @Header("Authorization") String accessToken);

    @PUT("/api/customer/v1/reservations/{reservationId}")
    Single<Response<UpdateReservationResponse>> updateReservation(@Path("reservationId") int reservationId,
                                                                  @Header("Authorization") String accessToken,
                                                                  @Body UpdateReservationRequest requestModel);

    @DELETE("/api/customer/v1/reservations/{reservationId}")
    Single<Response<ResponseBody>> cancelReservation(@Path("reservationId") int reservationId,
                                                     @Header("Authorization") String accessToken);

    @POST("/api/customer/v1/fcm-token")
    Single<Response<ResponseBody>> connectFCMToken(@Body FCMTokenRequest fcmTokenRequest,
                                                   @Header("Authorization") String accessToken);

    @POST("/api/customer/v1/guest")
    Single<Response<LoginResponse>> guestLogin(@Body LoginBaseRequestModel requestModel);

    @POST("/api/customer/v1/refresh")
    Single<Response<LoginResponse>> refreshToken(@Body TokenRefreshModel tokenRefreshModel);

    @GET("/api/customer/v1/user")
    Single<Response<GetUserDataResponse>> getUser(@Header("Authorization") String accessToken);

    @POST("/api/customer/v1/restaurants/{restaurant_id}/orders")
    Single<Response<ResponseBody>> createOrder(@Path("restaurant_id") int restaurantId,
                                               @Header("Authorization") String accessToken,
                                               @Body MakeOrder makeOrderModel);

    @POST("/api/customer/v1/restaurants/{restaurant_id}/delivery-zones/find")
    Single<Response<FindDeliveryZoneResponse>> findDeliveryZone(@Path("restaurant_id") int restaurantId,
                                                                @Header("Authorization") String accessToken,
                                                                @Body FindDeliveryZoneRequest requestModel);

    @GET("/api/customer/v1/restaurants/{restaurant_id}/payment/available")
    Single<Response<PaymentMethodResponse>> getPaymentMethod(@Path("restaurant_id") int restaurantId,
                                                             @Header("Authorization") String accessToken);

    @GET("/api/customer/v1/orders")
    Single<Response<MyOrdersResponse>> getOrderList(@Query("page") String nextPage,
                                                    @Header("Authorization") String accessToken);

    @GET("/api/customer/v1/orders/{order_id}")
    Single<Response<OrderDetailsResponse>> getOrderDetails(@Path("order_id") String orderId, @Header("Authorization") String accessToken);

    @POST("/api/customer/v1/cards")
    Single<Response<AddCreditCardResponse>> addCreditCard(@Body AddCreditCardRequest requestModel,
                                                          @Header("Authorization") String accessToken);

    @GET("/api/customer/v1/cards")
    Single<Response<CardListResponse>> getCardList(@Header("Authorization") String accessToken);

    @DELETE("/api/customer/v1/cards/{card_id}")
    Single<Response<ResponseBody>> deleteCard(@Path("card_id") int cardId,
                                              @Header("Authorization") String accessToken);

    @PUT("/api/customer/v1/cards/{card_id}")
    Single<Response<ResponseBody>> updateCard(@Body AddCreditCardRequest requestModel,
                                              @Header("Authorization") String accessToken,
                                              @Path("card_id") int cardId);
}
