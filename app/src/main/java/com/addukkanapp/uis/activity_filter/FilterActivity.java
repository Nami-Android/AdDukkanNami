package com.addukkanapp.uis.activity_filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.addukkanapp.R;
import com.addukkanapp.adapters.BrandAdapter;
import com.addukkanapp.adapters.CompanyAdapter;
import com.addukkanapp.adapters.SubCategoryFilterAdapter;
import com.addukkanapp.databinding.ActivityFilterBinding;
import com.addukkanapp.language.Language;
import com.addukkanapp.models.BrandDataModel;
import com.addukkanapp.models.CompanyDataModel;
import com.addukkanapp.models.CompanyModel;
import com.addukkanapp.models.FilterModel;
import com.addukkanapp.models.MainCategoryDataModel;
import com.addukkanapp.models.SubCategoryDataModel;
import com.addukkanapp.models.UserModel;
import com.addukkanapp.preferences.Preferences;
import com.addukkanapp.remote.Api;
import com.addukkanapp.tags.Tags;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterActivity extends AppCompatActivity {
    private ActivityFilterBinding binding;
    private String lang;
    private List<SubCategoryDataModel> subCategoryDataModelList;
    private SubCategoryFilterAdapter adapter;
    private List<CompanyModel> list;
    private CompanyAdapter companyAdapter;
    private List<BrandDataModel.Data> dataList;
    private BrandAdapter brandAdapter;
    private UserModel userModel;
    private Preferences preferences;
    private MainCategoryDataModel.Data sub_departments;
    private List<Integer> departments;
    private List<Integer> brand_id;
    private List<Integer> product_company_id;
    private int pos;
    private FilterModel filterModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            pos = intent.getIntExtra("pos", 0);

            sub_departments = (MainCategoryDataModel.Data) intent.getSerializableExtra("data");

        }
    }

    private void initView() {
        filterModel = new FilterModel();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        subCategoryDataModelList = new ArrayList<>();
        list = new ArrayList<>();
        dataList = new ArrayList<>();
        subCategoryDataModelList.clear();
        departments = new ArrayList<>();
        brand_id = new ArrayList<>();
        product_company_id = new ArrayList<>();
        subCategoryDataModelList.addAll(sub_departments.getSub_departments());
        departments.add(subCategoryDataModelList.get(pos).getId());
        binding.setLang(lang);
        binding.recViewCountry.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubCategoryFilterAdapter(this, subCategoryDataModelList, pos);
        binding.recViewCountry.setAdapter(adapter);
        companyAdapter = new CompanyAdapter(this, list);
        binding.recViewCompany.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewCompany.setAdapter(companyAdapter);
        brandAdapter = new BrandAdapter(this, dataList);
        binding.recViewBrand.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewBrand.setAdapter(brandAdapter);
        binding.llBack.setOnClickListener(view -> finish());


        binding.llCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.elexpendCompany.isExpanded()) {
                    binding.elexpendCompany.setExpanded(false);
                    if (lang.equals("en")) {
                        binding.arrow3.setRotation(180);
                    } else {
                        binding.arrow3.setRotation(0);
                    }
                } else {
                    binding.elexpendCompany.setExpanded(true);
                    binding.arrow3.setRotation(-90);
                }
            }
        });
        binding.llBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.elexpendBrand.isExpanded()) {
                    binding.elexpendBrand.setExpanded(false);
                    if (lang.equals("en")) {
                        binding.arrow2.setRotation(180);
                    } else {
                        binding.arrow2.setRotation(0);
                    }
                } else {
                    binding.elexpendBrand.setExpanded(true);
                    binding.arrow2.setRotation(-90);
                }
            }
        });
        binding.lldepart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.elexpendDepart.isExpanded()) {
                    binding.elexpendDepart.setExpanded(false);
                    if (lang.equals("en")) {
                        binding.arrow.setRotation(180);
                    } else {
                        binding.arrow.setRotation(0);
                    }
                } else {
                    binding.elexpendDepart.setExpanded(true);
                    binding.arrow.setRotation(-90);


                }
            }
        });
        getCompanies();
        getBrands();
        binding.btnRecet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subCategoryDataModelList.clear();
                subCategoryDataModelList.addAll(sub_departments.getSub_departments());
                adapter = new SubCategoryFilterAdapter(FilterActivity.this, subCategoryDataModelList, pos);
                binding.recViewCountry.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                list.clear();
                companyAdapter = new CompanyAdapter(FilterActivity.this, list);
                binding.recViewCompany.setAdapter(companyAdapter);
                getCompanies();
                dataList.clear();
                brandAdapter = new BrandAdapter(FilterActivity.this, dataList);
                binding.recViewBrand.setAdapter(brandAdapter);
                getBrands();

            }
        });
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterModel.setBrand_id(brand_id);
                filterModel.setDepartments(departments);
                filterModel.setProduct_company_id(product_company_id);
                Intent intent = getIntent();
                intent.putExtra("data", filterModel);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    private void getCompanies() {
        list.clear();

        companyAdapter.notifyDataSetChanged();


        Api.getService(Tags.base_url)
                .getCompany(lang, "all").enqueue(new Callback<CompanyDataModel>() {
            @Override
            public void onResponse(Call<CompanyDataModel> call, Response<CompanyDataModel> response) {
                //   binding.progBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {

                    if (response.body() != null && response.body().getStatus() == 200) {
                        if (response.body().getData() != null) {
                            if (response.body().getData().size() > 0) {
                                //     binding.tvNoData.setVisibility(View.GONE);
                                list.clear();
                                list.addAll(response.body().getData());
                                companyAdapter.notifyDataSetChanged();
                            } else {
                                // binding.tvNoData.setVisibility(View.VISIBLE);

                            }
                        }
                    } else {
                        //    Toast.makeText(CountryActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                    }


                } else {
                    //     binding.progBar.setVisibility(View.GONE);

                    switch (response.code()) {
                        case 500:
                            //  Toast.makeText(CountryActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            // Toast.makeText(CountryActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            break;
                    }
                    try {
                        Log.e("error_code", response.code() + "_");
                    } catch (NullPointerException e) {

                    }
                }


            }

            @Override
            public void onFailure(Call<CompanyDataModel> call, Throwable t) {
                try {
                    //   binding.progBar.setVisibility(View.GONE);
                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage());
                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            //Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                        } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                        } else {
                            //Toast.makeText(CountryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {

                }
            }
        });

    }

    private void getBrands() {
        dataList.clear();

        brandAdapter.notifyDataSetChanged();


        Api.getService(Tags.base_url)
                .getBrands(lang, "all").enqueue(new Callback<BrandDataModel>() {
            @Override
            public void onResponse(Call<BrandDataModel> call, Response<BrandDataModel> response) {
                //   binding.progBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {

                    if (response.body() != null && response.body().getStatus() == 200) {
                        if (response.body().getData() != null) {
                            if (response.body().getData().size() > 0) {
                                //     binding.tvNoData.setVisibility(View.GONE);
                                dataList.clear();
                                dataList.addAll(response.body().getData());
                                brandAdapter.notifyDataSetChanged();
                            } else {
                                // binding.tvNoData.setVisibility(View.VISIBLE);

                            }
                        }
                    } else {
                        //    Toast.makeText(CountryActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                    }


                } else {
                    //     binding.progBar.setVisibility(View.GONE);

                    switch (response.code()) {
                        case 500:
                            //  Toast.makeText(CountryActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            // Toast.makeText(CountryActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            break;
                    }
                    try {
                        Log.e("error_code", response.code() + "_");
                    } catch (NullPointerException e) {

                    }
                }


            }

            @Override
            public void onFailure(Call<BrandDataModel> call, Throwable t) {
                try {
                    //   binding.progBar.setVisibility(View.GONE);
                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage());
                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            //Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                        } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                        } else {
                            //Toast.makeText(CountryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {

                }
            }
        });

    }


    public void addBrandid(BrandDataModel.Data data) {
        if (brand_id.contains(data.getId())) {
            brand_id.remove(data.getId());
        } else {
            brand_id.add(data.getId());
        }
    }

    public void addCompanyid(CompanyModel companyModel) {
        if (product_company_id.contains(companyModel.getId())) {
            product_company_id.remove(companyModel.getId());
        } else {
            product_company_id.add(companyModel.getId());
        }
    }

    public void addDepartid(SubCategoryDataModel subCategoryDataModel) {
        if (departments.contains(subCategoryDataModel.getId())) {
            departments.remove(subCategoryDataModel.getId());
        } else {
            departments.add(subCategoryDataModel.getId());
        }
    }
}