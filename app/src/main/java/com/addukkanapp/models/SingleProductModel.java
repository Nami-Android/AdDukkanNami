package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class SingleProductModel implements Serializable {
    private int id;
    private int vendor_id;
    private String brand_id;
    private int product_company_id;
    private String main_image;
    private double rate;
    private double total_stock;
    private double sell_count;
    private double view_count;
    private String have_offer;
    private String offer_type;
    private int offer_value;
    private int offer_min;
    private int offer_bonus;
    private double price;
    private ProductTransFk product_trans_fk;
    private ProductDefaultPrice product_default_price;
    private Favourite favourite;
    private List<Sub> product_attr;
    private UserModel.Data vendor_fk;
    private BrandFk brand_fk;
    private ProductCompanyFk product_company_fk;
    private List<ProductImage> product_images;
    private int amount = 0;
    private boolean isLoading = false;
    private boolean isSelected = false;
    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public int getProduct_company_id() {
        return product_company_id;
    }

    public String getMain_image() {
        return main_image;
    }

    public double getRate() {
        return rate;
    }

    public double getTotal_stock() {
        return total_stock;
    }

    public double getSell_count() {
        return sell_count;
    }

    public double getView_count() {
        return view_count;
    }

    public String getHave_offer() {
        return have_offer;
    }

    public String getOffer_type() {
        return offer_type;
    }

    public int getOffer_value() {
        return offer_value;
    }

    public int getOffer_min() {
        return offer_min;
    }

    public int getOffer_bonus() {
        return offer_bonus;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public ProductTransFk getProduct_trans_fk() {
        return product_trans_fk;
    }

    public ProductDefaultPrice getProduct_default_price() {
        return product_default_price;
    }

    public Favourite getFavourite() {
        return favourite;
    }

    public List<Sub> getProduct_attr() {
        return product_attr;
    }

    public UserModel.Data getVendor_fk() {
        return vendor_fk;
    }

    public BrandFk getBrand_fk() {
        return brand_fk;
    }

    public ProductCompanyFk getProduct_company_fk() {
        return product_company_fk;
    }

    public List<ProductImage> getProduct_images() {
        return product_images;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public class ProductTransFk implements Serializable {
        private int id;
        private int product_id;
        private String title;
        private String description;
        private String details;
        private String lang;

        public int getId() {
            return id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getDetails() {
            return details;
        }

        public String getLang() {
            return lang;
        }
    }

    public class ProductDefaultPrice implements Serializable {
        private int id;
        private int product_id;
        private int product_set_id;
        private int vendor_id;
        private double price;
        private int stock;
        private String is_default;
        private String country_code;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public int getProduct_set_id() {
            return product_set_id;
        }

        public int getVendor_id() {
            return vendor_id;
        }

        public double getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }

        public String getIs_default() {
            return is_default;
        }

        public String getCountry_code() {
            return country_code;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

    public class Favourite implements Serializable {
        private int id;
        private int product_id;
        private int user_id;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

    public class Sub implements Serializable{
        private int id;
        private int product_id;
        private int vendor_id;
        private int level;
        private int parant_id;
        private int attribute_id;
        private double price;
        private int stock;
        private String is_default;
        private String country_code;
        private String image;
        private List<Sub> sub;
        private String title;
        private AttributeTransFk attribute_trans_fk;

        public int getId() {
            return id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public int getVendor_id() {
            return vendor_id;
        }

        public int getLevel() {
            return level;
        }

        public int getParant_id() {
            return parant_id;
        }

        public int getAttribute_id() {
            return attribute_id;
        }

        public double getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }

        public String getIs_default() {
            return is_default;
        }

        public String getCountry_code() {
            return country_code;
        }

        public String getImage() {
            return image;
        }

        public List<Sub> getSub() {
            return sub;
        }

        public String getTitle() {
            return title;
        }

        public AttributeTransFk getAttribute_trans_fk() {
            return attribute_trans_fk;
        }

        public class AttributeTransFk implements Serializable{
            private int id;
            private int attribute_id;
            private String title;
            private String lang;
            private String created_at;
            private String updated_at;

            public int getId() {
                return id;
            }

            public int getAttribute_id() {
                return attribute_id;
            }

            public String getTitle() {
                return title;
            }

            public String getLang() {
                return lang;
            }

            public String getCreated_at() {
                return created_at;
            }

            public String getUpdated_at() {
                return updated_at;
            }
        }
    }



    public class BrandFk implements Serializable{
        private int id;
        private String is_shown;
        private String image;
        private BrandTransFk brand_trans_fk;

        public int getId() {
            return id;
        }

        public String getIs_shown() {
            return is_shown;
        }

        public String getImage() {
            return image;
        }

        public BrandTransFk getBrand_trans_fk() {
            return brand_trans_fk;
        }

        public class BrandTransFk implements Serializable{
            private int id;
            private int brand_id;
            private String title;
            private String image;
            private String lang;

            public int getId() {
                return id;
            }

            public int getBrand_id() {
                return brand_id;
            }

            public String getTitle() {
                return title;
            }

            public String getImage() {
                return image;
            }

            public String getLang() {
                return lang;
            }
        }
    }



    public class ProductCompanyFk implements Serializable{
        private int id;
        private String is_shown;
        private String image;
        private ProductCompanyTransFk product_company_trans_fk;

        public int getId() {
            return id;
        }

        public String getIs_shown() {
            return is_shown;
        }

        public String getImage() {
            return image;
        }

        public ProductCompanyTransFk getProduct_company_trans_fk() {
            return product_company_trans_fk;
        }

        public class ProductCompanyTransFk{
            private int id;
            private int product_company_id;
            private String title;
            private String lang;

            public int getId() {
                return id;
            }

            public int getProduct_company_id() {
                return product_company_id;
            }

            public String getTitle() {
                return title;
            }

            public String getLang() {
                return lang;
            }
        }
    }

    public class ProductImage implements Serializable{
        private int id;
        private String image;
        private int product_id;

        public int getId() {
            return id;
        }

        public String getImage() {
            return image;
        }

        public int getProduct_id() {
            return product_id;
        }
    }
}
