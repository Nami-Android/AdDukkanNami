package com.addukkanapp.uis.activity_chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addukkanapp.R;
import com.addukkanapp.adapters.ChatAdapter;
import com.addukkanapp.databinding.ActivityChatBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.ChatRoomModel;
import com.addukkanapp.models.MessageDataModel;
import com.addukkanapp.models.MessageModel;
import com.addukkanapp.models.SingleMessageDataModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_home.HomeActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private String lang;
    private final int IMG_REQ = 1;
    private final int CAMERA_REQ = 2;
    private final int MIC_REQ = 3;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String WRITE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String CAMERA_PERM = Manifest.permission.CAMERA;
    private final String MIC_PERM = Manifest.permission.RECORD_AUDIO;
    private MediaRecorder recorder;
    private String audio_path = "";
    private Handler handler;
    private Runnable runnable;
    private long audio_total_seconds = 0;
    private ChatRoomModel chatRoomModel;
    private UserModel userModel;
    private Preferences preferences;
    private ChatAdapter adapter;
    private List<MessageModel> messageModelList;
    private int current_page = 1;
    private boolean isLoading = false;
    private LinearLayoutManager manager;
    private Call<MessageDataModel> loadMoreCall;
    private boolean isFromFireBase = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        new Handler()
                .postDelayed(() -> {
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel(Tags.not_tag, Tags.not_id);

                    }
                }, 1);

        chatRoomModel = (ChatRoomModel) intent.getSerializableExtra("data");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        messageModelList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        manager = new LinearLayoutManager(this);
        binding.setModel(chatRoomModel);
        adapter = new ChatAdapter(messageModelList, this, userModel.getData().getId());
        binding.recView.setLayoutManager(manager);
        binding.recView.setAdapter(adapter);

        binding.llBack.setOnClickListener(v -> {
            back();
        });




        binding.imageChooser.setOnClickListener(v -> {
            checkGalleryPermission();

        });

        binding.imageCamera.setOnClickListener(v -> {
            checkCameraPermission();
        });

        binding.imageRecord.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isMicReady()) {
                    createMediaRecorder();

                } else {
                    checkMicPermission();
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                if (isMicReady()) {
                    try {
                        recorder.stop();
                        stopTimer();
                        sendAttachment(audio_path, "voice");
                    } catch (Exception e) {
                        Log.e("error1", e.getMessage() + "___");
                    }

                }

            }

            return true;
        });

        binding.imageSend.setOnClickListener(v -> {
            String message = binding.edtMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                binding.edtMessage.setText("");
                sendChatText(message);
            }
        });





        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int current_item_pos = manager.findLastCompletelyVisibleItemPosition();
                    int total_items = adapter.getItemCount();
                    if (total_items >= 38 && (current_item_pos-total_items) == 2 && !isLoading) {
                        messageModelList.add(0, null);
                        adapter.notifyItemInserted(0);
                        isLoading = true;
                        int page = current_page + 1;
                        loadMore(page);
                    }
                }
            }
        });

        EventBus.getDefault().register(this);
        getAllMessages();

        preferences.create_room_id(this, String.valueOf(chatRoomModel.getRoom_id()));



    }


    public void getAllMessages() {
        if (loadMoreCall != null) {
            loadMoreCall.cancel();
        }
        Log.e("room_id", chatRoomModel.getRoom_id()+"_");
        Api.getService(Tags.base_url)
                .getChatMessages("Bearer " + userModel.getData().getToken(), "on", 40, 1,chatRoomModel.getRoom_id(),userModel.getData().getId())
                .enqueue(new Callback<MessageDataModel>() {
                    @Override
                    public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getData() != null) {

                                if (response.body().getData().size() > 0) {
                                    messageModelList.clear();
                                    messageModelList.addAll(response.body().getData());
                                    adapter.notifyDataSetChanged();
                                    binding.recView.postDelayed(() -> binding.recView.smoothScrollToPosition(messageModelList.size() - 1), 200);

                                }
                            }


                        } else {
                            try {
                                Log.e("error code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(ChatActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }


                        }

                    }

                    @Override
                    public void onFailure(Call<MessageDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("Error", t.getMessage());

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ChatActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().contains("socket")) {

                                } else {
                                    Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }


    private void loadMore(int page) {

        loadMoreCall = Api.getService(Tags.base_url).getChatMessages("Bearer " + userModel.getData().getToken(), "on", 40, page,chatRoomModel.getRoom_id(),userModel.getData().getId());


        loadMoreCall.enqueue(new Callback<MessageDataModel>() {
            @Override
            public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {


                messageModelList.remove(messageModelList.size() - 1);
                adapter.notifyItemRemoved(messageModelList.size() - 1);
                isLoading = false;

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getData() != null) {

                        if (response.body().getData().size() > 0) {
                            messageModelList.addAll(0, response.body().getData());
                            adapter.notifyItemRangeChanged(0, response.body().getData().size());
                            current_page = response.body().getCurrent_page();
                        }
                    }

                } else {
                    if (messageModelList.get(0) == null) {
                        messageModelList.remove(0);
                        adapter.notifyItemRemoved(0);
                        isLoading = false;
                    }


                    if (response.code() == 500) {
                        Toast.makeText(ChatActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }

                    try {
                        Log.e("error code", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onFailure(Call<MessageDataModel> call, Throwable t) {
                try {

                    if (messageModelList.get(0) == null) {
                        messageModelList.remove(0);
                        adapter.notifyItemRemoved(0);
                        isLoading = false;
                    }


                    if (t.getMessage() != null) {
                        Log.e("Error", t.getMessage());

                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            Toast.makeText(ChatActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    private void sendAttachment(String file_uri, String attachment_type) {
        Intent intent = new Intent(this, ServiceUploadAttachment.class);
        intent.putExtra("file_uri", file_uri);
        intent.putExtra("user_token", userModel.getData().getToken());
        intent.putExtra("user_id", userModel.getData().getId());
        intent.putExtra("to_user_id", chatRoomModel.getUser_id());
        intent.putExtra("room_id", chatRoomModel.getRoom_id());
        intent.putExtra("attachment_type", attachment_type);
        startService(intent);


    }

    private void sendChatText(String message) {

        Api.getService(Tags.base_url)
                .sendChatMessage("Bearer " + userModel.getData().getToken(), chatRoomModel.getRoom_id(), userModel.getData().getId(),chatRoomModel.getUser_id(), "message", message)
                .enqueue(new Callback<SingleMessageDataModel>() {
                    @Override
                    public void onResponse(Call<SingleMessageDataModel> call, Response<SingleMessageDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null&&response.body().getStatus()==200){
                             if (response.body().getData() != null) {
                                MessageModel model = response.body().getData();
                                messageModelList.add(model);
                                adapter.notifyItemInserted(messageModelList.size());
                                binding.recView.postDelayed(() -> binding.recView.smoothScrollToPosition(messageModelList.size() - 1), 200);
                            }
                            }



                        }

                    }

                    @Override
                    public void onFailure(Call<SingleMessageDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("Error", t.getMessage());

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ChatActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().contains("socket")) {

                                } else {
                                    Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }


    private void createMediaRecorder() {

        String audio_name = "AUD" + System.currentTimeMillis() + ".mp3";

        File file = new File(Tags.audio_path);
        boolean isFolderCreate;

        if (!file.exists()) {
            isFolderCreate = file.mkdir();
        } else {
            isFolderCreate = true;
        }


        if (isFolderCreate) {
            startTimer();
            binding.recordTime.setVisibility(View.VISIBLE);
            audio_path = file.getAbsolutePath() + "/" + audio_name;
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioChannels(1);
            recorder.setOutputFile(audio_path);
            try {
                recorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            recorder.start();
        } else {
            Toast.makeText(this, "Unable to create sound file on your device", Toast.LENGTH_SHORT).show();
        }


    }


    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, CAMERA_PERM) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, WRITE_PERM) == PackageManager.PERMISSION_GRANTED) {
            selectImage(CAMERA_REQ);

        } else {

            ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERM, WRITE_PERM}, CAMERA_REQ);

        }

    }


    private void checkGalleryPermission() {
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) == PackageManager.PERMISSION_GRANTED) {
            selectImage(IMG_REQ);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, IMG_REQ);

        }

    }


    private void checkMicPermission() {
        if (ActivityCompat.checkSelfPermission(this, MIC_PERM) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, WRITE_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{MIC_PERM, WRITE_PERM}, MIC_REQ);

        }

    }

    private boolean isMicReady() {

        if (ActivityCompat.checkSelfPermission(this, MIC_PERM) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, WRITE_PERM) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;

    }

    private void selectImage(int req) {

        Intent intent = new Intent();
        if (req == IMG_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            }
            intent.setType("image/*");


        } else if (req == CAMERA_REQ) {

            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        }

        startActivityForResult(intent, req);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage(requestCode);
            } else {
                Toast.makeText(this, "Access image denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                selectImage(requestCode);
            } else {
                Toast.makeText(this, "Access camera denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MIC_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Access Mic denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            sendAttachment(uri.toString(), "image");

        } else if (requestCode == CAMERA_REQ && resultCode == RESULT_OK && data != null) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            Uri uri = getUriFromBitmap(bitmap);
            sendAttachment(uri.toString(), "image");

        }

    }


    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
        return Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", ""));

    }


    private void startTimer() {
        handler = new Handler();
        runnable = () -> {
            audio_total_seconds += 1;
            binding.recordTime.setText(getRecordTimeFormat(audio_total_seconds));
            startTimer();
        };

        handler.postDelayed(runnable, 1000);
    }

    private void stopTimer() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        audio_total_seconds = 0;
        if (runnable != null) {
            handler.removeCallbacks(runnable);

        }
        handler = null;
        binding.recordTime.setText("00:00:00");
        binding.recordTime.setVisibility(View.GONE);
    }

    private String getRecordTimeFormat(long seconds) {
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int second = (int) (seconds % 60);

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, second);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAttachmentSuccess(MessageModel messageModel) {
        messageModelList.add(messageModel);
        adapter.notifyItemChanged(messageModelList.size());
        binding.recView.postDelayed(() -> binding.recView.smoothScrollToPosition(messageModelList.size() - 1), 200);
        if (messageModel.getFrom_user().getId() == userModel.getData().getId()) {
            deleteFile();

        }

    }

    private void deleteFile() {
        if (!audio_path.isEmpty()) {
            File file = new File(audio_path);
            if (file.exists()) {
                file.delete();
            }
        }
    }


    @Override
    public void onBackPressed() {
        back();
    }

    public void back() {

        preferences.create_room_id(this, "");
        if (isFromFireBase) {
            Intent intent;
            intent = new Intent(this, HomeActivity.class);

            startActivity(intent);

        }
        if (adapter != null) {
            adapter.stopPlay();
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }


        finish();
    }

}