package org.codiecon.reportit.outbound;

import org.codiecon.reportit.models.request.ReportIssueRequest;
import org.codiecon.reportit.models.response.ReportedIssueResponse;
import org.codiecon.reportit.models.response.Wrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IssueService {

    @GET("issue/getAll")
    Call<Wrapper<List<ReportedIssueResponse>>> getAll(@Query("page") Integer page, @Query("size") Integer size);

    @POST("issue/save")
    Call<Void> save(@Body ReportIssueRequest request);

    @GET("issue/upvote")
    Call<Long> upvote(@Query("id") String id);

    @GET("issue/downvote")
    Call<Long> downVote(@Query("id") String id);

}
