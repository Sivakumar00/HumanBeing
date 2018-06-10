package siva.com.weengineers.Adapter;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.icu.text.RelativeDateTimeFormatter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import siva.com.weengineers.Common.ISO8601Parse;
import siva.com.weengineers.DetailArticle;
import siva.com.weengineers.Interface.ItemClickListener;
import siva.com.weengineers.ListNews;
import siva.com.weengineers.Model.Article;
import siva.com.weengineers.R;

/**
 * Created by MANIKANDAN on 18-11-2017.
 */


class ListNewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ItemClickListener itemClickListener;
    TextView article_title,article_desc;
    RelativeTimeTextView article_time;
    CircleImageView article_image;

    public ListNewsViewHolder(View itemView) {
        super(itemView);
        article_image=(CircleImageView)itemView.findViewById(R.id.article_image);
        article_time=(RelativeTimeTextView)itemView.findViewById(R.id.article_times);
        article_title=(TextView)itemView.findViewById(R.id.article_titles);
        article_desc=(TextView)itemView.findViewById(R.id.article_desc);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}


public class ListNewsAdapter extends RecyclerView.Adapter<ListNewsViewHolder>{


    private List<Article> articleList;
    private Context context;

    public ListNewsAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }

    @Override
    public ListNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.news_layout,parent,false);
        return new ListNewsViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(ListNewsViewHolder holder, int position) {

        Picasso.with(context).load(articleList.get(position).getUrlToImage())
                .into(holder.article_image);


       if(articleList.get(position).getTitle().length()>50) {
            String title=articleList.get(position).getTitle().substring(0,50)+"...";
            Log.e("ajhfjadhfjahdfja",title);
            holder.article_title.setText(title);
           }
       else
           holder.article_title.setText(articleList.get(position).getTitle());

        String desc=articleList.get(position).getDescription();

           if(desc.length()>130&&desc!=null&&!desc.isEmpty()) {
                desc = articleList.get(position).getDescription().substring(0,130)+"...";
               Log.e("desc",desc);
           holder.article_desc.setText(desc);
       }else if (desc!=null&&!desc.isEmpty())
        holder.article_desc.setText(articleList.get(position).getDescription());

       Date date=null;
        try{
         if(articleList.get(position).getPublishedAt()!=null&& !articleList.get(position).getPublishedAt().isEmpty())
            date= ISO8601Parse.parse(articleList.get(position).getPublishedAt());
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        holder.article_time.setReferenceTime(date.getTime());
         holder.setItemClickListener(new ItemClickListener() {
        @Override
        public void onClick(View view, int position, boolean isLongClick) {
            Intent intent=new Intent(context,DetailArticle.class);
            intent.putExtra("webURL",articleList.get(position).getUrl());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    });

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
