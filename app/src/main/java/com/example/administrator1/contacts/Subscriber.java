package com.example.administrator1.contacts;

import android.os.Parcel;
import android.os.Parcelable;

public class Subscriber implements Parcelable {
    private String number = "";
    private String group = "";
    private String homeNumber = "";
    private String email = "";
    private String name = "";
    private long id = -1;

    public Subscriber() {
    }

    public Subscriber(Parcel parcel) {
        name = parcel.readString();
        email = parcel.readString();
        number = parcel.readString();
        homeNumber = parcel.readString();
        group = parcel.readString();
        id = parcel.readLong();
    }


    public void update(Subscriber subscriber) {
        name = subscriber.getName();
        email = subscriber.getEmail();
        number = subscriber.getNumber();
        homeNumber = subscriber.getHomeNumber();
        group = subscriber.getGroup();
        id = subscriber.getId();
    }

    public boolean equalInValues(String name, String email, String number, String group, String homeNumber){
        boolean validName = (name.isEmpty() || this.name.contains(name));//( name.isEmpty() ? true : this.name.equals(name));
        boolean validGroup = (group.isEmpty() || this.group.contains(group));//( group.isEmpty() ? true : this.group.equals(group));
        boolean validEmail = (email.isEmpty() || this.email.contains(email));//( email.isEmpty() ? true : this.email.equals(email));
        boolean validNumber = (number.isEmpty() || this.number.contains(number));//( number.isEmpty() ? true : this.number.equals(number));
        boolean validHomeNumber = (homeNumber.isEmpty() || this.homeNumber.contains(homeNumber));// ( homeNumber.isEmpty() ? true : this.homeNumber.equals(homeNumber));

        if (validHomeNumber && validEmail && validNumber && validName && validGroup) {
            return true;
        }

        return false;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHomeNumber() {
        return homeNumber;
    }

    public void setHomeNumber(String homeNumber) {
        this.homeNumber = homeNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(number);
        dest.writeString(homeNumber);
        dest.writeString(group);
        dest.writeLong(id);
    }

    public static final Creator<Subscriber> CREATOR
            = new Creator<Subscriber>() {
        public Subscriber createFromParcel(Parcel in) {
            return new Subscriber(in);
        }

        public Subscriber[] newArray(int size) {
            return new Subscriber[size];
        }
    };

}
