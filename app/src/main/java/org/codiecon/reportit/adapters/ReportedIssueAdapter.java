package org.codiecon.reportit.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.codiecon.reportit.R;
import org.codiecon.reportit.models.ReportedIssue;

import java.util.List;

public class ReportedIssueAdapter extends RecyclerView.Adapter<ReportedIssueAdapter.IssueHolder> {

    private int page = 0;
    private Context _context;

    private int[] images;
    private List<ReportedIssue> issues;

    public ReportedIssueAdapter(Context context, List<ReportedIssue> reportedIssues) {
        this._context = context;
        this.issues = reportedIssues;
        this.images = new int[]{
            R.drawable.character,
            R.drawable.character,
            R.drawable.character,
            R.drawable.character,
            R.drawable.character,
            R.drawable.character
        };
    }

    @NonNull
    @Override
    public ReportedIssueAdapter.IssueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.image_container_layout, parent, false);
        return new IssueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReportedIssueAdapter.IssueHolder holder, int position) {
        ReportedIssue issue = this.issues.get(position);
        if (issue != null) {
            final ImageContainerAdapter imageContainer = new ImageContainerAdapter((Activity) _context, this.images);
            holder.viewPager.setAdapter(imageContainer);

            holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    page = position;
                    addBottomDots(position, holder.slider_dots);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            addBottomDots(0, holder.slider_dots);
        }

    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public static class IssueHolder extends RecyclerView.ViewHolder {
        ViewPager viewPager;
        LinearLayout slider_dots;

        public IssueHolder(View view) {
            super(view);
            this.slider_dots = view.findViewById(R.id.slider_dots);
            this.viewPager = view.findViewById(R.id.image_container);
        }
    }

    private void addBottomDots(int currentPage, LinearLayout slider_dots) {
        TextView[] dots = new TextView[this.images.length];
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
