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
    public static final String Col_7 = "Description";

    //Variables defining the table of saved circuits
    public static final String Save_Table = "SavedCircuits";
    public static final String Save_ID = "ID";
    public static final String Save_Exercise = "Exercise";
    public static final String Next_Index = "Next_Index";
    public static final String isHead = "isHead";
    public static final String Save_Name = "circuitName";

    //DB Constructor
    public DBHelper(Context context)
    {
        super(context, DB_Name, null, 26);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Table_Name + " (ID Integer primary key autoincrement, Name text, Option_1 text, Option_2 text, Category text, isEasy text, Description text)");
        db.execSQL("create table if not exists " + Save_Table + " (ID Integer primary key autoincrement, Exercise text, Next_Index Integer, isHead text, circuitName text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists " + Table_Name);
        db.execSQL("drop table if exists " + Save_Table);
        onCreate(db);
    }

    public CircuitHolder getExercise(String name)
    {
        CircuitHolder myExercise = null;
        String query = "SELECT * FROM " + Table_Name + " WHERE " + Col_2 + " LIKE '" + name + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor curs = db.rawQuery(query, null);
        if(curs != null)
        {
            curs.moveToFirst();
            String exName = curs.getString(1);
            String exDescription = curs.getString(6);
            myExercise = new CircuitHolder(exName, exDescription, 0);
        }
        return myExercise;
    }

    public void saveCircuit(CircuitHolder myCircuit, String isFirst, int num, String SaveName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        saveCircuit(myCircuit,isFirst,num,db,SaveName);
    }
    public boolean saveCircuit(CircuitHolder myCircuit, String isFirst, int num, SQLiteDatabase db, String SaveName)
    {
        try{
            if(myCircuit != null)
            {
                String myExercise = num + ": " + myCircuit.repCount + " (" + myCircuit.name + ")";

                ContentValues contentValues = new ContentValues();
                contentValues.put(Save_Exercise, myExercise);
                if(myCircuit.next != null)
                {
                    Cursor idCursor = db.rawQuery("SELECT " + Save_ID + " FROM " + Save_Table, null);
                    if(idCursor.getCount() == 0)
                        contentValues.put(Next_Index, 2);
                    else {
                        idCursor.moveToLast();
                        int nextID = idCursor.getInt(0) + 2;
                        contentValues.put(Next_Index, nextID);
                    }
                    idCursor.close();
                }
                else{
                    contentValues.put(Next_Index, 0);
                }
                contentValues.put(isHead, isFirst);
                contentValues.put(Save_Name, SaveName);
                db.insert(Save_Table, null, contentValues);
                saveCircuit(myCircuit.next, "No", num+1, db, SaveName);

            }
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public CircuitHolder getSavedCircuit(String Name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor savedCursor = db.rawQuery("SELECT * FROM " + Save_Table + " WHERE " + Save_Name + " Like '" + Name + "'",null);
        savedCursor.moveToFirst();
        int circLength = savedCursor.getCount();
        String[] myCircEntries = new String[circLength];
        for(int i =0; i<circLength; i++)
        {
            myCircEntries[i] = savedCursor.getString(1);
            savedCursor.moveToNext();
        }
        CircuitHolder mySavedCircuit = CreateLinkedList(null,myCircEntries,0);
        return mySavedCircuit;
    }

//    public Cursor CircuitData(String Name)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor res = db.rawQuery("select " + Saved_Circuit + " from " + Save_Table + " where " + Save_Name + " like '" + Name + "'",null);
//        return res;
//    }

    //recursive method initially passed an empty circuitholder and a string array of all the list entries
    public CircuitHolder CreateLinkedList(CircuitHolder myCircuit, String[] listofstuff, int pos)
    {
        if(pos < listofstuff.length - 1)
        {
            myCircuit = new CircuitHolder(listofstuff[pos], CreateLinkedList(myCircuit, listofstuff, pos+1), 1);
        }
        else if(pos == listofstuff.length - 1)
        {
            myCircuit = new CircuitHolder(listofstuff[pos], 1);
        }
        return myCircuit;
    }
//
    public Cursor SavedCircuitNames()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct " +Save_Name+ " from " + Save_Table, null);
        return res;
    }

//    public void saveCircuit(CircuitHolder myCircuit)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues;
//        boolean isfirst = true;
//        while(myCircuit.next != null)
//        {
//            contentValues = new ContentValues();
//            contentValues.put(Save_Name, myCircuit.name);
//            if(isFirst)
//            {
//                contentValues.put(Next_Save, CircuitData);
//            }
//
//            contentValues.put(isFirst, CircuitData);
//        }
//
//        db.insert(Save_Table, null, contentValues);
//    }

    public void insertData(String Name, String Option_1, String Option_2, String Category, String isEasy, String Description)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_2, Name);
        contentValues.put(Col_3, Option_1);
        contentValues.put(Col_4, Option_2);
        contentValues.put(Col_5, Category);
        contentValues.put(Col_6, isEasy);
        contentValues.put(Col_7, Description);

        db.insert(Table_Name, null, contentValues);
    }

    public Cursor getAllExercises()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + Table_Name, null);
        return res;
    }

    public static String[] addOn(String[] array, String newString)
    {
        String[] newArray = new String[array.length + 1];
        for(int i=0; i < array.length; i++)
        {
            newArray[i] = array[i];
        }
        newArray[array.length] = newString;
        return newArray;
    }

    public static int[] addOnInt(int[] array, int newInt)
    {
        int[] newArray = new int[array.length + 1];
        for(int i=0; i < array.length; i++)
        {
            newArray[i] = array[i];
        }
        newArray[array.length] = newInt;
        return newArray;
    }

    public CircuitHolder newCircuit(boolean isArms, boolean isBackAndShoulders, boolean isCore, boolean isLegs, boolean isOther, int length, boolean isEasy, boolean isHard)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //Set up variables to note whether the exercise is the first n the list or not, all the queries required to get the desired results from the db
        //as well as count the available number of exercises selectable for the current circuit parameters
        boolean first = true;
        String[] requiredQueries = new String[1];
        int[] AvailableOptions = new int[1];
        String easyQueryAdd = "";
        if(isEasy && !isHard)
            easyQueryAdd = " AND isEasy Like 'Yes'";
        else if(isHard && !isEasy)
            easyQueryAdd = " AND isEasy Like 'No'";

        //based on the states of the check boxes, add required queries to string[], as well as count available choices
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

        //use appropriate queries to build exercise list
        CircuitHolder circRes = null;
        String[] duplicateFinder = new String[length];

        for(int i=0; i < length; i++)
        {
            Cursor res = db.rawQuery(requiredQueries[i%requiredQueries.length], null);
            res.moveToFirst();
            String Name = res.getString(1);
            String[] Options = {res.getString(2),res.getString(3)};
            String Cat = res.getString(4);

            //if the random exercise is already in our circuit list
            if(Arrays.asList(duplicateFinder).contains(Name) && AvailableOptions[i%AvailableOptions.length] > 0)
            {
                i--;
            }
            //if there are no more appropriate exercises that we haven't used yet
            else if(AvailableOptions[i%AvailableOptions.length] < 1)
            {
                return new CircuitHolder("Insufficient stored exercises to fulfill your request, select a shorter length circuit and don't hurt yourself please.","");
            }
            //if the exercise is one that we want to add to our circuit
            else if(Cat.equalsIgnoreCase("Other") || AvailableOptions[i%AvailableOptions.length] > 0)
            {
                Random rand = new Random();
                int option = rand.nextInt(2);
                duplicateFinder[i] = Name;
                AvailableOptions[i%AvailableOptions.length]--;
                circRes = CircuitHolder.addExercise(circRes, Options[option], Name);
            }
            //pretty much to catch anything else, a simple try catch situation i guess
            else
            {
                return new CircuitHolder("Some Logical Error Occurred", "");
            }
        }
        return circRes;
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
            insertData("Dips", "50", "40", "Arms","Yes","Keep your body as straight as possible \n\nTry your best to dip down until your shoulders almost touch the bar");
            insertData("Reverse Dips", "60", "50", "Arms","Yes","Ensure you are in a comfortable position and the gap between your hands and feet is not too large \n\nYour back should be very close to the bar when you dip down");
            insertData("Swings to Handstand", "15", "2 x 10", "Arms","Yes","Keep your body straight and swing from the shoulders \n\nThere should always be a straight line down your body, from your shoulders to your feet (So don't bend at the hips) \n\nIt is a common for people to drop the bum and lift the feet in the front swing, so try not to do this by exaggerating pushing your hips out in the front swing");
            insertData("Dip Swings", "15", "15", "Arms","Yes","Ensure your dips and your swings are both strong before trying this one \n\nWhen your feet are going down, you must dip down\n\nWhen your feet are going up, push back up out of the dip");
            insertData("Dip Swings to Planche", "10", "12", "Arms","Yes","Ensure dip swings are strong for this one \n\nTry to momentarily stop in a planche at the back of each dip swing");
            insertData("Dip Swings to Handstand","10","8","Arms","No","Ensure dip swings are strong for this one \n\nPush to a momentary handstand on each back swing");
            insertData("Chicken Wing Dips","15","18","Arms","No","Keep your body straight as possible \n\nDip your shoulders as low as possible before dropping back into upper arm support \n\nThis motion is key in a muscle-up when transitioning from under the bar to over the bar and as such is a good way to train for a muscle-up if that is your goal");
            insertData("Overgrip Pull-ups","15","10","Arms","Yes","Control the movement\nLower yourself down, do not drop down uncontrollably as this could damage your shoulders and such\n\nEnsure you at least get your chin over the bar, I personally try and go a bit higher and get my nipples to the bar on each rep\n\nTry to arch your body and use your back and shoulder muscles to lift yourself up");
            insertData("Undergrip Pull-ups","15","10","Arms","Yes","Control the movement\nLower yourself down, do not drop down uncontrollably as this could damage your shoulders and such\n\nEnsure you at least get your chin over the bar\n\nTry to pike your body slightly so your feet are in front of your body and focus on using the biceps for pulling yourself up");
            insertData("Wide arm Pull-ups","15","10","Arms","Yes","Control the movement\nLower yourself down, do not drop down uncontrollably as this could damage your shoulders and such\n\nEnsure you get your chin over the bar\n\nKeep your whole body straight and pull yourself up using your back and shoulder muscles mostly");
            insertData("Diamond Push-ups","20","30","Arms","Yes","Hands close together with index fingers and thumbs touching to make the shape of a diamond\n\nBring your chest close to your hands on each rep\n\nKeep your body straight");
            insertData("Push-ups","30","40","Arms","Yes","Bring your chest all the way down in between your hands\n\nkeep your body straight");
            insertData("Handstand Push-ups","10","12","Arms","No","Best done with a spotter or against a wall, in my opinion with your stomach towards the wall\n\nTry to keep your body straight and use your shoulders/scapula mostly");
            insertData("Inverted Rows","15","12","Arms","Yes","Try to have your feet just slightly lower than the bar/rings you're hanging on\n\nKeep your body straight, do not bend your hips and let your butt drop down");
            insertData("Rings Push-ups","15","20","Arms","Yes","Turn the rings out - At the top of the pushup try turn your hands so your palms face inward towards eachother\n\nKeep your body straight");
            insertData("Reverse push-ups on Rings","15","12","Arms","Yes","Rings pushups with a reverse grip - palms face forward and elbows are kept in for the entire pushup\n\nKeep your body straight");
            insertData("Archer Push-ups","20 [10 / side]","24 [12 / side]","Arms","Yes","Pushups with hands far apart - In each pushup keep one arm straight and pushup with the other arm\n\nIt's like you're about to fire a bow & arrow");
            insertData("Archer Push-ups on Rings","14 [7 / side]","12 [6 / side]","Arms","Yes","In each pushup keep one arm straight and pushup with the other arm\n\nIt's like you're about to fire a bow & arrow");
            insertData("Dips on Rings","15","20","Arms","No","Try to keep your body straight\n\nTurn the rings out - At the top of the dip try turn your hands so your palms face inward towards eachother");
            insertData("Rings Support Hold","30 Seconds","35 Seconds","Arms","Yes","Turn the rings out - Turn your hands so your palms face inward towards eachother or even a little bit forwards\n\nArms straight, Legs straight, Legs together");
            insertData("Clapping Push-ups","20","30","Arms","Yes","Explosive movements, push up as fast and hard as possible\n\nJust clap once, don't try to be cool");
            insertData("Japanese Handstand Push-ups","5","6","Arms","No","Dip the handstand all the way down first, only then start to move your shoulders forward until your body is flat, then reverse the process\n\nEnsure your handstand push-ups are very strong as well as planches\n\nKeep your body straight\n\nThis one is hard");
            insertData("Bent-arm Straight Body Presses","6","5","Arms","No","From an L-sit, slowly lower your legs until your body is straight, then keep your body straight while you bend your arms and press up to handstand\n\nEnsure you can do a press already");
            insertData("Ice Cream Makers (Rings)","12","15","Arms","Yes","Start hanging, then swing to a front lever position, then pull-up while you swing back down\n\nKeep your body straight\n\nkeep the motion smooth and controlled");
            insertData("Ice Cream Makers (Bar)","12","10","Arms","Yes","Start hanging, pull to a front lever, then pull-up while you swing back down\n\nKeep your body straight");
            insertData("Side-to-Side Pull-ups","10 [5 / side]","12 [6 / side]","Arms","Yes","Try to do mostly a one arm pull-up while keeping your other arm straight and only using it to help you just enough");
            insertData("Muscle-ups on Bar","10","8","Arms","No","Ensure your pull-ups are strong for this one\n\nthe key is getting the shoulders on top of the bar, once you can do that well it's easy");
            insertData("Muscle-ups on Rings","10","12","Arms","No","Keep the rings close together when getting your shoulders on top of the rings\n\nIf you let the rings move apart too much it gets very difficult");
            insertData("Overgrip Pull-up Hold","45 Seconds","2 x 30 Seconds","Arms","Yes","In overgrip, keep yourself at the top of a pull-up with your chin over the bar for the duration\n\nTry to move as little as possible");
            insertData("Undergrip Pull-up Hold","45 Seconds","2 x 30 Seconds","Arms","Yes","In undergrip, keep yourself at the top of a pull-up with your chin over the bar for the duration\n\nTry to move as little as possible");
            insertData("Rope Climb","1","1","Arms","No","Climb a rope somewhere\n\nTry to use only your arms, this of course depends on how experienced you are, you are allowed to use your legs");
            insertData("Chest Roll to Handstand","15","20","Arms","No","You want the surface you're rolling off of to drop off right under your pecks\n\nkeep the arms bent until you get your feet all the way up, then push-up to handstand\nMomentarily hold the handstand and reverse the action back to the starting position");
            insertData("Wrist Rollers","Up and Down Each Way Once","2 Minutes Steady Pace Up and Down for","Arms","Yes","This one requires a unique contraption\nBasically hang weights on a rope from some kind of pipe\nKeep rolling the pipe until all the rope is wrapped around the pipe, then slowly roll it the other way to unravel it\n\nKeep your arms straight out in front of you so that your hands are at shoulder level the entire time");
            insertData("Pull-ups on Rope with Vertical Grip","12","10","Arms","Yes","Squeeze your scapula together at the top of the pull-up");

            //Back and Shoulders Exercises (35 Total)
            insertData("Arch Hold","1 Minute","1 Minute 10 Seconds","BackAndShoulders","Yes","Lying on your stomach keep your arms straight up, like how superman flies\n\nKeep your arms straight and hold your hands as far off the floor as you can for the duration");
            insertData("Arch Rocks","40","45","BackAndShoulders","Yes","Keep your arms straight forward and your hards up off the floor\n\nKeep your feet off the floor and rock back and forth\n\nKeep your rocks small and controlled");
            insertData("Handstand Hold","1 Minute","2 x 40 Seconds","BackAndShoulders","Yes","Your entire body, from your hands to your feet, should be a straight line\n\nLook at the ground but trying to keep the head as forward as possible.\nSo it's like you're rolling your eyes up\n\nDo it against a wall, with a spotter, or simply on your own\n\nYou can use parallets or do it on flat ground.\nParallets will be better for your wrists");
            insertData("Single Rail Handstand Hold","45 Seconds","2 x 30 Seconds","BackAndShoulders","No","Your entire body, from your hands to your feet, should be a straight line\n\nLook at the rail but trying to keep the head as forward as possible.\nSo it's like you're rolling your eyes up\n\nDo it with a spotter helping you\n\nKeep your hands in an overgrip position");
            insertData("Tuck Planche Hold","2 x 10 Seconds","3 x 7 Seconds","BackAndShoulders","Yes","Keep your arms straight\n\nKeep your knees by your chest\n\nDon't lift your butt too high, it should be level with your shoulders");
            insertData("Straddle Planche Hold","3 x 3 Seconds","2 x 5 Seconds","BackAndShoulders","No","Keep your arms and legs straight\n\nOpen your legs as wide as you are able to, the wider you can get your legs the easier the hold will be\n\nMay or may not need a spotter");
            insertData("Planche Push-ups","12","10","BackAndShoulders","Yes","Swing the legs up until your body is flat first, then push your arms straight up to a planche\n\nCan be done straddled (Legs open and straight, easier)\nCan be done straight bodied (Legs straight and together, harder)");
            insertData("Planche Presses","10","5 Holding Each Planche for 3 Seconds","BackAndShoulders","No","Definitely need a spotter for this one\n\nKeep the arms and body straight as you can, only rotate at the shoulders");
            insertData("Presses","5","6","BackAndShoulders","No","Lift your butt all the way up first, only then start to lift your legs up until in a handstand");
            insertData("Standing Presses","6","8","BackAndShoulders","No","Can be done straddled (Legs straight and together, easier)\nCan be done piked (Legs together and straight, harder)\n\nStart with your hands on the floor close to your feet and your legs straight\n\nLift your butt all the way up first, only then start to lift your legs up until in a handstand");
            insertData("Endo Roll to Handstand","8","6","BackAndShoulders","No","The earlier you get your hands on the floor between your legs the easier this move is\n\nKep your legs straight and try to keep your feet off the floor the entire move");
            insertData("Back Uprise Handstand","10","8","BackAndShoulders","No","Try swing the feet all the way up first, so your body's vertical, then push-up to handstand to finish the move\nThen reverse the process\n\nTry and use the momentum from your swing to get your feet all the way up");
            insertData("Front Uprise Swing Handstand","10","8","BackAndShoulders","No","Push the butt and the shoulders forward as you uprise, this will ensure that you have enough swing to get to handstand");
            insertData("Swing to Handstand","10","12","BackAndShoulders","Yes","Keep the arms straight as well as your body\n\nFeet must go as high as they can in the front swing as well\n\nEnsure before trying this move that your swings are strong as well as your handstands\n\nHave a spotter if you are inexperienced");
            insertData("A-Pivots","6","8","BackAndShoulders","No","Start pivoting as you reach the top of the swing, try not to stop in handstand before turning as this creates more opportunity for falling over");
            insertData("Wall Angels","20","25","BackAndShoulders","Yes","Forearms should be vertical and elbows should be as far back as you can get them\n\nExtend all the way up and go all the way down");
            insertData("Floor Angles","12","10","BackAndShoulders","Yes","Use any kind of pole (weights bar, broom stick, random pipe you found) it must just be long enough so you can grip it widely\n\nKeep your hands as far up off the ground as you can\n\nBring the bar all the way to toughing your traps and extend it all the way out until your arms are straight");
            insertData("Front Lever Pulls","6","5","BackAndShoulders","Yes","Use a spotter for your legs\n\nKeep the body straight from shoulders to feet, do not bend your hips and drop your butt down");
            insertData("Back Lever Pulls","6","5","BackAndShoulders","Yes","Use a spotter\n\nKeep your body straight from shoulders to feet");
            insertData("Chest Raises","15","12","BackAndShoulders","Yes","Keep hands behind head and lift until your body is flat, do not go all the way up to an arched position");
            insertData("Heel Drives","30","25","BackAndShoulders","Yes","Kick the legs up fast until your body is flat, do not kick them up above your head into an arched position\n\nKick them up fast, then slowly and controlled lower them down again");
            insertData("Stunted Pump Swings to Planche","10","8","BackAndShoulders","No","Stunted = Stopping your legs from swinging in front of you, this forces you to use strength as it takes away momentum from a swing\n\nEnsure you have good Dip swings already as well as a strong planche");
            insertData("Cast to Handstand","10","8","BackAndShoulders","No","Keep the arms and body straight\n\nBest to have a spotter catch you once you reach handstand\n\nPush away from the bar and land on your feet on the floor unless you are strong enough to control the motion and reverse the cast back onto the bar from handstand");
            insertData("Flies on Pommel with Weights","12","15","BackAndShoulders","Yes","Keep the arms straight\n\nsqueeze the scapula together at the top and lower slowly");
            insertData("Jack-Knife Push-Ups on Rings","12","10","BackAndShoulders","Yes","Can be done on the knees instead of the feet to make it easier\n\nKeep your core tight and the rings close together as you do the move\n\nOnly go until you're flat, dont go any lower");
            insertData("Flies on Rings","6","8","BackAndShoulders","Yes","Can be done on your knees to make it easier\n\nA spotter helping is always nice\n\nArms straight out next to you and back up\n\nTry have straight arms however this puts major strain on the elbows and as such I do these with my arms bent ever so slightly");
            insertData("Crescent Push-ups on Rings","6","8","BackAndShoulders","Yes","Aim to keep your body flat while moving your hands from out in front of you all the way back next to your hips with your arms straight\n\nit is hard to get them all the way back there, so just get them as far back as you can and push back up\n\nArms are straight this entire move");
            insertData("Hanging Upside-Down Shrugs","20","25","BackAndShoulders","Yes","Keep the body as straight as you can and lift your body up and down by shrugging your shoulders\n\nSomeone can stand above you and spot your feet if you have too much trouble balancing while doing it");
            insertData("Planche Rockers","10","12","BackAndShoulders","Yes","Keep the body straight as well as the arms\n\nHave the spotter let you forward as far as you can manage and then pull you back, talk to the spotter so they know where your limit is");
            insertData("Reverse Planche Rockers","10","12","BackAndShoulders","Yes","Keep the body straight as well as the arms\n\nHave the spotter let you back as far as you can manage and then pull you back, talk to the spotter so they know where your limit is");
            insertData("Rings Handstand Hold","40 Seconds","2 x 25 Seconds","BackAndShoulders","No","Keep the rings close together and turned out (Palms facing each other)\n\nYou can keep your feet on the rings cable but try keep your shoulders off the cable/straps");
            insertData("One Arm Handstand Hold","25 Seconds Each Arm","20 Seconds Each Arm","BackAndShoulders","No","Do it against a wall or with a spotter\n\nBest done using a parallet/handle so as to not hurt the wrist\n\nKeep the other arm by your side");
            insertData("Pommel Sliders","4 Lengths [1 of each style]","4 Lengths [1 of each style]","BackAndShoulders","Yes","Keep the body straight as possible and walk the arms forward with your feet on a slider\n\nIf you don't have sliders then I really don't know");
            insertData("Weight Plate Lifts","15","12","BackAndShoulders","Yes","Keep the arms straight and isolate the movement to the shoulders while keeping your core tight\n\n10-15kg is a good weight, 20kg is possible");
            insertData("Maltese Presses on Red Blocks","10","8","BackAndShoulders","Yes","Ensure you have a spotter so you can move through the entire motion\n\nkeep the body straight and lift yourself up by pushing with your arms\n\nDon't stick your butt out to help you get up, that's what the spotter's for");
            insertData("Maltese Lifts with Weights","10","12","BackAndShoulders","Yes","Keep your core tight and legs and arms straight\n\nLower weights down next to your side and then bring them back up and together above your legs\n\nTry have straight arms however this puts major strain on the elbows and as such I do these with my arms bent ever so slightly");

            //Core Exercises (25 Total)
            insertData("Windscreen Wipers","24 [12 / side]","20 [10 / side]","Core","No","test");
            insertData("Leg Lifts","15","20","Core","Yes","test");
            insertData("Plank Hold","1 Minute","1 Minute 15 Seconds","Core","Yes","test");
            insertData("Dish Hold","1 Minute","45 Seconds","Core","Yes","test");
            insertData("Dish Rocks","60","50","Core","Yes","test");
            insertData("Sitting Leg Lifts","20","25","Core","Yes","test");
            insertData("Lying Leg Lifts","20","25","Core","Yes","test");
            insertData("Side Plank Hold","45 Seconds Each Side","30 Seconds Resisted Each Side","Core","Yes","test");
            insertData("Inchworm With Sliders","2 Lengths","3 Lengths","Core","Yes","test");
            insertData("Side Rocks","30 Each Side","25 Each Side","Core","Yes","test");
            insertData("Extended Plank Hold","30 Seconds","40 Seconds","Core","Yes","test");
            insertData("Jack Knives","25","30","Core","Yes","test");
            insertData("Hanging Sit-ups","15","20","Core","Yes","test");
            insertData("Crunches","30","40","Core","Yes","test");
            insertData("V-Ups","10","12","Core","No","test");
            insertData("Bruce Lee Sit-ups","10","12","Core","Yes","test");
            insertData("Side Leg Lifts","20 Each Side","25 Each Side","Core","Yes","test");
            insertData("Bicycle Crunches","50","40","Core","Yes","test");
            insertData("Resisted Dish Hold","40 Seconds","2 x 25 Seconds","Core","Yes","test");
            insertData("Tuck-ups","30","25","Core","Yes","test");
            insertData("L-Sit Hold","3 x 10 Seconds","2 x 15 Seconds","Core","Yes","test");
            insertData("Reverse Sit-ups","25","20","Core","Yes","test");
            insertData("AbCircuit","2 x 8s","10s","Core","Yes","test");
            insertData("Side Jack-Knives","20 Each Side","25 Each Side","Core","Yes","test");
            insertData("Kip Extenders","15","12","Core","Yes","test");

            //Leg Exercises (23 Total)
            insertData("Lunges","15 Each Leg","20 Each Leg","Legs","Yes","test");
            insertData("Calf Raises","30-30-30","50","Legs","Yes","test");
            insertData("Weighted Thrusts","15","12","Legs","Yes","test");
            insertData("Single Leg Weighted Thrusts","8 Each Leg","6 Each Leg","Legs","Yes","test");
            insertData("Squats","30","40","Legs","Yes","test");
            insertData("Weighted Squats","12","15","Legs","Yes","test");
            insertData("Squat Jumps","18","20","Legs","Yes","test");
            insertData("Broad Jumps","20","25","Legs","Yes","test");
            insertData("Lunge Jumps","20","24","Legs","Yes","test");
            insertData("Box Jumps","10","15","Legs","Yes","test");
            insertData("Pistol Squats","10 Each Leg","8 Each Leg","Legs","No","test");
            insertData("Single Leg Roll-ups","10 Each Leg","8 Each Leg","Legs","No","test");
            insertData("Resistance Runs 25m","2","3","Legs","Yes","test");
            insertData("Sled Push","2","3","Legs","Yes","test");
            insertData("Standing Back Tucks","10","8","Legs","No","test");
            insertData("Standing Front Tucks","6","8","Legs","No","test");
            insertData("Depth Jumps","8","6","Legs","No","test");
            insertData("Weighted Lunge Walks 12m","3","4","Legs","Yes","test");
            insertData("Leg Extension","15","20","Legs","Yes","test");
            insertData("Hamstring Sliders","10 Each Leg","8 Each Leg","Legs","Yes","test");
            insertData("Single Leg Broad Jumps","10 Each Leg","12 Each Leg","Legs","Yes","test");
            insertData("Tree-Falls","10","15","Legs","Yes","test");
            insertData("Toe Raises","35","40","Legs","Yes","test");

            //Other Exercises (20 Total)
            insertData("Push-up Burpees","","","Other","Yes","test");
            insertData("Fat Mat Sprint","","","Other","Yes","test");
            insertData("Skipping","","","Other","Yes","test");
            insertData("Punch Front Tucks","","","Other","No","test");
            insertData("Squat Jumps","","","Other","Yes","test");
            insertData("Push-ups","","","Other","Yes","test");
            insertData("Dips","","","Other","No","test");
            insertData("Reverse Dips","","","Other","Yes","test");
            insertData("Plank","","","Other","Yes","test");
            insertData("Inverted Rows on Low Rings","","","Other","Yes","test");
            insertData("Inverted Rows on P-Bar","","","Other","Yes","test");
            insertData("Side-to-Side Bounces Over Rail","","","Other","No","test");
            insertData("Box Jumps","","","Other","Yes","test");
            insertData("Standing Back Tucks","","","Other","No","test");
            insertData("Crunches","","","Other","Yes","test");
            insertData("Lunges","","","Other","Yes","test");
            insertData("Calf Raises","","","Other","Yes","test");
            insertData("Bicycle Crunches","","","Other","Yes","test");
            insertData("Hanging Tuck-ups","","","Other","Yes","test");
            insertData("Reverse Sit-ups","","","Other","Yes","test");
        }
    }
}
