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

    //Variables defining the table of stored exercises
    public static final String Table_Name = "Exercise_Data";
    public static final String Col_1 = "ID";
    public static final String Col_2 = "Name";
    public static final String Col_3 = "Option_1";
    public static final String Col_4 = "Option_2";
    public static final String Col_5 = "Category";
    public static final String Col_6 = "isEasy";

    //Variables defining the table of saved circuits
    public static final String Save_Table = "SavedCircuits";
    public static final String Save_ID = "ID";
    public static final String Save_Name = "Name";
    public static final String Saved_Circuit = "Circuit_Data";

    //DB Constructor
    public DBHelper(Context context)
    {
        super(context, DB_Name, null, 10);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Table_Name + " (ID Integer primary key autoincrement, Name text, Option_1 text, Option_2 text, Category text, isEasy text)");
        db.execSQL("create table if not exists " + Save_Table + " (ID Integer primary key autoincrement, Name text, Circuit_Data text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists " + Table_Name);
        db.execSQL("drop table if exists " + Save_Table);
        onCreate(db);
    }

    public Cursor CircuitData(String Name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select " + Saved_Circuit + " from " + Save_Table + " where " + Save_Name + " like '" + Name + "'",null);
        return res;
    }

    public Cursor SavedCircuitNames()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select " +Save_Name+ " from " + Save_Table, null);
        return res;
    }

    public void saveCircuit(String Name, String CircuitData)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Save_Name, Name);
        contentValues.put(Saved_Circuit, CircuitData);
        db.insert(Save_Table, null, contentValues);
    }

    public void insertData(String Name, String Option_1, String Option_2, String Category, String isEasy)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_2, Name);
        contentValues.put(Col_3, Option_1);
        contentValues.put(Col_4, Option_2);
        contentValues.put(Col_5, Category);
        contentValues.put(Col_6, isEasy);
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

    public int[] addOnInt(int[] array, int newInt)
    {
        int[] newArray = new int[array.length + 1];
        for(int i=0; i < array.length; i++)
        {
            newArray[i] = array[i];
        }
        newArray[array.length] = newInt;
        return newArray;
    }

    public String newCircuit(boolean isArms, boolean isBackAndShoulders, boolean isCore, boolean isLegs, boolean isOther, int length, boolean isEasy, boolean isHard)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        boolean first = true;
        String[] requiredQueries = new String[1];
        int[] AvailableOptions = new int[1];
        String easyQueryAdd = "";
        if(isEasy && !isHard)
            easyQueryAdd = " AND isEasy Like 'Yes'";
        else if(isHard && !isEasy)
            easyQueryAdd = " AND isEasy Like 'No'";

        if(isArms && first) {
            first = false;
            requiredQueries[0] = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Arms'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1";
            Cursor armCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Arms'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
            armCurs.moveToFirst();
            AvailableOptions[0] = armCurs.getInt(0);
        }
        if(isBackAndShoulders) {
            if(first)
            {
                first = false;
                requiredQueries[0] = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'BackAndShoulders'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1";
                Cursor bsCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'BackAndShoulders'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
                bsCurs.moveToFirst();
                AvailableOptions[0] = bsCurs.getInt(0);
            }
            else
            {
                requiredQueries = addOn(requiredQueries, "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'BackAndShoulders'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1");
                Cursor bsCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'BackAndShoulders'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
                bsCurs.moveToFirst();
                AvailableOptions = addOnInt(AvailableOptions,bsCurs.getInt(0));
            }
        }
        if(isCore) {
            if(first)
            {
                first = false;
                requiredQueries[0] = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Core'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1";
                Cursor cCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Core'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
                cCurs.moveToFirst();
                AvailableOptions[0] = cCurs.getInt(0);
            }
            else{
                requiredQueries = addOn(requiredQueries, "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Core'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1");
                Cursor cCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Core'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
                cCurs.moveToFirst();
                AvailableOptions = addOnInt(AvailableOptions,cCurs.getInt(0));
            }
        }
        if(isLegs) {
            if(first)
            {
                first = false;
                requiredQueries[0] = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Legs'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1";
                Cursor lCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Legs'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
                lCurs.moveToFirst();
                AvailableOptions[0] = lCurs.getInt(0);
            }
            else
            {
                requiredQueries = addOn(requiredQueries, "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Legs'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1");
                Cursor lCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Legs'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
                lCurs.moveToFirst();
                AvailableOptions = addOnInt(AvailableOptions,lCurs.getInt(0));
            }
        }
        if(isOther) {
            if(first){
                first = false;
                requiredQueries[0] = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Other'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1";
                Cursor oCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Other'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
                oCurs.moveToFirst();
                AvailableOptions[0] = oCurs.getInt(0);
            }
            else{
                requiredQueries = addOn(requiredQueries, "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Other'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1");
                Cursor oCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Other'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
                oCurs.moveToFirst();
                AvailableOptions = addOnInt(AvailableOptions,oCurs.getInt(0));
            }

        }

        StringBuffer exRes = new StringBuffer();
        String[] duplicateFinder = new String[length];

        for(int i=0; i < length; i++)
        {
            Cursor res = db.rawQuery(requiredQueries[i%requiredQueries.length], null);
            res.moveToFirst();
            String Name = res.getString(1);
            String[] Options = {res.getString(2),res.getString(3)};
            String Cat = res.getString(4);
            if(Arrays.asList(duplicateFinder).contains(Name) && AvailableOptions[i%AvailableOptions.length] > 0)
            {
                i--;
            }
            else if(AvailableOptions[i%AvailableOptions.length] < 1)
            {
                exRes.append("Insufficient Exercises to fulfill your request, All Available options have been shown");
                return exRes.toString();
            }
            else if(Cat.equalsIgnoreCase("Other") || AvailableOptions[i%AvailableOptions.length] > 0)
            {
                int num = i+1;
                Random rand = new Random();
                int option = rand.nextInt(2);
                exRes.append(num + ": " + Options[option] + " " + Name + "\n\n");
                duplicateFinder[i] = Name;
                AvailableOptions[i%AvailableOptions.length]--;
            }
            else if(AvailableOptions[i%AvailableOptions.length] < 1 && !Cat.equalsIgnoreCase("Other"))
            {
                return "Insufficient Exercise Options \nTry Again with a Smaller Length";
            }
            else
            {
                return "Some Logical Error Occurred";
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
            insertData("Dips", "50", "40", "Arms","Yes");
            insertData("Reverse Dips", "60", "50", "Arms","Yes");
            insertData("Swings to Handstand", "15", "2 x 10", "Arms","Yes");
            insertData("Dip Swings", "15", "15", "Arms","Yes");
            insertData("Dip Swings to Planche", "10", "12", "Arms","Yes");
            insertData("Dip Swings to Handstand","10","8","Arms","No");
            insertData("Chicken Wing Dips","15","18","Arms","No");
            insertData("Pull-ups (Overgrip)","15","10","Arms","Yes");
            insertData("Pull-ups (Undergrip)","15","10","Arms","Yes");
            insertData("Pull-ups (Wide)","15","10","Arms","Yes");
            insertData("Diamond Push-ups","20","30","Arms","Yes");
            insertData("Push-ups","30","40","Arms","Yes");
            insertData("Handstand Push-ups","10","12","Arms","No");
            insertData("Inverted Rows","15","12","Arms","Yes");
            insertData("Rings Push-ups","15","20","Arms","Yes");
            insertData("Reverse push-ups on Rings","15","12","Arms","Yes");
            insertData("Archer Push-ups","20 (10 / side)","24 (12 / side)","Arms","Yes");
            insertData("Archer Push-ups on Rings","14 (7 / side)","12 (6 / side)","Arms","Yes");
            insertData("Dips on Rings","15","20","Arms","No");
            insertData("Rings Support Hold","30 Seconds","35 Seconds","Arms","Yes");
            insertData("Clapping Push-ups","20","30","Arms","Yes");
            insertData("Japanese Handstand Push-ups","5","6","Arms","No");
            insertData("Bent-arm Straight Body Presses","6","5","Arms","No");
            insertData("Ice Cream Makers (Rings)","12","15","Arms","Yes");
            insertData("Ice Cream Makers (Bar)","12","10","Arms","Yes");
            insertData("Side-to-Side Pull-ups","10 (5 / side)","12 (6 / side)","Arms","Yes");
            insertData("Muscle-ups (Bar)","10","8","Arms","No");
            insertData("Muscle-ups (Rings)","10","12","Arms","No");
            insertData("Pull-up Hold","45 Seconds","2 x 30 Seconds","Arms","Yes");
            insertData("Rope Climb","1","1","Arms","No");
            insertData("Chest Roll to Handstand","15","20","Arms","No");
            insertData("Wrist Rollers","Up and Down Each Way Once","2 Minutes Steady Pace Up and Down for","Arms","Yes");
            insertData("Pull-ups on Rope (Vertical Grip)","12","10","Arms","Yes");

            //Back and Shoulders Exercises (35 Total)
            insertData("Arch Hold","1 Minute","1 Minute 10 Seconds","BackAndShoulders","Yes");
            insertData("Arch Rocks","40","45","BackAndShoulders","Yes");
            insertData("Handstand Hold","1 Minute","2 x 40 Seconds","BackAndShoulders","Yes");
            insertData("Single Rail Handstand Hold","45 Seconds","2 x 30 Seconds","BackAndShoulders","No");
            insertData("Tuck Planche Hold","2 x 10 Seconds","3 x 7 Seconds","BackAndShoulders","Yes");
            insertData("Straddle Planche Hold","3 x 3 Seconds","2 x 5 Seconds","BackAndShoulders","No");
            insertData("Planche Push-ups","12","10","BackAndShoulders","Yes");
            insertData("Planche Presses","10","5 (Holding Each Planche for 3 Seconds)","BackAndShoulders","No");
            insertData("Presses","5","6","BackAndShoulders","No");
            insertData("Standing Presses","6","8","BackAndShoulders","No");
            insertData("Endo Roll to Handstand","8","6","BackAndShoulders","No");
            insertData("Back Uprise Handstand","10","8","BackAndShoulders","No");
            insertData("Front Uprise Swing Handstand","10","8","BackAndShoulders","No");
            insertData("Swing to Handstand","10","12","BackAndShoulders","Yes");
            insertData("A-Pivots","6","8","BackAndShoulders","No");
            insertData("Wall Angels","20","25","BackAndShoulders","Yes");
            insertData("Floor Angles","12","10","BackAndShoulders","Yes");
            insertData("Front Lever Pulls","6","5","BackAndShoulders","Yes");
            insertData("Back Lever Pulls","6","5","BackAndShoulders","Yes");
            insertData("Chest Raises","15","20","BackAndShoulders","Yes");
            insertData("Heel Drives","30","25","BackAndShoulders","Yes");
            insertData("Stunted Pump Swings to Planche","10","8","BackAndShoulders","No");
            insertData("Cast to Handstand","10","8","BackAndShoulders","No");
            insertData("Flies (on Pommel with Weights)","12","15","BackAndShoulders","Yes");
            insertData("Jack-Knife Push-Ups on Rings","12","10","BackAndShoulders","Yes");
            insertData("Flies on Rings","6","8","BackAndShoulders","Yes");
            insertData("Crescent Push-ups on Rings","6","8","BackAndShoulders","Yes");
            insertData("Hanging Upside-Down Shrugs","20","25","BackAndShoulders","Yes");
            insertData("Planche Rockers","10","12","BackAndShoulders","Yes");
            insertData("Rings Handstand Hold","40 Seconds","2 x 25 Seconds","BackAndShoulders","No");
            insertData("One Arm Handstand Hold","25 Seconds Each Arm","20 Seconds Each Arm","BackAndShoulders","No");
            insertData("Pommel Sliders","4 Lengths (1 of each style)","4 Lengths (1 of each style)","BackAndShoulders","Yes");
            insertData("Weight Plate Lifts","15","12","BackAndShoulders","Yes");
            insertData("Maltese Presses on Red Blocks","10","8","BackAndShoulders","Yes");
            insertData("Maltese Lifts with Weights","10","12","BackAndShoulders","Yes");

            //Core Exercises (25 Total)
            insertData("Windscreen Wipers","24 (12 / side)","20 (10 / side)","Core","No");
            insertData("Leg Lifts","15","20","Core","Yes");
            insertData("Plank Hold","1 Minute","1 Minute 15 Seconds","Core","Yes");
            insertData("Dish Hold","1 Minute","45 Seconds","Core","Yes");
            insertData("Dish Rocks","60","50","Core","Yes");
            insertData("Sitting Leg Lifts","20","25","Core","Yes");
            insertData("Lying Leg Lifts","20","25","Core","Yes");
            insertData("Side Plank Hold","45 Seconds Each Side","30 Seconds Resisted Each Side","Core","Yes");
            insertData("Inchworm With Sliders","2 Lengths","3 Lengths","Core","Yes");
            insertData("Side Rocks","30 Each Side","25 Each Side","Core","Yes");
            insertData("Extended Plank Hold","30 Seconds","40 Seconds","Core","Yes");
            insertData("Jack Knives","25","30","Core","Yes");
            insertData("Hanging Sit-ups","15","20","Core","Yes");
            insertData("Crunches","30","40","Core","Yes");
            insertData("V-Ups","10","12","Core","No");
            insertData("Bruce Lee Sit-ups","10","12","Core","Yes");
            insertData("Side Leg Lifts","20 Each Side","25 Each Side","Core","Yes");
            insertData("Bicycle Crunches","50","40","Core","Yes");
            insertData("Resisted Dish Hold","40 Seconds","2 x 25 Seconds","Core","Yes");
            insertData("Tuck-ups","30","25","Core","Yes");
            insertData("L-Sit Hold","3 x 10 Seconds","2 x 15 Seconds","Core","Yes");
            insertData("Reverse Sit-ups","25","20","Core","Yes");
            insertData("AbCircuit","2 x (8)s","(10)s","Core","Yes");
            insertData("Side Jack-Knives","20 Each Side","25 Each Side","Core","Yes");
            insertData("Kip Extenders","15","12","Core","Yes");

            //Leg Exercises (23 Total)
            insertData("Lunges","15 Each Leg","20 Each Leg","Legs","Yes");
            insertData("Calf Raises","30-30-30","50","Legs","Yes");
            insertData("Weighted Thrusts","15","12","Legs","Yes");
            insertData("Single Leg Weighted Thrusts","8 Each Leg","6 Each Leg","Legs","Yes");
            insertData("Squats","30","40","Legs","Yes");
            insertData("Weighted Squats","12","15","Legs","Yes");
            insertData("Squat Jumps","18","20","Legs","Yes");
            insertData("Broad Jumps","20","25","Legs","Yes");
            insertData("Lunge Jumps","20","24","Legs","Yes");
            insertData("Box Jumps","10","15","Legs","Yes");
            insertData("Pistol Squats","10 Each Leg","8 Each Leg","Legs","No");
            insertData("Single Leg Roll-ups","10 Each Leg","8 Each Leg","Legs","No");
            insertData("Resistance Runs (25m)","2","3","Legs","Yes");
            insertData("Sled Push","2","3","Legs","Yes");
            insertData("Standing Back Tucks","10","8","Legs","No");
            insertData("Standing Front Tucks","6","8","Legs","No");
            insertData("Depth Jumps","8","6","Legs","No");
            insertData("Weighted Lunge Walks (12m)","3","4","Legs","Yes");
            insertData("Leg Extension","15","20","Legs","Yes");
            insertData("Hamstring Sliders","10 Each Leg","8 Each Leg","Legs","Yes");
            insertData("Single Leg Broad Jumps","10 Each Leg","12 Each Leg","Legs","Yes");
            insertData("Tree-Falls","10","15","Legs","Yes");
            insertData("Toe Raises","35","40","Legs","Yes");

            //Other Exercises (20 Total)
            insertData("Push-up Burpees","","","Other","Yes");
            insertData("Fat Mat Sprint","","","Other","Yes");
            insertData("Skipping","","","Other","Yes");
            insertData("Punch Front Tucks","","","Other","No");
            insertData("Squat Jumps","","","Other","Yes");
            insertData("Push-ups","","","Other","Yes");
            insertData("Dips","","","Other","No");
            insertData("Reverse Dips","","","Other","Yes");
            insertData("Plank","","","Other","Yes");
            insertData("Inverted Rows (Low Rings)","","","Other","Yes");
            insertData("Inverted Rows (P-Bar)","","","Other","Yes");
            insertData("Side-to-Side Bounces Over Rail","","","Other","No");
            insertData("Box Jumps","","","Other","Yes");
            insertData("Standing Back Tucks","","","Other","No");
            insertData("Crunches","","","Other","Yes");
            insertData("Lunges","","","Other","Yes");
            insertData("Calf Raises","","","Other","Yes");
            insertData("Bicycle Crunches","","","Other","Yes");
            insertData("Hanging Tuck-ups","","","Other","Yes");
            insertData("Reverse Sit-ups","","","Other","Yes");
        }
    }
}
