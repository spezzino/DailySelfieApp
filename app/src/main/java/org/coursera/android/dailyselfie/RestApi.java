package org.coursera.android.dailyselfie;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by spezzino on 11/17/15.
 */
public interface RestApi {
    @POST("/applyFilter")
    @Multipart
    public void applyFilter(
            @Part("image") TypedFile image,
            @Query("effect") String effect,
            final Callback<Response> callback);
}
