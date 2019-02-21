package com.example.circuitgen;

import android.os.Parcel;
import android.os.Parcelable;

public class CircuitHolder implements Parcelable {
    public String repCount;
    public String name;
    public String description;
    public CircuitHolder next;
    public int isSaved = 0;
    public int isEdited;
    public String saveName;
    public int hideTheButtons = 0;
    public int isHIIT = 0;

    public CircuitHolder(String Name, int Saved, String sName)
    {
        name = Name;
        next = null;
        repCount = null;
        description = null;
        isSaved = Saved;
        saveName = sName;
        isEdited = 0;
    }

    public CircuitHolder(String Name, CircuitHolder Next, int Saved, String sName)
    {
        name = Name;
        next = Next;
        repCount = null;
        description = null;
        isSaved = Saved;
        saveName = sName;
        isEdited = 0;
    }

    public CircuitHolder(String Name, String rep)
    {
        name = Name;
        next = null;
        repCount = rep;
        description = null;
        saveName = null;
        isEdited = 0;
    }
    public CircuitHolder(String Name, String Description, int num)
    {
        name = Name;
        next = null;
        repCount = null;
        description = Description;
        saveName = null;
        isEdited = 0;

    }

    protected CircuitHolder(Parcel in) {
        repCount = in.readString();
        name = in.readString();
        description = in.readString();
        next = in.readParcelable(CircuitHolder.class.getClassLoader());
        isSaved = in.readInt();
        isEdited = in.readInt();
        saveName = in.readString();
        hideTheButtons = in.readInt();
        isHIIT = in.readInt();
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
        dest.writeInt(isEdited);
        dest.writeString(saveName);
        dest.writeInt(hideTheButtons);
        dest.writeInt(isHIIT);
    }

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

    public static CircuitHolder addHIITExercise(CircuitHolder prev, String rep, String Name)
    {
        if(prev == null)
        {
            prev = new CircuitHolder(Name, "");
            prev.isHIIT = 1;
            return prev;
        }
        CircuitHolder last = prev;
        while(last.next != null)
        {
            last = last.next;
        }
        last.next = new CircuitHolder(Name, rep);
        last.isHIIT = 1;
        return prev;
    }
}
