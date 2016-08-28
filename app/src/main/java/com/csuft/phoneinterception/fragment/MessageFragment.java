package com.csuft.phoneinterception.fragment;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.csuft.phoneinterception.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    @Bind(R.id.button_send_message)
    Button send_msg;

    Context context;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, v);
        init();
        return v;
    }

    private void init() {
        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
                builder.setSmallIcon(R.drawable.ic_phone_missed_black_24dp)
                        .setContentTitle("新拦截")
                        .setContentText("18973766628 湖南益阳");
//                Intent resultIntent = new Intent(context, MainActivity.class);
//                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//                stackBuilder.addParentStack(MainActivity.class);
//                stackBuilder.addNextIntent(resultIntent);
//                PendingIntent resultPendingIntent =
//                        stackBuilder.getPendingIntent(
//                                0,
//                                PendingIntent.FLAG_UPDATE_CURRENT
//                        );
//                builder.setContentIntent(resultPendingIntent);
                Notification notification = builder.build();
                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(10, notification);

            }
        });
    }


}
