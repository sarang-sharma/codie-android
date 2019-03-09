package org.codiecon.reportit;

import android.os.Bundle;
import android.util.Log;

import org.codiecon.reportit.adapters.ReportedIssueAdapter;
import org.codiecon.reportit.models.ReportedIssue;
import org.codiecon.reportit.models.response.ReportedIssueResponse;
import org.codiecon.reportit.outbound.IssueService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyIssueActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_issue_listing);

        this.recyclerView = findViewById(R.id.parent_recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ConnectionManager.instance()
            .create(IssueService.class)
            .getAll(0, 10).enqueue(new Callback<List<ReportedIssueResponse>>() {

            @Override
            public void onResponse(Call<List<ReportedIssueResponse>> call, Response<List<ReportedIssueResponse>> response) {
                List<ReportedIssue> issues = new ArrayList<>();
                if(response != null && response.body() != null){
                    for(ReportedIssueResponse item : response.body()){
                        ReportedIssue issue = new ReportedIssue();
                        issue.setTitle(item.getTitle());
                        issue.setDescription(item.getDescription());
                        issue.setLocation(item.getAddress());
                        issue.setImages(new ArrayList<>(item.getImages()));
                        issue.setReporter(item.getCreatedBy());
                        issue.setVotes(item.getVotes());
                        issues.add(issue);
                    }
                }
                adapter = new ReportedIssueAdapter(NearbyIssueActivity.this, issues);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<ReportedIssueResponse>> call, Throwable t) {
                Log.e( "jkfsadkjf", t.toString());
            }
        });
    }

}
