package com.addukkanapp.uis.activity_ask_doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.addukkanapp.R;
import com.addukkanapp.adapters.DoctorsAdapter;
import com.addukkanapp.adapters.SpecializationAdapter;
import com.addukkanapp.databinding.ActivityAskDoctorBinding;
import com.addukkanapp.interfaces.Listeners;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.DoctorsDataModel;
import com.addukkanapp.models.SpecialDataModel;
import com.addukkanapp.models.SpecialModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_doctor_detials.DoctorDetialsActivity;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AskDoctorActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityAskDoctorBinding binding;
    private String lang;
    private List<SpecialModel> specialModelList;
    private SpecialModel selectedSpecialize;
    private int selectedPos = -1;
    private SpecializationAdapter specializationAdapter;
    private List<UserModel.Data> doctorList;
    private DoctorsAdapter adapter;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ask_doctor);
        initView();

    }

    private void initView() {
        doctorList = new ArrayList<>();
        specialModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setBackListener(this);
        binding.setFilter("");
        binding.imageFilter.setOnClickListener(v -> openSheet());
        binding.imageCloseSpecialization.setOnClickListener(v -> closeSheet());

        adapter = new DoctorsAdapter(this,doctorList);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setAdapter(adapter);

        specializationAdapter = new SpecializationAdapter(this,specialModelList);
        binding.recViewSpecialization.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewSpecialization.setAdapter(specializationAdapter);
        binding.imageClearFilter.setOnClickListener(v -> {
            binding.setFilter("");
            selectedPos=-1;
            selectedSpecialize=null;
            specializationAdapter.updateSelection(selectedPos);

        });
        binding.editQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId== EditorInfo.IME_ACTION_SEARCH){
                String query = binding.editQuery.getText().toString().trim();
                search(query);
            }
            return false;
        });
        binding.editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()){
                    search("all");
                }
            }
        });
        getSpecialization();
        search("all");

    }
    private void getSpecialization() {

        Api.getService(Tags.base_url)
                .getSpecial(lang)
                .enqueue(new Callback<SpecialDataModel>() {
                    @Override
                    public void onResponse(Call<SpecialDataModel> call, Response<SpecialDataModel> response) {

                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getStatus() == 200) {
                                if (response.body().getData() != null) {
                                    if (response.body().getData().size() > 0) {
                                        updateSpecialData(response.body().getData());
                                    }
                                }
                            } else {

                            }


                        } else {

                            switch (response.code()) {
                                case 500:
                                    break;
                                default:
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_");
                            } catch (NullPointerException e) {

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<SpecialDataModel> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                });

    }

    private void updateSpecialData(List<SpecialModel> data) {
        specialModelList.clear();
        specialModelList.addAll(data);
        if (specialModelList.size()>0){
            specializationAdapter.notifyDataSetChanged();
            binding.tvNoDataSpecialization.setVisibility(View.GONE);

        }else {
            binding.tvNoDataSpecialization.setVisibility(View.VISIBLE);

        }


    }

    private void search(String query)
    {
        doctorList.clear();
        adapter.notifyDataSetChanged();
        binding.tvNoData.setVisibility(View.GONE);
        binding.progBar.setVisibility(View.VISIBLE);
        String spec_id = "all";
        String q = "all";

        if (selectedSpecialize!=null){
            spec_id = String.valueOf(selectedSpecialize.getId());
        }

        if (query!=null&&!query.isEmpty()){
            q = query;
        }

        Api.getService(Tags.base_url)
                .getDoctorsFilter(lang,q,spec_id)
                .enqueue(new Callback<DoctorsDataModel>() {
                    @Override
                    public void onResponse(Call<DoctorsDataModel> call, Response<DoctorsDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getStatus() == 200) {
                                if (response.body().getData() != null) {
                                    if (response.body().getData().size() > 0) {
                                        doctorList.addAll(response.body().getData());
                                        adapter.notifyDataSetChanged();
                                    }else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {

                            }


                        } else {
                            binding.progBar.setVisibility(View.GONE);

                            switch (response.code()) {
                                case 500:
                                    break;
                                default:
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_");
                            } catch (NullPointerException e) {

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<DoctorsDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                });


    }
    private void openSheet() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        binding.flSpecializationSheet.clearAnimation();
        binding.flSpecializationSheet.startAnimation(animation);


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.flSpecializationSheet.setVisibility(View.VISIBLE);


            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void closeSheet() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        binding.flSpecializationSheet.clearAnimation();
        binding.flSpecializationSheet.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.flSpecializationSheet.setVisibility(View.GONE);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void back() {
        if (binding.flSpecializationSheet.getVisibility()==View.VISIBLE){
            closeSheet();
        }else {
            finish();

        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void setSpecializeItemData(SpecialModel specialModel, int adapterPosition) {
        this.selectedSpecialize = specialModel;
        selectedPos = adapterPosition;
        binding.setFilter(specialModel.getSpecialization_trans_fk().getTitle());
        String query = binding.editQuery.getText().toString().trim();
        search(query);
    }




    public void setDoctorItemData(UserModel.Data model) {
        Intent intent = new Intent(this, DoctorDetialsActivity.class);
        intent.putExtra("data", model);
        startActivity(intent);
    }
}
