package siva.com.weengineers;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.florent37.diagonallayout.DiagonalLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import siva.com.weengineers.Adapter.ListNewsAdapter;
import siva.com.weengineers.Common.Common;
import siva.com.weengineers.Interface.NewsService;
import siva.com.weengineers.Model.Article;
import siva.com.weengineers.Model.News;

public class ListNews extends AppCompatActivity {

    KenBurnsView kbv;
    DiagonalLayout diagonalLayout;
    SpotsDialog dialog;
    NewsService mService;
    TextView top_author, top_title;
    SwipeRefreshLayout swipeRefreshLayout;

    String source="",sortBy="",webHotURL="";

    ListNewsAdapter adapter;
    RecyclerView lstNews;
   // LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_news);
//service
        mService= Common.getNewsService();
        dialog=new SpotsDialog(this);
      //view

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
         swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                    loadNews(source,true);
                 
             }
         });

        diagonalLayout=(DiagonalLayout)findViewById(R.id.diagonalLayout);
        diagonalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),DetailArticle.class);
                intent.putExtra("webURL",webHotURL);
                startActivity(intent);
            }
        });
        kbv=(KenBurnsView)findViewById(R.id.top_image);
        top_author=(TextView)findViewById(R.id.top_author);
        top_title=(TextView)findViewById(R.id.top_title);

       lstNews=(RecyclerView)findViewById(R.id.lstnnews);

        lstNews.setHasFixedSize(true);
     LinearLayoutManager layoutManager=new LinearLayoutManager(this);
     layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lstNews.setLayoutManager(layoutManager);
lstNews.setAdapter(adapter);

        if(getIntent()!=null){
            source=getIntent().getStringExtra("source");
            sortBy=getIntent().getStringExtra("sortBy");
            //if(!source.isEmpty()&&!sortBy.isEmpty()){
                loadNews(source,false);

            //}
        }
    }

    private void loadNews(String source, boolean isRefreshed) {
        if (!isRefreshed) {

            dialog.show();
            mService.getNewestArticles(Common.getAPIUrl(source, sortBy,Common.API_KEY))
            .enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    dialog.dismiss();
                    if(response.body().getArticles()!=null) {
                        Picasso.with(getBaseContext()).load(response.body().getArticles().get(0).getUrlToImage()).into(kbv);
                        top_title.setText(response.body().getArticles().get(0).getTitle());
                        top_author.setText(response.body().getArticles().get(0).getAuthor());

                        webHotURL = response.body().getArticles().get(0).getUrl();
                        //load other article
                        List<Article> removeFirstItem=response.body().getArticles();
                        removeFirstItem.remove(0);
                        adapter=new ListNewsAdapter(removeFirstItem,getBaseContext());
                        adapter.notifyDataSetChanged();
                        lstNews.setAdapter(adapter);

                    }
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {

                }
            });
        }
        else{
            dialog.show();
            mService.getNewestArticles(Common.getAPIUrl(source, sortBy,Common.API_KEY))
                    .enqueue(new Callback<News>() {
                        @Override
                        public void onResponse(Call<News> call, Response<News> response) {
                            dialog.dismiss();
                            if(response.body().getArticles()!=null) {
                                Picasso.with(getBaseContext()).load(response.body().getArticles().get(0).getUrlToImage()).into(kbv);
                                top_title.setText(response.body().getArticles().get(0).getTitle());
                                top_author.setText(response.body().getArticles().get(0).getAuthor());

                                webHotURL = response.body().getArticles().get(0).getUrl();
                                //load other article
                                List<Article> removeFirstItem=response.body().getArticles();
                                removeFirstItem.remove(0);
                                adapter=new ListNewsAdapter(removeFirstItem,getBaseContext());
                                adapter.notifyDataSetChanged();
                                lstNews.setAdapter(adapter);

                            }
                        }

                        @Override
                        public void onFailure(Call<News> call, Throwable t) {

                        }
                    });
            swipeRefreshLayout.setRefreshing(false);
        }

    }
}
