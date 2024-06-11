package vip.mokardder.smsclient.apiInterface;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import vip.mokardder.smsclient.models.ResponseModel;
import vip.mokardder.smsclient.models.SendDataPayload;
import vip.mokardder.smsclient.models.SendQoutaUpdate;

public interface API {


        @POST("AKfycbxxIgVG1KMdx3SYiJ3MoIeuRxW7tVlnlzyphSwQDAVRAJX9lqEzcTiE8CZNvMlWnev0/exec")
        Call<ResponseModel> uploadFcm (@Body SendDataPayload payload);
        @POST("AKfycbxxIgVG1KMdx3SYiJ3MoIeuRxW7tVlnlzyphSwQDAVRAJX9lqEzcTiE8CZNvMlWnev0/exec")
        Call<ResponseModel> updateQouta (@Body SendQoutaUpdate payload);
}
