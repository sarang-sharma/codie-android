package org.codiecon.reportit;

import android.os.Bundle;

import org.codiecon.reportit.adapters.ReportedIssueAdapter;
import org.codiecon.reportit.models.ReportedIssue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NearbyIssueActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<ReportedIssue> populate(){
        List<ReportedIssue> issues = new ArrayList<>();
        for(int i=0; i<10; i++){
            ReportedIssue issue = new ReportedIssue();
            issue.setTitle("Tree Fallen on road");
            issue.setImages(Arrays.asList(R.drawable.tree_fallen_1, R.drawable.tree_fallen_2, R.drawable.tree_fallen_3));
            issue.setTags(Arrays.asList("road", "block", "traffic"));
            issue.setLocation("HSR Sector " + i);
            issues.add(issue);
        }
        return issues;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_issue_listing);

        this.recyclerView = findViewById(R.id.parent_recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ReportedIssueAdapter(this, populate());
        recyclerView.setAdapter(adapter);
    }

}
