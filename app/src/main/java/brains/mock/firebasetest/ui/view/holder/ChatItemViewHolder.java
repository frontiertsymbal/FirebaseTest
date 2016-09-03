package brains.mock.firebasetest.ui.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import brains.mock.firebasetest.R;
import brains.mock.firebasetest.model.Chat;
import brains.mock.firebasetest.util.DateTimeUtils;

public class ChatItemViewHolder extends RecyclerView.ViewHolder {

    public TextView vLastMessageUserName;
    public TextView vLastMessage;
    public TextView vTimestamp;

    public ChatItemViewHolder(View itemView) {
        super(itemView);

        vLastMessageUserName = (TextView) itemView.findViewById(R.id.chatName);
        vLastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
        vTimestamp = (TextView) itemView.findViewById(R.id.time);
    }

    public void bindToItem(Chat chat) {
        vLastMessageUserName.setText(chat.getChatName());
        vLastMessage.setText(chat.getLastMessageUserName() + " : " + chat.getLastMessage());
        vTimestamp.setText(DateTimeUtils.getMessageTimeString(chat.getTimeStamp()));
    }
}
