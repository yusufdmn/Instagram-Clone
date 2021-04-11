package com.example.firebaseinstagram;

import android.telephony.ims.RcsUceAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedRcyclerAdapter extends RecyclerView.Adapter<FeedRcyclerAdapter.PostHolder> {
    private ArrayList<String> usermailList , userCommentList , userImageList;

    public FeedRcyclerAdapter(ArrayList<String> usermailList, ArrayList<String> userCommentList, ArrayList<String> userImageList) {
        this.usermailList = usermailList;
        this.userCommentList = userCommentList;
        this.userImageList = userImageList;
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {    //// recycler_rowu bağladık

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycle_row , parent , false);

        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.mailText.setText("From: " + usermailList.get(position));
        holder.commentText.setText("Comment: "+ userCommentList.get(position));
        Picasso.get (). load (userImageList.get(position)) .into (holder.imageView);

    }

    @Override
    public int getItemCount() {  /// usermail gibi listeler oluşturmuştuk FeedActivitede. onda kaç tane varsa onu döndürüyor.
        return usermailList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{    ////  RECYCLE_ROW DAKİ ELEMANLARI BURAYA TANIMLIYORUZ. ONLARI TUTUYOR.

        ImageView imageView;
        TextView mailText, commentText;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.postId);
            commentText = itemView.findViewById(R.id.commentId);
            mailText = itemView.findViewById(R.id.mailId);

        }
    }


}
