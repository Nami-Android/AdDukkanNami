package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class BrandDataModel implements Serializable {
    private String message;
    private int status;
    private int current_page;
    private List<Data> data;
    private String first_page_url;
    private int from;
    private int last_page;
    private String last_page_url;
    private String next_page_url;
    private String path;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public List<Data> getData() {
        return data;
    }

    public String getFirst_page_url() {
        return first_page_url;
    }

    public int getFrom() {
        return from;
    }

    public int getLast_page() {
        return last_page;
    }

    public String getLast_page_url() {
        return last_page_url;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public String getPath() {
        return path;
    }

    public class Data implements Serializable{
        public int id;
        public String is_shown;
        public String image;
        public String created_at;
        public String updated_at;
        public BrandTransFk brand_trans_fk;

        public int getId() {
            return id;
        }

        public String getIs_shown() {
            return is_shown;
        }

        public String getImage() {
            return image;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
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
            private String created_at;
            private String updated_at;

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

            public String getCreated_at() {
                return created_at;
            }

            public String getUpdated_at() {
                return updated_at;
            }
        }
    }

}
