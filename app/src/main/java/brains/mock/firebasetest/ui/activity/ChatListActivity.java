package brains.mock.firebasetest.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import brains.mock.firebasetest.R;
import brains.mock.firebasetest.model.Chat;
import brains.mock.firebasetest.model.User;
import brains.mock.firebasetest.ui.adapter.ChatListAdapter;
import brains.mock.firebasetest.util.Const;

import java.util.ArrayList;

public class ChatListActivity extends BaseActivity implements View.OnClickListener {

    private String mUserUID;
    private DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference(Const.REFERENCE_USER);
    private DatabaseReference mChatListReference = FirebaseDatabase.getInstance().getReference(Const.REFERENCE_CHAT);
    private Toolbar mActionBarToolbar;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mChatList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mUserUID = getIntent().getStringExtra(Const.EXTRA_USER_UID);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mChatList = (RecyclerView) findViewById(R.id.chatList);

        setSupportActionBar(mActionBarToolbar);
        mFloatingActionButton.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mChatList.setLayoutManager(linearLayoutManager);

        final ArrayList<Chat> chatList = new ArrayList<>();
        final ChatListAdapter adapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(adapter);

        mUserReference.child(mUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                adapter.notifyDataSetChanged();
                User currentUser = dataSnapshot.getValue(User.class);
                ArrayList<String> chatUIDList = new ArrayList<>();
                chatUIDList.addAll(currentUser.getChats().keySet());
                for (String chatUID : chatUIDList) {
                    mChatListReference.child(chatUID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Chat chat = dataSnapshot.getValue(Chat.class);
                            for (String userUID : chat.getMembers().keySet()) {
                                if (!userUID.equals(mUserUID)) {
                                    mUserReference.child(mUserUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User user = dataSnapshot.getValue(User.class);
                                            if (user != null) {
                                                chat.setChatName(user.getUserName());
                                            }
                                            chatList.add(chat);
                                            adapter.notifyItemInserted(chatList.size() - 1);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(ChatListActivity.this, UserSettingsActivity.class);
            intent.putExtra(Const.EXTRA_USER_UID, mUserUID);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.openSettingsButton:

//                break;
//
            case R.id.floatingActionButton:
                Log.e(TAG, "mFloatingActionButton");
                break;
        }
    }
}
