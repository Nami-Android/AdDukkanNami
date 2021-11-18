package com.addukkanapp.notifications;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.addukkanapp.R;
import com.addukkanapp.models.ChatRoomModel;
import com.addukkanapp.models.MessageModel;
import com.addukkanapp.models.ResponseModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_chat.ChatActivity;
import com.addukkanapp.uis.activity_notification.NotificationActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FireBaseMessaging extends FirebaseMessagingService {

    private Preferences preferences = Preferences.getInstance();
    private Map<String, String> map;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        map = remoteMessage.getData();

        for (String key : map.keySet()) {
            Log.e("Key=", key + "_value=" + map.get(key));
        }
        String notification_type = map.get("noti_type");


        if (notification_type.equals("chat")) {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            String className = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            if (className.equals("com.addukkanapp.uis.activity_chat.ChatActivity")) {
                String room_id = map.get("room_id");
                String current_room_id = getRoom();
                if (room_id.equals(current_room_id)){
                    String id = map.get("room_message_id");
                    String from_user_id = map.get("from_user_id");
                    String to_user_id = map.get("to_user_id");

                    String date = map.get("notification_date");
                    String type = map.get("data_chat_type");
                    String message = map.get("message");
                    String from = map.get("from_user_data");
                    String to = map.get("to_user_data");

                    String voice = "";
                    String image = "";

                    if (type.equals("voice")) {
                        voice = map.get("voice");
                    }else if (type.equals("image")) {
                        image = map.get("image");
                    }

                    UserModel.Data from_user = new Gson().fromJson(from,UserModel.Data.class);
                    UserModel.Data to_user = new Gson().fromJson(to,UserModel.Data.class);

                    MessageModel messageModel = new MessageModel(Integer.parseInt(id),Integer.parseInt(from_user_id), Integer.parseInt(to_user_id),Integer.parseInt(room_id) , type, message,voice ,image ,date ,date ,from_user,to_user );
                    EventBus.getDefault().post(messageModel);
                }else {
                    manageNotification(map);

                }


            } else {
                manageNotification(map);
            }

        } else {
            manageNotification(map);

        }
    }

    private void manageNotification(Map<String, String> map) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNewNotificationVersion(map);
        } else {
            createOldNotificationVersion(map);

        }

    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        updateToken(s);

    }

    private void updateToken(String token) {
        UserModel userModel = getUserData();
        if (userModel==null){
            return;
        }
        Api.getService(Tags.base_url)
                .updateFirebaseToken("Bearer " + userModel.getData().getToken(), userModel.getData().getId(), token, "android")
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                            Log.e("data", "success");

                            userModel.getData().setFirebase_token(token);
                            preferences.create_update_userdata(FireBaseMessaging.this, userModel);

                        } else {
                            try {

                                Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("errorToken2", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    // Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createNewNotificationVersion(Map<String, String> map) {
        String notification_type = map.get("noti_type");

        String sound_Path = "";
        if (sound_Path.isEmpty()) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            sound_Path = uri.toString();
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        String CHANNEL_ID = "my_channel_02";
        CharSequence CHANNEL_NAME = "my_channel_name";
        int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

        final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);

        channel.setShowBadge(true);
        channel.setSound(Uri.parse(sound_Path), new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        );
        builder.setChannelId(CHANNEL_ID);
        builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);

        String title = "";
        String body = "";
        Intent intent;

        if (notification_type.equals("chat")) {
            String my_id = getUserData().getData().getId()+"";
            String from_user_id = map.get("from_user_id");
            String to_user_id = map.get("to_user_id");
            String from = map.get("from_user_data");
            String to = map.get("to_user_data");
            String room_id = map.get("room_id");
            UserModel.Data from_user = new Gson().fromJson(from,UserModel.Data.class);
            UserModel.Data to_user = new Gson().fromJson(to,UserModel.Data.class);
            ChatRoomModel roomModel;
            if (my_id.equals(from_user_id)){
                roomModel = new ChatRoomModel(Integer.parseInt(room_id), Integer.parseInt(to_user_id), to_user.getLogo(), to_user.getName());
            }else {
                roomModel = new ChatRoomModel(Integer.parseInt(room_id), Integer.parseInt(from_user_id), from_user.getLogo(), from_user.getName());

            }
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra("data", roomModel);
            String type = map.get("data_chat_type");
            title = from_user.getName();
            if (type.equals("voice")) {
                body = getString(R.string.voice_uploaded);

            } else if (type.equals("image")) {
                body = getString(R.string.image_uploaded);
            } else {
                body = map.get("message");

            }


        } else {
            intent = new Intent(this, NotificationActivity.class);


        }


        builder.setContentTitle(title);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        builder.setLargeIcon(bitmap);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {

            manager.createNotificationChannel(channel);
            manager.notify(Tags.not_tag, Tags.not_id, builder.build());


        }


    }

    private void createOldNotificationVersion(Map<String, String> map) {
        String notification_type = map.get("notification_type");

        String sound_Path = "";
        if (sound_Path.isEmpty()) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            sound_Path = uri.toString();
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


        builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);

        String title = "";
        String body = "";
        Intent intent;

        if (notification_type.equals("chat")) {
            String my_id = getUserData().getData().getId()+"";
            String from_user_id = map.get("from_user_id");
            String to_user_id = map.get("to_user_id");
            String from = map.get("from_user_data");
            String to = map.get("to_user_data");
            String room_id = map.get("room_id");
            UserModel.Data from_user = new Gson().fromJson(from,UserModel.Data.class);
            UserModel.Data to_user = new Gson().fromJson(to,UserModel.Data.class);
            ChatRoomModel roomModel;
            if (my_id.equals(from_user_id)){
                roomModel = new ChatRoomModel(Integer.parseInt(room_id), Integer.parseInt(to_user_id), to_user.getLogo(), to_user.getName());
            }else {
                roomModel = new ChatRoomModel(Integer.parseInt(room_id), Integer.parseInt(from_user_id), from_user.getLogo(), from_user.getName());

            }
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra("data", roomModel);
            String type = map.get("data_chat_type");
            title = from_user.getName();
            if (type.equals("voice")) {
                body = getString(R.string.voice_uploaded);

            } else if (type.equals("image")) {
                body = getString(R.string.image_uploaded);
            } else {
                body = map.get("message");

            }


        } else {
            intent = new Intent(this, NotificationActivity.class);


        }


        builder.setContentTitle(title);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        builder.setLargeIcon(bitmap);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {

            manager.notify(Tags.not_tag, Tags.not_id, builder.build());


        }


    }
    
    private UserModel getUserData() {
        return preferences.getUserData(this);
    }

    private String getRoom() {
        return preferences.getRoomId(this);
    }


}
