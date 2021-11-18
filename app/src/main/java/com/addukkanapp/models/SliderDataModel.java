package com.addukkanapp.models;

import java.io.Serializable;
import java.util.List;

public class SliderDataModel implements Serializable {
    private List<SliderModel> data;
    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<SliderModel> getData() {
        return data;
    }

    public static class SliderModel implements Serializable {
        private int id;
        private int slider_id;
        private String title;
        private String image;
        private String lang;
        private String created_at;
        private String updated_at;
        private SlidersTransFk sliders_trans_fk;

        public int getId() {
            return id;
        }

        public int getSlider_id() {
            return slider_id;
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

        public SlidersTransFk getSliders_trans_fk() {
            return sliders_trans_fk;
        }

        public class SlidersTransFk{
            private int id;
            private int slider_id;
            private String title;
            private String image;
            private String lang;
            private String  created_at;
            private String updated_at;

            public int getId() {
                return id;
            }

            public int getSlider_id() {
                return slider_id;
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
