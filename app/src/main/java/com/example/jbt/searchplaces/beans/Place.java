package com.example.jbt.searchplaces.beans;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

//To allow custom object to be parsed to another component I am using Parcelable(to  pass a customer objects via a Bundle)
public class Place implements Parcelable {


    //the id hold the primary key of the table automatically created

    private long id;

    //the values of these field are entered by the user

    private String name, address;
    private float lat, lng;
    private Bitmap pic;


    public Place(){

    }
    //create constructor included id

    public Place(long id, String name, String address, float lat, float lng, Bitmap pic) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.pic = pic;

    }


    //create parameterized constructor will be used to add value to the table here
    //note the id field is not considered here because is automatically created
    public Place(String name, String address, float lat, float lng, Bitmap pic) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.pic = pic;
    }


        // Parcelling part

    protected Place(Parcel in) {
        id = in.readLong();
        name = in.readString();
        address = in.readString();
        lat = in.readFloat();
        lng = in.readFloat();
        pic = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };


    //create the getters and setters for all instances variables

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }


    //override methods of Parcelable Interface
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeFloat(lat);
        parcel.writeFloat(lng);
        parcel.writeParcelable(pic, i);
    }


        //represent the Share intent section that will share with another device/user

    @Override
    public String toString() {
        return "Place " +
                "name is: " + name + '\'' +
                ", address: " + address + '\'' +
                ", picture: " + pic ;
    }
}
