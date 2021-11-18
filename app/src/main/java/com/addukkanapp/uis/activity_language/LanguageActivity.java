package com.addukkanapp.uis.activity_language;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import com.addukkanapp.R;
import com.addukkanapp.adapters.SpinnerCountryAdapter;
import com.addukkanapp.databinding.ActivityLanguageBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.AppLocalSettings;
import com.addukkanapp.models.CountryDataModel;
import com.addukkanapp.models.CountryModel;
import com.addukkanapp.models.SelectedLocation;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;
import com.addukkanapp.uis.activity_map.MapActivity;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageActivity extends AppCompatActivity {
    private ActivityLanguageBinding binding;
    private String lang = "";
    private Animation animation, animation2, animation3;
    private boolean isFromSplash = false;
    private Preferences preferences;
    private SpinnerCountryAdapter adapter;
    private List<CountryModel> countryModelList;
    private String countrycode;
    private String currency;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_language);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        isFromSplash = intent.getBooleanExtra("data", false);
    }

    private void initView() {
        countryModelList = new ArrayList<>();
        animation = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
        animation2 = AnimationUtils.loadAnimation(this, R.anim.scale_down_anim);
        animation3 = AnimationUtils.loadAnimation(this, R.anim.translate);

        preferences = Preferences.getInstance();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");

        if (lang.equals("ar")) {
            binding.flAr.setBackgroundResource(R.drawable.small_stroke_primary);
            binding.flEn.setBackgroundResource(0);

        } else {
            binding.flAr.setBackgroundResource(0);
            binding.flEn.setBackgroundResource(R.drawable.small_stroke_primary);

        }
        binding.btnNext.setVisibility(View.VISIBLE);

        binding.cardAr.setOnClickListener(view -> {
            lang = "ar";
            binding.flAr.setBackgroundResource(R.drawable.small_stroke_primary);
            binding.flEn.setBackgroundResource(0);
            binding.btnNext.setVisibility(View.VISIBLE);

        });

        binding.cardEn.setOnClickListener(view -> {
            lang = "en";
            binding.flAr.setBackgroundResource(0);
            binding.flEn.setBackgroundResource(R.drawable.small_stroke_primary);
            binding.btnNext.setVisibility(View.VISIBLE);
        });

        binding.btnNext.setOnClickListener(view -> {
            if (!isFromSplash) {
                Intent intent = getIntent();
                intent.putExtra("lang", lang);
                intent.putExtra("countrycode",countrycode);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Intent intent = new Intent(this, MapActivity.class);
                startActivityForResult(intent, 100);
            }

        });

        if (isFromSplash) {
            binding.logo2.startAnimation(animation);
        } else {
            binding.consData.setVisibility(View.VISIBLE);
            binding.logo2.setVisibility(View.GONE);
            binding.logo.setVisibility(View.VISIBLE);
            binding.spinner.setVisibility(View.GONE);

        }
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.consData.setVisibility(View.GONE);
                binding.logo2.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.logo2.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.consData.setVisibility(View.VISIBLE);
                binding.logo2.setVisibility(View.GONE);
                binding.logo.startAnimation(animation3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.logo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        adapter = new SpinnerCountryAdapter(countryModelList, this);
        getCountries();
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                countrycode = countryModelList.get(i).getCode();
                currency=countryModelList.get(i).getCountry_setting_trans_fk().getCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void getCountries() {

        Api.getService(Tags.base_url)
                .getCountries(lang)
                .enqueue(new Callback<CountryDataModel>() {
                    @Override
                    public void onResponse(Call<CountryDataModel> call, Response<CountryDataModel> response) {
                        // binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getStatus() == 200) {
                                if (response.body().getData() != null) {
                                    if (response.body().getData().size() > 0) {
                                        // binding.tvNoData.setVisibility(View.GONE);
                                        updateCountryData(response.body().getData());
                                    } else {
                                        //binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                }
                            } else {
                                //Toast.makeText(CountryActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            //binding.progBar.setVisibility(View.GONE);

                            switch (response.code()) {
                                case 500:
                                    //      Toast.makeText(CountryActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    //    Toast.makeText(CountryActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_");
                            } catch (NullPointerException e) {

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<CountryDataModel> call, Throwable t) {
                        try {
                            //binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //      Toast.makeText(CountryActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    //    Toast.makeText(CountryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                });

    }

    private void updateCountryData(List<CountryModel> data) {
        countryModelList.clear();
        countryModelList.addAll(data);

        adapter.notifyDataSetChanged();
        binding.spinner.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            SelectedLocation location = (SelectedLocation) data.getSerializableExtra("location");
            AppLocalSettings settings = preferences.isLanguageSelected(this);
            if (settings == null) {
                settings = new AppLocalSettings();

            }

            settings.setAddress(location.getAddress());
            settings.setLat(location.getLat());
            settings.setLng(location.getLng());
            //settings.setCountry_code(countrycode);
            preferences.setIsLanguageSelected(this, settings);
            Intent intent = getIntent();
            intent.putExtra("lang", lang);
            intent.putExtra("countrycode",countrycode);
            intent.putExtra("currency",currency);
            setResult(RESULT_OK, intent);
            finish();

        }
    }
}