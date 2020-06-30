package com.kyser.thenews.component;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kyser.thenews.R;
import com.kyser.thenews.newsstream.model.Article;
import com.kyser.thenews.newsstream.model.NewsResponse;


import androidx.recyclerview.widget.RecyclerView;

public class NewsItemAdaptor extends RecyclerView.Adapter<NewsItemAdaptor.ViewHolder>  {

    private final Context mContext;
    private NewsResponse mNewsResponse;
    private ItemSelection mItemSelection;
    public interface ItemSelection{ void onItemSelection(Article article, int position);}

    public NewsItemAdaptor(Context context,ItemSelection itemSelection) {
        this.mContext = context;
        this.mItemSelection = itemSelection;
    }

    public NewsResponse getNewsResponse() {
        return mNewsResponse;
    }

    public void setNewsResponse(NewsResponse mNewsResponse) {
        this.mNewsResponse = mNewsResponse;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.news_item,null,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mNewsResponse.getArticles().get(position).getTitle());
        Log.v("========","===="+mNewsResponse.getArticles().get(position).getUrlToImage());
        Glide.with(holder.image)
                .load(mNewsResponse.getArticles().get(position).getUrlToImage())
                .into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemSelection.onItemSelection(mNewsResponse.getArticles().get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewsResponse!=null?mNewsResponse.getArticles().size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.news_item_title);
            image = (ImageView) itemView.findViewById(R.id.news_item_image);
        }
    }
}
