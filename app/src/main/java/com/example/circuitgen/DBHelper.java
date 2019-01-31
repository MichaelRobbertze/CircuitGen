package com.example.circuitgen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;
import java.util.Random;

public class DBHelper extends SQLiteOpenHelper {

    //Variables defining the Database
    public static final String DB_Name = "Exercises.db";
    public static final String Table_Name = "Exercise_Data";
    public static final String Col_1 = "ID";
    public static final String Col_2 = "Name";
    public static final String Col_3 = "Option_1";
    public static final String Col_4 = "Option_2";
    public static final String Col_5 = "Category";

    //DB Constructor
    public DBHelper(Context context)
    {
        super(context, DB_Name, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Table_Name + " (ID Integer primary key autoincrement, Name text, Option_1 text, Option_2 text, Category text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists " + Table_Name);
        onCreate(db);
    }

    public void insertData(String Name, String Option_1, String Option_2, String Category)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_2, Name);
        contentValues.put(Col_3, Option_1);
        contentValues.put(Col_4, Option_2);
        contentValues.put(Col_5, Category);
        db.insert(Table_Name, null, contentValues);
    }

    public Cursor getAllExercises()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + Table_Name, null);
        return res;
    }

    public String[] addOn(String[] array, String newString)
    {
        String[] newArray = new String[array.length + 1];
        for(int i=0; i < array.length; i++)
        {
            newArray[i] = array[i];
        }
        newArray[array.length] = newString;
        return newArray;
    }

    public String newCircuit(boolean isArms, boolean isBackAndShoulders, boolean isCore, boolean isLegs, boolean isOther, int length)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //String totalCount = "SELECT count(*) FROM " + Table_Name;
        String CounterString = "SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like ";
        String RequestQuery = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like ";
        String[] CatagoryPointer = new String[1];
        boolean first = true;
        if(isArms && first) {
            CounterString += "'Arms'";
            RequestQuery += "'Arms'";
            CatagoryPointer[0] = "Arms";
            first = false;
        }
        if(isBackAndShoulders && first) {
            CounterString += "'BackAndShoulders'";
            RequestQuery += "'BackAndShoulders'";
            CatagoryPointer[0] = "BackAndShoulders";
            first = false;
        }
        if(isCore && first) {
            CounterString += "'Core'";
            RequestQuery += "'Core'";
            CatagoryPointer[0] = "Core";
            first = false;
        }
        if(isLegs && first) {
            CounterString += "'Legs'";
            RequestQuery += "'Legs'";
            CatagoryPointer[0] = "Legs";
            first = false;
        }
        if(isOther && first) {
            CounterString += "'Other'";
            RequestQuery += "'Other'";
            CatagoryPointer[0] = "Other";
            first = false;
        }
        if(isBackAndShoulders && !first) {
            CounterString += " OR " + Col_5 +" Like 'BackAndShoulders'";
            RequestQuery += " OR " + Col_5 +" Like 'BackAndShoulders'";
            CatagoryPointer = addOn(CatagoryPointer, "BackAndShoulders");
        }
        if(isCore && !first) {
            CounterString += " OR " + Col_5 +" Like 'Core'";
            RequestQuery += " OR " + Col_5 +" Like 'Core'";
            CatagoryPointer = addOn(CatagoryPointer, "Core");
        }
        if(isLegs && !first) {
            CounterString += " OR " + Col_5 +" Like 'Legs'";
            RequestQuery += " OR " + Col_5 +" Like 'Legs'";
            CatagoryPointer = addOn(CatagoryPointer, "Legs");
        }
        if(isOther && !first) {
            CounterString += " OR " + Col_5 +" Like 'Other'";
            RequestQuery += " OR " + Col_5 +" Like 'Other'";
            CatagoryPointer = addOn(CatagoryPointer, "Other");
        }

        RequestQuery += " ORDER BY RANDOM() LIMIT 1";
        Cursor myCursor = db.rawQuery(CounterString, null);
        myCursor.moveToFirst();
        int AvailableNumber = myCursor.getInt(0);
        if(AvailableNumber < length)
        {
            return "Insufficient Matching Exercises, you wanted " + length + " but only " + AvailableNumber + " Recorded Exercises match your requirements \nSorry";
        }
        StringBuffer exRes = new StringBuffer();
        String[] duplicateFinder = new String[length];

        for(int i=0; i<length; i++)
        {
            String reqCat = CatagoryPointer[i % CatagoryPointer.length];
            Cursor Results = db.rawQuery(RequestQuery, null);
            Results.moveToFirst();
            String Name = Results.getString(1);
            String[] Options = {Results.getString(2),Results.getString(3)};
            String Cat = Results.getString(4);
            Random rand = new Random();
            int option = rand.nextInt(2);
            if(Arrays.asList(duplicateFinder).contains(Name) || !Cat.equalsIgnoreCase(reqCat))
                i--;
            else {
                int num = i+1;
                exRes.append(num + ": " + Options[option] + " " + Name + "\n\n");
                duplicateFinder[i] = Name;
            }
        }
        return exRes.toString();
    }

    public void populate()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + Table_Name;
        Cursor myCursor = db.rawQuery(count, null);
        myCursor.moveToFirst();
        int iCount = myCursor.getInt(0);
        if(iCount > 0)
            return;
        else {
            //Arms Exercises (33 Total)
            insertData("Dips", "50", "40", "Arms");
            insertData("Reverse Dips", "60", "50", "Arms");
            insertData("Swings to Handstand", "15", "2 x 10", "Arms");
            insertData("Dip Swings", "15", "15", "Arms");
            insertData("Dip Swings to Planche", "10", "12", "Arms");
            insertData("Dip Swings to Handstand","10","8","Arms");
            insertData("Chicken Wing Dips","15","18","Arms");
            insertData("Pull-ups (Overgrip)","15","10","Arms");
            insertData("Pull-ups (Undergrip)","15","10","Arms");
            insertData("Pull-ups (Wide)","15","10","Arms");
            insertData("Diamond Push-ups","20","30","Arms");
            insertData("Push-ups","30","40","Arms");
            insertData("Handstand Push-ups","10","12","Arms");
            insertData("Inverted Rows","15","12","Arms");
            insertData("Rings Push-ups","15","20","Arms");
            insertData("Reverse push-ups on Rings","15","12","Arms");
            insertData("Archer Push-ups","20 (10 / side)","24 (12 / side)","Arms");
            insertData("Archer Push-ups on Rings","14 (7 / side)","12 (6 / side)","Arms");
            insertData("Dips on Rings","15","20","Arms");
            insertData("Rings Support Hold","30 Seconds","35 Seconds","Arms");
            insertData("Clapping Push-ups","20","30","Arms");
            insertData("Japanese Handstand Push-ups","5","6","Arms");
            insertData("Bent-arm Straight Body Presses","6","5","Arms");
            insertData("Ice Cream Makers (Rings)","12","15","Arms");
            insertData("Ice Cream Makers (Bar)","12","10","Arms");
            insertData("Side-to-Side Pull-ups","10 (5 / side)","12 (6 / side)","Arms");
            insertData("Muscle-ups (Bar)","10","8","Arms");
            insertData("Muscle-ups (Rings)","10","12","Arms");
            insertData("Pull-up Hold","45 Seconds","2 x 30 Seconds","Arms");
            insertData("Rope Climb","1","1","Arms");
            insertData("Chest Roll to Handstand","15","20","Arms");
            insertData("Wrist Rollers","Up and Down Each Way Once","2 Minutes Steady Pace Up and Down for","Arms");
            insertData("Pull-ups on Rope (Vertical Grip)","12","10","Arms");

            //Back and Shoulders Exercises (35 Total)
            insertData("Arch Hold","1 Minute","1 Minute 10 Seconds","BackAndShoulders");
            insertData("Arch Rocks","40","45","BackAndShoulders");
            insertData("Handstand Hold","1 Minute","2 x 40 Seconds","BackAndShoulders");
            insertData("Single Rail Handstand Hold","45 Seconds","2 x 30 Seconds","BackAndShoulders");
            insertData("Tuck Planche Hold","2 x 10 Seconds","3 x 7 Seconds","BackAndShoulders");
            insertData("Straddle Planche Hold","3 x 3 Seconds","2 x 5 Seconds","BackAndShoulders");
            insertData("Planche Push-ups","12","10","BackAndShoulders");
            insertData("Planche Presses","10","5 (Holding Each Planche for 3 Seconds)","BackAndShoulders");
            insertData("Presses","5","6","BackAndShoulders");
            insertData("Standing Presses","6","8","BackAndShoulders");
            insertData("Endo Roll to Handstand","8","6","BackAndShoulders");
            insertData("Back Uprise Handstand","10","8","BackAndShoulders");
            insertData("Front Uprise Swing Handstand","10","8","BackAndShoulders");
            insertData("Swing to Handstand","10","12","BackAndShoulders");
            insertData("A-Pivots","6","8","BackAndShoulders");
            insertData("Wall Angels","20","25","BackAndShoulders");
            insertData("Floor Angles","12","10","BackAndShoulders");
            insertData("Front Lever Pulls","6","5","BackAndShoulders");
            insertData("Back Lever Pulls","6","5","BackAndShoulders");
            insertData("Chest Raises","15","20","BackAndShoulders");
            insertData("Heel Drives","30","25","BackAndShoulders");
            insertData("Stunted Pump Swings to Planche","10","8","BackAndShoulders");
            insertData("Cast to Handstand","10","8","BackAndShoulders");
            insertData("Flies (on Pommel with Weights)","12","15","BackAndShoulders");
            insertData("Jack-Knife Push-Ups on Rings","12","10","BackAndShoulders");
            insertData("Flies on Rings","6","8","BackAndShoulders");
            insertData("Crescent Push-ups on Rings","6","8","BackAndShoulders");
            insertData("Hanging Upside-Down Shrugs","20","25","BackAndShoulders");
            insertData("Planche Rockers","10","12","BackAndShoulders");
            insertData("Rings Handstand Hold","40 Seconds","2 x 25 Seconds","BackAndShoulders");
            insertData("One Arm Handstand Hold","25 Seconds Each Arm","20 Seconds Each Arm","BackAndShoulders");
            insertData("Pommel Sliders","4 Lengths (1 of each style)","4 Lengths (1 of each style)","BackAndShoulders");
            insertData("Weight Plate Lifts","15","12","BackAndShoulders");
            insertData("Maltese Presses on Red Blocks","10","8","BackAndShoulders");
            insertData("Maltese Lifts with Weights","10","12","BackAndShoulders");

            //Core Exercises (25 Total)
            insertData("Windscreen Wipers","24 (12 / side)","20 (10 / side)","Core");
            insertData("Leg Lifts","15","20","Core");
            insertData("Plank Hold","1 Minute","1 Minute 15 Seconds","Core");
            insertData("Dish Hold","1 Minute","45 Seconds","Core");
            insertData("Dish Rocks","60","50","Core");
            insertData("Sitting Leg Lifts","20","25","Core");
            insertData("Lying Leg Lifts","20","25","Core");
            insertData("Side Plank Hold","45 Seconds Each Side","30 Seconds Resisted Each Side","Core");
            insertData("Inchworm With Sliders","2 Lengths","3 Lengths","Core");
            insertData("Side Rocks","30 Each Side","25 Each Side","Core");
            insertData("Extended Plank Hold","30 Seconds","40 Seconds","Core");
            insertData("Jack Knives","25","30","Core");
            insertData("Hanging Sit-ups","15","20","Core");
            insertData("Crunches","30","40","Core");
            insertData("V-Ups","10","12","Core");
            insertData("Bruce Lee Sit-ups","10","12","Core");
            insertData("Side Leg Lifts","20 Each Side","25 Each Side","Core");
            insertData("Bicycle Crunches","50","40","Core");
            insertData("Resisted Dish Hold","40 Seconds","2 x 25 Seconds","Core");
            insertData("Tuck-ups","30","25","Core");
            insertData("L-Sit Hold","3 x 10 Seconds","2 x 15 Seconds","Core");
            insertData("Reverse Sit-ups","25","20","Core");
            insertData("AbCircuit","2 x (8)s","(10)s","Core");
            insertData("Side Jack-Knives","20 Each Side","25 Each Side","Core");
            insertData("Kip Extenders","15","12","Core");

            //Leg Exercises (23 Total)
            insertData("Lunges","15 Each Leg","20 Each Leg","Legs");
            insertData("Calf Raises","30-30-30","50","Legs");
            insertData("Weighted Thrusts","15","12","Legs");
            insertData("Single Leg Weighted Thrusts","8 Each Leg","6 Each Leg","Legs");
            insertData("Squats","30","40","Legs");
            insertData("Weighted Squats","12","15","Legs");
            insertData("Squat Jumps","18","20","Legs");
            insertData("Broad Jumps","20","25","Legs");
            insertData("Lunge Jumps","20","24","Legs");
            insertData("Box Jumps","10","15","Legs");
            insertData("Pistol Squats","10 Each Leg","8 Each Leg","Legs");
            insertData("Single Leg Roll-ups","10 Each Leg","8 Each Leg","Legs");
            insertData("Resistance Runs (25m)","2","3","Legs");
            insertData("Sled Push","2","3","Legs");
            insertData("Standing Back Tucks","10","8","Legs");
            insertData("Standing Front Tucks","6","8","Legs");
            insertData("Depth Jumps","8","6","Legs");
            insertData("Weighted Lunge Walks (12m)","3","4","Legs");
            insertData("Leg Extension","15","20","Legs");
            insertData("Hamstring Sliders","10 Each Leg","8 Each Leg","Legs");
            insertData("Single Leg Broad Jumps","10 Each Leg","12 Each Leg","Legs");
            insertData("Tree-Falls","10","15","Legs");
            insertData("Toe Raises","35","40","Legs");
            insertData("Punch Front Tucks","10","8","Other");

            //Other Exercises (3 Total)
            insertData("Push-up Burpees","20","30","Other");
            insertData("Fat Mat Sprint","Time","Time","Other");
            insertData("Skipping","However Long","However Long","Other");
        }
    }
}
