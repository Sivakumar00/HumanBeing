package siva.com.weengineers.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.text.TextUtils;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import siva.com.weengineers.Common.Common;
import siva.com.weengineers.Interface.IconBetterIdeaService;
import siva.com.weengineers.Interface.ItemClickListener;
import siva.com.weengineers.ListNews;
import siva.com.weengineers.MainActivity;
import siva.com.weengineers.Model.IconBetterIdea;
import siva.com.weengineers.Model.WebSite;
import siva.com.weengineers.R;

/**
 * Created by MANIKANDAN on 16-11-2017.
 */

class ListSourceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ItemClickListener itemClickListener;

    TextView source_title;
    CircleImageView source_images;
    public ListSourceViewHolder(View itemView) {
        super(itemView);
        source_images= (CircleImageView)itemView.findViewById(R.id.source_image);
        source_title=(TextView)itemView.findViewById(R.id.source_name);
        itemView.setOnClickListener(this);
       // this.itemClickListener = itemClickListener;
    }
    void setItemClickListener(ItemClickListener itemClickListener) {
       this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}

public class ListSourceAdapter extends RecyclerView.Adapter<ListSourceViewHolder>{
    private Context context;
    private WebSite webSite;

    private IconBetterIdeaService mService;

    public ListSourceAdapter(Context context, WebSite webSite) {
        this.context = context;
        this.webSite = webSite;

        mService= Common.getIconService();

    }

    @Override
    public ListSourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.source_layout,parent,false);
        return new ListSourceViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ListSourceViewHolder holder, int position) {
        StringBuilder iconBetterAPI=new StringBuilder("https://icons.better-idea.org/allicons.json?url=");
        iconBetterAPI.append(webSite.getSources().get(position).getUrl());

        mService.getIconUrl(iconBetterAPI.toString())
                .enqueue(new Callback<IconBetterIdea>() {
                    @Override

                    public void onResponse(Call<IconBetterIdea> call, Response<IconBetterIdea> response) {

                        if (response.body()!=null && response.body().getIcons()!=null && response.body().getIcons().size() > 0 && !TextUtils.isEmpty(response.body().getIcons().get(0).getUrl()))
                        {
                                   Picasso.with(context)
                                           .load(response.body().getIcons().get(0).getUrl()).placeholder(R.drawable.ic_action_name)
                                           .into(holder.source_images);
                        }

                    }

                    @Override
                    public void onFailure(Call<IconBetterIdea> call, Throwable t) {

                    }
                });

        holder.source_title.setText(webSite.getSources().get(position).getName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //dddd
                Intent intent = new Intent(context, ListNews.class);
                intent.putExtra("source", webSite.getSources().get(position).getId());
//                intent.putExtra("sortby", webSite.getSources().get(position).getSortByAvailable().get(0)); //Se obtiene la configuración por defecto del acomodo
                intent.putExtra("sortby", "top");
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return webSite.getSources().size();
    }
}
