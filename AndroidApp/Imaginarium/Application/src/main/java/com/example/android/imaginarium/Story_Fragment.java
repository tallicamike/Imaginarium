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

    int has_media;
    String path_to_media;

    Story_Fragment(int id, String owner, String text,
                   int has_media, String path_to_media) {
        this.id = id;
        this.owner = owner;
        this.text = text;
        this.has_media = has_media;
        if (this.has_media == 1)
            this.path_to_media = path_to_media;
        else
            this.path_to_media = "";
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
        dest.writeInt(this.has_media);
        dest.writeString(this.path_to_media);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Story_Fragment createFromParcel(Parcel in) {
            return new Story_Fragment(in.readInt(),
                    in.readString(), in.readString(),
                    in.readInt(),
                    in.readString());
        }

        public Story_Fragment[] newArray(int size) {
            return new Story_Fragment[size];
        }
    };
}
