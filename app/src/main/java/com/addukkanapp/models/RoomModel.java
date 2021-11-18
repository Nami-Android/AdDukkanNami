package com.addukkanapp.models;

import java.io.Serializable;

public class RoomModel implements Serializable {

   private int id;
   private int first_user_id;
   private int second_user_id;
   private int unread_messages_count;
   private UserModel.Data other_user;
   private LastMessages last_messages;

    public int getId() {
        return id;
    }

    public int getFirst_user_id() {
        return first_user_id;
    }

    public int getSecond_user_id() {
        return second_user_id;
    }

    public int getUnread_messages_count() {
        return unread_messages_count;
    }

    public UserModel.Data getOther_user() {
        return other_user;
    }

    public void setUnread_messages_count(int unread_messages_count) {
        this.unread_messages_count = unread_messages_count;
    }

    public LastMessages getLast_messages() {
        return last_messages;
    }

    public static class LastMessages implements Serializable
    {
        private int id;
        private int from_user_id;
        private int to_user_id;
        private int user_room_id;
        private String type;
        private String message;
        private String voice;
        private String image;
        private String created_at;

        public int getId() {
            return id;
        }

        public int getFrom_user_id() {
            return from_user_id;
        }

        public int getTo_user_id() {
            return to_user_id;
        }

        public int getUser_room_id() {
            return user_room_id;
        }

        public String getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }

        public String getVoice() {
            return voice;
        }

        public String getImage() {
            return image;
        }

        public String getCreated_at() {
            return created_at;
        }
    }


}