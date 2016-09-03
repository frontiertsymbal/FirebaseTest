package brains.mock.firebasetest.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import brains.mock.firebasetest.R;
import brains.mock.firebasetest.model.Chat;
import brains.mock.firebasetest.ui.view.holder.ChatItemViewHolder;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatItemViewHolder> {

    private ArrayList<Chat> chatList;

    public ChatListAdapter(ArrayList<Chat> chatList) {
        this.chatList = chatList;
    }

    @Override
    public ChatItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        ChatItemViewHolder chatViewHolder = new ChatItemViewHolder(v);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatItemViewHolder holder, int position) {
        holder.bindToItem(chatList.get(position));
    }

    @Override
    public int getItemCount() {
        if (chatList != null) {
            return chatList.size();
        } else {
            return 0;
        }
    }
}
