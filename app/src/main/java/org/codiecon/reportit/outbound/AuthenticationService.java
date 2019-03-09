package org.codiecon.reportit.outbound;

import org.codiecon.reportit.models.request.AuthenticationRequest;
import org.codiecon.reportit.models.response.AuthenticationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationService {

    @POST("authenticate")
    Call<AuthenticationResponse> authenticate(@Body AuthenticationRequest payload);

}
