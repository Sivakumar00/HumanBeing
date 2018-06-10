package siva.com.weengineers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.drive.query.Query;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.data;
import static android.R.attr.thumb;

public class ChatsFragment extends Fragment {
    RecyclerView mChats_recyclerview;
    private FirebaseAuth mAuth;
    private DatabaseReference mChatsDatabase;
    private String mCurrent_user_id;
    private CircleImageView alert_image;
    private View mView;
    private TextView noChats;
    private DatabaseReference mUsersDatabase,mMessagesDatabase;


    public static class UsersViewHolder extends ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public void setName(String name) {
          //  ((TextView) this.mView.findViewById(R.id.name_text_layout)).setText(name);
        }

        public void setlastmessage(String lastmessage) {
            ((TextView) this.mView.findViewById(R.id.message_text_layout)).setText(lastmessage);
        }

        public void setThump(final String thumb, final Context context) {
            final CircleImageView mCirculerImage = (CircleImageView) this.mView.findViewById(R.id.message_profile_layout);
            Picasso.with(context).load(thumb).networkPolicy(NetworkPolicy.OFFLINE, new NetworkPolicy[0]).placeholder((int) R.drawable.default_avatar).into(mCirculerImage, new Callback() {
                public void onSuccess() {
                }

                public void onError() {
                    Picasso.with(context).load(thumb).placeholder((int) R.drawable.default_avatar).into(mCirculerImage);
                }
            });
        }

        public View getView() {
            return this.mView;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView= inflater.inflate(R.layout.fragment_chats, container, false);
        mChats_recyclerview = (RecyclerView) mView.findViewById(R.id.chats_recyclerview);
        mAuth=FirebaseAuth.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();
        mMessagesDatabase=FirebaseDatabase.getInstance().getReference().child("messages");
        mMessagesDatabase.keepSynced(true);
        alert_image=(CircleImageView)mView.findViewById(R.id.user_single_online_icon);
        noChats=(TextView)mView.findViewById(R.id.no_chat);

        mChatsDatabase=FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
        mChatsDatabase.keepSynced(true);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
       mUsersDatabase.keepSynced(true);
        this.mChats_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.mChats_recyclerview.setHasFixedSize(true);
        return mView;

    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void onStart() {
        super.onStart();
    final FirebaseRecyclerAdapter<ChatFragmentRecycler,ChatViewHolder> chatRecyclerViewAdapter=new FirebaseRecyclerAdapter<ChatFragmentRecycler, ChatViewHolder>(
            ChatFragmentRecycler.class,
            R.layout.users_single_layout,
            ChatViewHolder.class,
            mChatsDatabase
    ) {
        @Override
        protected void populateViewHolder(final ChatViewHolder viewHolder, final ChatFragmentRecycler model, final int i) {

           final String list_user_id=getRef(i).getKey();
            viewHolder.setLastmessage(model.getLastmessage());



            mMessagesDatabase.child(mCurrent_user_id).child(list_user_id).orderByChild(mCurrent_user_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String lastmessage=dataSnapshot.child("message").getValue().toString();
                    viewHolder.setLastmessage(lastmessage);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String userName=dataSnapshot.child("name").getValue().toString();
                    final String userThumb=dataSnapshot.child("thumb_image").getValue().toString();
                    //String userStatus=dataSnapshot.child
                    noChats.setVisibility(View.INVISIBLE);
                    viewHolder.setName(userName);
                   // viewHolder.setLastmessage(model.getLastmessage());
                    viewHolder.setUserImage(userThumb,getContext());
                    viewHolder.mView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                            chatIntent.putExtra("user_id", list_user_id);
                            chatIntent.putExtra("user_name", userName);
                            chatIntent.putExtra("thumb", thumb);
                            startActivity(chatIntent);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //viewHolder.setName(model.getName());

        }
    };
    mChats_recyclerview.setAdapter(chatRecyclerViewAdapter);
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public ChatViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name){
            TextView userNameView=(TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);


        }

        public void setAlert(String thumb_image, final Context ctx){
            final ImageView userImageView = (ImageView) mView.findViewById(R.id.user_single_online_icon);
                userImageView.setImageResource(R.drawable.alert);
            userImageView.setVisibility(View.VISIBLE);

        }
        public void setLastmessage(String lastmessage){
            TextView userMessageView=(TextView)mView.findViewById(R.id.user_single_status);
            userMessageView.setText(lastmessage);
        }
        public void setUserImage(final String thumb_image, final Context ctx){
            final CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            //Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

            //    if(thumb_image.equals("default")){
            //Picasso.with(ctx).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_avatar).into(userImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

                }
            });
        }


    }

}
