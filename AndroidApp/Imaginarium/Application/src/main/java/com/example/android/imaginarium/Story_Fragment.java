package com.example.android.imaginarium;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cristina on 16.05.2015.
 */
public class Story_Fragment implements Parcelable {

    int id;

    String owner;
    String text;

    int has_image, has_video;
    String path_to_img, path_to_video;

    Story_Fragment(int id, String owner, String text,
                   int has_image, int has_video,
                    String path_to_img, String path_to_video) {
        this.id = id;
        this.owner = owner;
        this.text = text;
        this.has_image = has_image;
        this.has_video = has_video;
        if (this.has_image == 1)
            this.path_to_img = path_to_img;
        else
            this.path_to_img = "";

        if (this.has_video == 1)
            this.path_to_video = path_to_video;
        else
            this.path_to_video = "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.owner);
        dest.writeString(this.text);
        dest.writeInt(this.has_image);
        dest.writeInt(this.has_video);
        dest.writeString(this.path_to_img);
        dest.writeString(this.path_to_video);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Story_Fragment createFromParcel(Parcel in) {
            return new Story_Fragment(in.readInt(),
                    in.readString(), in.readString(),
                    in.readInt(), in.readInt(),
                    in.readString(), in.readString());
        }

        public Story_Fragment[] newArray(int size) {
            return new Story_Fragment[size];
        }
    };
}
