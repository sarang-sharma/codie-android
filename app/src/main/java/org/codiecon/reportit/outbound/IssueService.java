package org.codiecon.reportit.outbound;

import org.codiecon.reportit.models.request.ReportIssueRequest;
import org.codiecon.reportit.models.response.ReportedIssueResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IssueService {

    @GET("issue/getAll")
    Call<List<ReportedIssueResponse>> getAll();

    @POST("issue/save")
    Call<Void> save(@Body ReportIssueRequest request);

    @POST("issue/upvote")
    Call<Void> upvote(@Query("id") String id);

    @POST("issue/downVote")
    Call<Void> downVote(@Query("id") String id);

}
