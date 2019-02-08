package com.example.circuitgen;

import android.os.Parcel;
import android.os.Parcelable;

public class CircuitHolder implements Parcelable {
    public String repCount;
    public String name;
    public String description;
    public CircuitHolder next;

    public CircuitHolder(String Name, String rep)
    {
        name = Name;
        next = null;
        repCount = rep;
        description = null;
    }
    public CircuitHolder(String Name, String Description, int num)
    {
        name = Name;
        next = null;
        repCount = null;
        description = Description;

    }

    protected CircuitHolder(Parcel in) {
        repCount = in.readString();
        name = in.readString();
        next = in.readParcelable(CircuitHolder.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(repCount);
        dest.writeString(name);
        dest.writeParcelable(next, flags);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircuitHolder> CREATOR = new Creator<CircuitHolder>() {
        @Override
        public CircuitHolder createFromParcel(Parcel in) {
            return new CircuitHolder(in);
        }

        @Override
        public CircuitHolder[] newArray(int size) {
            return new CircuitHolder[size];
        }
    };

    public static CircuitHolder addExercise(CircuitHolder prev, String rep, String Name)
    {
        if(prev == null)
        {
            prev = new CircuitHolder(Name, rep);
            return prev;
        }
        CircuitHolder last = prev;
        while(last.next != null)
        {
            last = last.next;
        }
        last.next = new CircuitHolder(Name, rep);
        return prev;
    }
}
