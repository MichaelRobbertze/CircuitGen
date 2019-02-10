package com.example.circuitgen;

import android.os.Parcel;
import android.os.Parcelable;

public class CircuitListItem implements Parcelable {
    public String repCount;
    public String name;
    public String description;
    public CircuitListItem next;
    public int isSaved = 0;

    public CircuitListItem(String Name, int Saved)
    {
        name = Name;
        next = null;
        repCount = null;
        description = null;
        isSaved = Saved;
    }

    public CircuitListItem(String Name, CircuitListItem Next, int Saved)
    {
        name = Name;
        next = Next;
        repCount = null;
        description = null;
        isSaved = Saved;
    }

    public CircuitListItem(String Name, String rep)
    {
        name = Name;
        next = null;
        repCount = rep;
        description = null;
    }
    public CircuitListItem(String Name, String Description, int num)
    {
        name = Name;
        next = null;
        repCount = null;
        description = Description;

    }

    public static CircuitListItem addExercise(CircuitListItem prev, String rep, String Name)
    {
        if(prev == null)
        {
            prev = new CircuitListItem(Name, rep);
            return prev;
        }
        CircuitListItem last = prev;
        while(last.next != null)
        {
            last = last.next;
        }
        last.next = new CircuitListItem(Name, rep);
        return prev;
    }

    protected CircuitListItem(Parcel in) {
        repCount = in.readString();
        name = in.readString();
        description = in.readString();
        next = in.readParcelable(CircuitListItem.class.getClassLoader());
        isSaved = in.readInt();
    }

    public static final Creator<CircuitListItem> CREATOR = new Creator<CircuitListItem>() {
        @Override
        public CircuitListItem createFromParcel(Parcel in) {
            return new CircuitListItem(in);
        }

        @Override
        public CircuitListItem[] newArray(int size) {
            return new CircuitListItem[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(repCount);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeParcelable(next, flags);
        dest.writeInt(isSaved);
    }
}

