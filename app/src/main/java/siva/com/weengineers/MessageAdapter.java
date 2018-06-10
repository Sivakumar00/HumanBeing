package siva.com.weengineers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static siva.com.weengineers.R.id.scroll;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);
        View v1=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sender,parent,false);

        mAuth=FirebaseAuth.getInstance();
        return new MessageViewHolder(v);


    }



    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView custom_image;
        public CircleImageView profileImage;
        public TextView displayName;

        public MessageViewHolder(View view) {
            super(view);
            custom_image=(CircleImageView)view.findViewById(R.id.custom_bar_image);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            //displayName = (TextView) view.findViewById(R.id.name_text_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);
        String current_user_id=mAuth.getCurrentUser().getUid();
        String from_user = c.getFrom();
        if(from_user.equals(current_user_id)){
//            viewHolder.messageText.notify();
            viewHolder.messageText.setGravity(Gravity.RIGHT);
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_sender);
            viewHolder.messageText.setTextColor(Color.BLACK);
            viewHolder.messageText.setText(c.getMessage());



        }
        else
        {
            viewHolder.messageText.setGravity(Gravity.BOTTOM);
            viewHolder.messageText.setForegroundGravity(Gravity.RIGHT);
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.WHITE);
            viewHolder.messageText.setText(c.getMessage());
        }

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

               // viewHolder.displayName.setText(name);

                Picasso.with(viewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }



}
