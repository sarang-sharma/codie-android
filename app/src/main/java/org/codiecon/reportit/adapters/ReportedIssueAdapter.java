package org.codiecon.reportit.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.codiecon.reportit.ConnectionManager;
import org.codiecon.reportit.R;
import org.codiecon.reportit.models.ReportedIssue;
import org.codiecon.reportit.outbound.IssueService;

import java.util.List;

public class ReportedIssueAdapter extends RecyclerView.Adapter<ReportedIssueAdapter.IssueHolder> {

    private Context _context;

    private List<ReportedIssue> issues;

    public ReportedIssueAdapter(Context context, List<ReportedIssue> reportedIssues) {
        this._context = context;
        this.issues = reportedIssues;
    }

    @NonNull
    @Override
    public ReportedIssueAdapter.IssueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.issue_container, parent, false);
        return new IssueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReportedIssueAdapter.IssueHolder holder, int position) {
        final ReportedIssue issue = this.issues.get(position);
        if (issue != null) {
            final ImageContainerAdapter imageContainer = new ImageContainerAdapter((Activity) _context, issue.getImages());
            holder.title.setText(issue.getTitle());
            holder.location.setText(issue.getLocation());
            holder.viewPager.setAdapter(imageContainer);
            holder.reporter.setImageResource(R.drawable.character);

            holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    sliderDots(position, holder.slider_dots, issue.getImages().size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            sliderDots(0, holder.slider_dots, issue.getImages().size());
        }

        holder.upVotes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ConnectionManager.instance()
                    .create(IssueService.class)
                    .upvote(issue.getDescription()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });

        holder.downVotes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ConnectionManager.instance()
                    .create(IssueService.class)
                    .downVote(issue.getDescription()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public static class IssueHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView location;
        ImageView reporter;
        ViewPager viewPager;
        TextView upVotes;
        TextView downVotes;
        LinearLayout slider_dots;

        public IssueHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.issue_title);
            this.reporter = view.findViewById(R.id.reporter);
            this.location = view.findViewById(R.id.issue_location);
            this.slider_dots = view.findViewById(R.id.slider_dots);
            this.viewPager = view.findViewById(R.id.image_container);
        }
    }

    private void sliderDots(int currentPage, LinearLayout slider_dots, int size) {
        TextView[] dots = new TextView[size];
        slider_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(_context);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#343434"));
            slider_dots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(Color.parseColor("#A2A2A2"));
        }
    }

}
