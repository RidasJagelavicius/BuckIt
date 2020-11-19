package com.example.buckit.ui.inspirationFeed;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.buckit.R;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private String [] friend_names = {"@mygoodfriend1", "@mygoodfriend2", "@mygoodfriend3"};
    private String [] list_titles = {"Home Cooking", "Spring Break", "Books to Read"};

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return friend_names.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, final int position){
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.friends_slider, null);
        TextView names = (TextView) view.findViewById(R.id.friends_names);
        names.setText(friend_names[position]);

        TextView titles = (TextView) view.findViewById(R.id.friends_titles);
        titles.setText(list_titles[position]);

        titles.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.i("TAG", "This page was clicked: " + list_titles[position]);
            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
