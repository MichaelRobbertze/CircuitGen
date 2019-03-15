package com.example.circuitgen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

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
    public static final String Col_8 = "Home";
    public static final String Col_9 = "Unwanted";
    public static final String Col_10 = "isHIIT";

    //Variables defining the table of saved circuits
    public static final String Save_Table = "SavedCircuits";
    public static final String Save_ID = "ID";
    public static final String Save_Exercise = "Exercise";
    public static final String Next_Index = "Next_Index";
    public static final String isHead = "isHead";
    public static final String Save_Name = "circuitName";

    //Variables defining the Weekly schedule
    public static String Schedule_Table = "Schedule_Table";
    public static String DayID = "ID";
    public static String DayOfTheWeek = "DayOfTheWeek";
    public static String isArms = "isArms";
    public static String isBAS = "isBAS";
    public static String isCore = "isCore";
    public static String isLegs = "isLegs";
    public static String isHIIT = "isHIIT";

    //Variables defining the Unwanted exercises table
    public static final String Unwanted_Table = "UnwantedExercises";
    public static final String unwantedName = "Name";

    //DB Constructor
    public DBHelper(Context context) {
        super(context, DB_Name, null, 41);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Table_Name + " (ID Integer primary key autoincrement, Name text, Option_1 text, Option_2 text, Category text, isEasy text, Description text, Home text, Unwanted text, isHIIT text)");
        db.execSQL("create table if not exists " + Save_Table + " (ID Integer primary key autoincrement, Exercise text, Next_Index Integer, isHead text, circuitName text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Schedule_Table + " (ID Integer PRIMARY KEY AUTOINCREMENT, DayOfTheWeek text, isArms text, isBAS text, isCore text, isLegs text, isHIIT text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Unwanted_Table + "(ID Integer PRIMARY KEY AUTOINCREMENT, Name text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Table_Name);
//        db.execSQL("drop table if exists " + Save_Table);
//        db.execSQL("DROP TABLE IF EXISTS " + Schedule_Table);
        onCreate(db);
    }

    public String[] getRepOptions(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + Col_3 + "," + Col_4 + " FROM " + Table_Name + " WHERE " + Col_2 + " LIKE '" + name + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        String[] res = new String[]{cursor.getString(0),cursor.getString(1)};
        return res;
    }

    public void editSave(String Original, String savName, String newName, String newReps, int num)
    {
        num = num + 1;
        SQLiteDatabase db = this.getWritableDatabase();
        String myExercise = num + ": " + newReps + " (" + newName + ")";
//        String query = "UPDATE " + Save_Table + " SET " + Save_Exercise + " = '" + myExercise + "' WHERE " + Save_Name + " LIKE '" + circuit.saveName + "' AND " + Save_Exercise + " LIKE '" + circuit.name + "'";
//        db.rawQuery(query, null);
        ContentValues cv = new ContentValues();
        cv.put(Save_Exercise, myExercise);
        db.update(Save_Table, cv, "circuitName=? AND Exercise=?", new String[]{savName, Original});
        return;
    }

    public String replaceExercise(int num, String currEx, boolean isHIIT)
    {
        SQLiteDatabase db = this.getWritableDatabase();
//        int num = Integer.parseInt(currEx.substring(0,1));
        num++;
        char[] currExChar = currEx.toCharArray();
        currEx = getExName(currExChar);
        String category = getExCategory(currEx);
        String query = "";
        if(isHIIT)
            query = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like '" + category + "' AND " + Col_6 + " LIKE 'Yes' AND " + Col_9 + " LIKE 'No' AND " + Col_10 + " Like 'Yes' ORDER BY RANDOM() LIMIT 1";
        else
            query = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like '" + category + "' AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        Random rand = new Random();
        int option = rand.nextInt(2);
        cursor.moveToFirst();
        String EName = cursor.getString(1);
        String[] options = new String[]{cursor.getString(2), cursor.getString(3)};

        if(isHIIT)
            return num + ":  (" + EName + ")";
        else
            return num + ": " + options[option] + " (" + EName + ")";


    }

    public String[] getExerciseOptionsList(String egName)
    {
        String name;
        SQLiteDatabase db = this.getWritableDatabase();
        String category = getExCategory(egName);
        String[] res = new String[]{egName};
        Cursor cursor = db.rawQuery("SELECT " + Col_2 + " FROM " + Table_Name + " WHERE " + Col_5 + " LIKE '" + category + "' ORDER BY " + Col_2,null);
        while(cursor.moveToNext())
        {
            name = cursor.getString(0);
            if(!name.equalsIgnoreCase(egName))
            {
                res = addOn(res, name);
            }
        }
        return res;
    }

    public String getExCategory(String Name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + Col_5 + " FROM " + Table_Name + " WHERE " + Col_2 + " Like '" + Name + "'",null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public boolean isScheduleSet() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + Schedule_Table;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        return cursor.getString(2) != null;
    }

    public String ScheduleString(boolean[] arr) {
        String res = "";
        String[] strings = new String[]{"Arms & ", "Back/Shoulders & ", "Core & ", "Legs & ", "HIIT & "};
        for (int i = 0; i < arr.length; i++) {
            if (arr[i])
                res += strings[i];
        }
        if (res == "")
            if (isScheduleSet())
                res = "Rest Day & ";
            else
                res = "Not yet programmed & ";

        res = res.substring(0, res.length() - 3);
        return res;
    }

    public String getWeeksSchedule() {
        String res = "", theDay;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + Schedule_Table;
        boolean[] resArr = new boolean[5];
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            theDay = cursor.getString(1);
            resArr[0] = StringToBool(cursor.getString(2));
            resArr[1] = StringToBool(cursor.getString(3));
            resArr[2] = StringToBool(cursor.getString(4));
            resArr[3] = StringToBool(cursor.getString(5));
            resArr[4] = StringToBool(cursor.getString(6));
            res += theDay + ": " + ScheduleString(resArr) + "\n\n";
        }
        return res;
    }

    public boolean[] getDaysSchedule(String Day) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + Schedule_Table + " WHERE " + DayOfTheWeek + " LIKE '" + Day + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
//      booleanArray[5] = {isArms, isBAS, isCore, isLegs, isHIIT}
        boolean[] res = new boolean[5];
        res[0] = StringToBool(cursor.getString(2));
        res[1] = StringToBool(cursor.getString(3));
        res[2] = StringToBool(cursor.getString(4));
        res[3] = StringToBool(cursor.getString(5));
        res[4] = StringToBool(cursor.getString(6));
        return res;
    }

    public void setDaysSchedule(String Day, boolean isArmsDay, boolean isBASDay, boolean isCoreDay, boolean isLegsDay, boolean isHIITDay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DayOfTheWeek, Day);
        cv.put(isArms, boolToString(isArmsDay));
        cv.put(isBAS, boolToString(isBASDay));
        cv.put(isCore, boolToString(isCoreDay));
        cv.put(isLegs, boolToString(isLegsDay));
        cv.put(isHIIT, boolToString(isHIITDay));

        db.update(Schedule_Table, cv, "DayOfTheWeek=?", new String[]{Day});

    }

    public boolean StringToBool(String str) {
        if (str != null)
            return str.equalsIgnoreCase("Yes");
        else
            return false;
    }

    public String boolToString(boolean bool) {
        if (bool)
            return "Yes";
        else if (!bool)
            return "No";
        else
            return null;
    }

    public CircuitHolder getExercise(String name) {
        CircuitHolder myExercise = null;
        String query = "SELECT * FROM " + Table_Name + " WHERE " + Col_2 + " LIKE '" + name + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor curs = db.rawQuery(query, null);
        if (curs != null) {
            curs.moveToFirst();
            String exName = curs.getString(1);
            String exDescription = curs.getString(6);
            myExercise = new CircuitHolder(exName, exDescription, 0);
        }
        return myExercise;
    }

    public void DeleteSavedCircuit(String savedCirc) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Save_Table, "circuitName=?", new String[]{savedCirc});

    }

    public void saveCircuit(CircuitHolder myCircuit, String isFirst, int num, String SaveName) {
        SQLiteDatabase db = this.getWritableDatabase();
        saveCircuit(myCircuit, isFirst, num, db, SaveName);
    }

    public boolean saveCircuit(CircuitHolder myCircuit, String isFirst, int num, SQLiteDatabase db, String SaveName) {
        try {
            if (myCircuit != null) {
                String myExercise = num + ": " + myCircuit.repCount + " (" + myCircuit.name + ")";
                if(myCircuit.isEdited == 1)
                    myExercise = myCircuit.name;
                ContentValues contentValues = new ContentValues();
                contentValues.put(Save_Exercise, myExercise);
                if (myCircuit.next != null) {
                    Cursor idCursor = db.rawQuery("SELECT " + Save_ID + " FROM " + Save_Table, null);
                    if (idCursor.getCount() == 0)
                        contentValues.put(Next_Index, 2);
                    else {
                        idCursor.moveToLast();
                        int nextID = idCursor.getInt(0) + 2;
                        contentValues.put(Next_Index, nextID);
                    }
                    idCursor.close();
                } else {
                    contentValues.put(Next_Index, 0);
                }
                contentValues.put(isHead, isFirst);
                contentValues.put(Save_Name, SaveName);
                db.insert(Save_Table, null, contentValues);
                saveCircuit(myCircuit.next, "No", num + 1, db, SaveName);

            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public CircuitHolder getSavedCircuit(String Name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor savedCursor = db.rawQuery("SELECT * FROM " + Save_Table + " WHERE " + Save_Name + " Like '" + Name + "'", null);
        savedCursor.moveToFirst();
        int circLength = savedCursor.getCount();
        String[] myCircEntries = new String[circLength];
        for (int i = 0; i < circLength; i++) {
            myCircEntries[i] = savedCursor.getString(1);
            savedCursor.moveToNext();
        }
        CircuitHolder mySavedCircuit = CreateLinkedList(null, myCircEntries, 0, Name);
        return mySavedCircuit;
    }

    //recursive method initially passed an empty circuitholder and a string array of all the list entries
    public CircuitHolder CreateLinkedList(CircuitHolder myCircuit, String[] listofstuff, int pos, String saveName) {
        if (pos < listofstuff.length - 1) {
            myCircuit = new CircuitHolder(listofstuff[pos], CreateLinkedList(myCircuit, listofstuff, pos + 1, saveName), 1, saveName);
        } else if (pos == listofstuff.length - 1) {
            myCircuit = new CircuitHolder(listofstuff[pos], 1, saveName);
        }
        return myCircuit;
    }

    //
    public Cursor SavedCircuitNames() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct " + Save_Name + " from " + Save_Table, null);
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

    public void setRemoved(String remName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ContentValues cv = new ContentValues();
        cv.put(Col_9, "Yes");
        contentValues.put(unwantedName, remName);
        db.insert(Unwanted_Table, null, contentValues);
//        db.delete(Table_Name, "Name=?", new String[]{remName});
        db.update(Table_Name, cv, "Name=?", new String[]{remName});
    }

    public void redoRemoval(SQLiteDatabase db)
    {
        String remName;
        Cursor cursor = db.rawQuery("SELECT Name FROM " + Unwanted_Table, null);
        while(cursor.moveToNext())
        {
            ContentValues cv = new ContentValues();
            cv.put(Col_9,"Yes");
            remName = cursor.getString(0);
            db.update(Table_Name,cv, "Name=?", new String[]{remName});
        }
    }

    public void unRemove(String remName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Col_9, "No");
        db.delete(Unwanted_Table, "Name=?", new String[]{remName});
//        db.delete(Table_Name, "Name=?", new String[]{remName});
        db.update(Table_Name, cv, "Name=?", new String[]{remName});
    }

    public void insertData(String HIITBool, String Name, String Home, String Option_1, String Option_2, String Category, String isEasy, String Description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_2, Name);
        contentValues.put(Col_3, Option_1);
        contentValues.put(Col_4, Option_2);
        contentValues.put(Col_5, Category);
        contentValues.put(Col_6, isEasy);
        contentValues.put(Col_7, Description);
        contentValues.put(Col_8, Home);
        contentValues.put(Col_9, "No");
        contentValues.put(Col_10, HIITBool);

        db.insert(Table_Name, null, contentValues);
    }

    public void insertScheds(String Day) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DayOfTheWeek, Day);

        db.insertWithOnConflict(Schedule_Table, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Cursor getAllExercises() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + Table_Name + " WHERE " + Col_9 + " LIKE 'No' Order by " + Col_2, null);
        return res;
    }

    public Cursor getAllUnwantedExercises() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + Table_Name + " WHERE " + Col_9 + " LIKE 'Yes' Order by " + Col_2, null);
        return res;
    }

    public static String[] addOn(String[] array, String newString) {
        String[] newArray = new String[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = newString;
        return newArray;
    }

    public static int[] addOnInt(int[] array, int newInt) {
        int[] newArray = new int[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = newInt;
        return newArray;
    }

    public static String getExName ( char[] charArr)
    {
        Stack<Character> bracketStack = new Stack<>();
        String ExName = "";
        for (Character currChar : charArr) {
            if (currChar == '(' && bracketStack.isEmpty()) {
                bracketStack.push(currChar);
            } else if (currChar == '(' && !bracketStack.isEmpty()) {
                ExName += currChar;
                bracketStack.push(currChar);
            } else if (currChar == ')') {
                bracketStack.pop();
                if (!bracketStack.isEmpty())
                    ExName += currChar;
            } else {
                if (!bracketStack.isEmpty()) {
                    ExName += currChar;
                }
            }
        }
        return ExName;
    }

    public CircuitHolder newCircuit(boolean isArms, boolean isBackAndShoulders, boolean isCore, boolean isLegs, boolean isOther, int length, boolean isNoob, boolean isEquipped) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(isOther)
        {
            isArms = isBackAndShoulders = isCore = isLegs = isNoob = isOther = true;
        }
        //Set up variables to note whether the exercise is the first n the list or not, all the queries required to get the desired results from the db
        //as well as count the available number of exercises selectable for the current circuit parameters
        boolean first = true;
        String[] requiredQueries = new String[1];
        int[] AvailableOptions = new int[1];
        String easyQueryAdd = "";
        if (isNoob)
            easyQueryAdd += " AND isEasy Like 'Yes'";
        if (!isEquipped)
            easyQueryAdd += " AND Home Like 'Yes'";
        if(isOther)
            easyQueryAdd += " AND isHIIT Like 'Yes'";

        //based on the states of the check boxes, add required queries to string[], as well as count available choices
        if (isArms && first) {
            first = false;
            requiredQueries[0] = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Arms'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1";
            Cursor armCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Arms'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1", null);
            armCurs.moveToFirst();
            AvailableOptions[0] = armCurs.getInt(0);
        }
        if (isBackAndShoulders) {
            if (first) {
                first = false;
                requiredQueries[0] = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'BackAndShoulders'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1";
                Cursor bsCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'BackAndShoulders'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1", null);
                bsCurs.moveToFirst();
                AvailableOptions[0] = bsCurs.getInt(0);
            } else {
                requiredQueries = addOn(requiredQueries, "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'BackAndShoulders'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1");
                Cursor bsCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'BackAndShoulders'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1", null);
                bsCurs.moveToFirst();
                AvailableOptions = addOnInt(AvailableOptions, bsCurs.getInt(0));
            }
        }
        if (isCore) {
            if (first) {
                first = false;
                requiredQueries[0] = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Core'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1";
                Cursor cCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Core'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1", null);
                cCurs.moveToFirst();
                AvailableOptions[0] = cCurs.getInt(0);
            } else {
                requiredQueries = addOn(requiredQueries, "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Core'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1");
                Cursor cCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Core'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1", null);
                cCurs.moveToFirst();
                AvailableOptions = addOnInt(AvailableOptions, cCurs.getInt(0));
            }
        }
        if (isLegs) {
            if (first) {
                first = false;
                requiredQueries[0] = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Legs'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1";
                Cursor lCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Legs'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1", null);
                lCurs.moveToFirst();
                AvailableOptions[0] = lCurs.getInt(0);
            } else {
                requiredQueries = addOn(requiredQueries, "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Legs'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1");
                Cursor lCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Legs'" + easyQueryAdd + " AND " + Col_9 + " LIKE 'No' ORDER BY RANDOM() LIMIT 1", null);
                lCurs.moveToFirst();
                AvailableOptions = addOnInt(AvailableOptions, lCurs.getInt(0));
            }
        }
//        if (isOther) {
//            if (first) {
//                first = false;
//                requiredQueries[0] = "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Other'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1";
//                Cursor oCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Other'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
//                oCurs.moveToFirst();
//                AvailableOptions[0] = oCurs.getInt(0);
//            } else {
//                requiredQueries = addOn(requiredQueries, "SELECT * FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Other'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1");
//                Cursor oCurs = db.rawQuery("SELECT count(*) FROM " + Table_Name + " WHERE " + Col_5 + " Like 'Other'" + easyQueryAdd + " ORDER BY RANDOM() LIMIT 1", null);
//                oCurs.moveToFirst();
//                AvailableOptions = addOnInt(AvailableOptions, oCurs.getInt(0));
//            }
//
//        }

        //use appropriate queries to build exercise list
        CircuitHolder circRes = null;
        String[] duplicateFinder = new String[length];

        for (int i = 0; i < length; i++) {
            Cursor res = db.rawQuery(requiredQueries[i % requiredQueries.length], null);
            res.moveToFirst();
            String Name = res.getString(1);
            String[] Options = {res.getString(2), res.getString(3)};
            String Cat = res.getString(4);

            //if the random exercise is already in our circuit list
            if (Arrays.asList(duplicateFinder).contains(Name) && AvailableOptions[i % AvailableOptions.length] > 0) {
                i--;
            }
            //if there are no more appropriate exercises that we haven't used yet
            else if (AvailableOptions[i % AvailableOptions.length] < 1) {
                return new CircuitHolder("Insufficient stored exercises to fulfill your request, select a shorter length circuit and don't hurt yourself please.", "");
            }
            //if the exercise is one that we want to add to our circuit
//            else if (Cat.equalsIgnoreCase("Other") || AvailableOptions[i % AvailableOptions.length] > 0) {
            else if(AvailableOptions[i % AvailableOptions.length] > 0){
                Random rand = new Random();
                int option = rand.nextInt(2);
                duplicateFinder[i] = Name;
                AvailableOptions[i % AvailableOptions.length]--;
                if(!isOther)
                    circRes = CircuitHolder.addExercise(circRes, Options[option], Name);
                else
                    circRes = CircuitHolder.addHIITExercise(circRes, "", Name);
            }
            //pretty much to catch anything else, a simple try catch situation i guess
            else {
                return new CircuitHolder("Some Logical Error Occurred", "");
            }
        }
        return circRes;
    }

    public void populate() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + Table_Name;
        String countSched = "SELECT count(*) FROM " + Schedule_Table;

        Cursor myCursor = db.rawQuery(count, null);
        Cursor myCursorSched = db.rawQuery(countSched, null);

        myCursor.moveToFirst();
        myCursorSched.moveToFirst();

        int iCount = myCursor.getInt(0);
        int iCountSched = myCursorSched.getInt(0);

        if (iCountSched <= 0) {
            insertScheds("Monday");
            insertScheds("Tuesday");
            insertScheds("Wednesday");
            insertScheds("Thursday");
            insertScheds("Friday");
            insertScheds("Saturday");
            insertScheds("Sunday");
        }

        if (iCount <= 0) {
            //Arms Exercises (33 Total)
            insertData("Yes","Dips", "No", "50", "40", "Arms", "Yes", "Keep your body as straight as possible \n\nTry your best to dip down until your shoulders almost touch the bar");
            insertData("No","One Arm Pushups", "Yes", "10 Each Arm", "8 Each Arm", "Arms", "Yes", "Keep your body straight\n\nTip your unused shoulder up so your pushing shoulder can go all the way to the floor");
            insertData("Yes","Reverse Dips", "Yes", "60", "50", "Arms", "Yes", "Ensure you are in a comfortable position and the gap between your hands and feet is not too large \n\nYour back should be very close to the bar when you dip down");
            insertData("No","One arm Reverse Dips", "Yes", "30 each side", "20 each side", "Arms", "Yes", "Reverse dips with a wider grip than normal\n\nLower to one side then the other\n\nThey're like archer reverse dips");
            insertData("No","Swings to Handstand", "No", "15", "2 x 10", "Arms", "Yes", "Keep your body straight and swing from the shoulders \n\nThere should always be a straight line down your body, from your shoulders to your feet (So don't bend at the hips) \n\nIt is a common for people to drop the bum and lift the feet in the front swing, so try not to do this by exaggerating pushing your hips out in the front swing");
            insertData("No","Dip Swings", "No", "15", "15", "Arms", "Yes", "Ensure your dips and your swings are both strong before trying this one \n\nWhen your feet are going down, you must dip down\n\nWhen your feet are going up, push back up out of the dip");
            insertData("No","Dip Swings to Planche", "No", "10", "12", "Arms", "Yes", "Ensure dip swings are strong for this one \n\nTry to momentarily stop in a planche at the back of each dip swing");
            insertData("No","Dip Swings to Handstand", "No", "10", "8", "Arms", "No", "Ensure dip swings are strong for this one \n\nPush to a momentary handstand on each back swing");
            insertData("No","Chicken Wing Dips", "No", "15", "18", "Arms", "No", "Keep your body straight as possible \n\nDip your shoulders as low as possible before dropping back into upper arm support \n\nThis motion is key in a muscle-up when transitioning from under the bar to over the bar and as such is a good way to train for a muscle-up if that is your goal");
            insertData("Yes","Overgrip Pull-ups", "Yes", "15", "10", "Arms", "Yes", "Control the movement\nLower yourself down, do not drop down uncontrollably as this could damage your shoulders and such\n\nEnsure you at least get your chin over the bar, I personally try and go a bit higher and get my nipples to the bar on each rep\n\nTry to arch your body and use your back and shoulder muscles to lift yourself up");
            insertData("Yes","Undergrip Pull-ups", "Yes", "15", "10", "Arms", "Yes", "Control the movement\nLower yourself down, do not drop down uncontrollably as this could damage your shoulders and such\n\nEnsure you at least get your chin over the bar\n\nTry to pike your body slightly so your feet are in front of your body and focus on using the biceps for pulling yourself up");
            insertData("No","One arm Undergrip Pull-ups", "Yes", "5 each arm", "8 each arm", "Arms", "No", "Hold your wrist with your other hand for support until you can do it without your other hand");
            insertData("Yes","Wide arm Pull-ups", "Yes", "15", "10", "Arms", "Yes", "Control the movement\nLower yourself down, do not drop down uncontrollably as this could damage your shoulders and such\n\nEnsure you get your chin over the bar\n\nKeep your whole body straight and pull yourself up using your back and shoulder muscles mostly");
            insertData("Yes","Diamond Push-ups", "Yes", "20", "30", "Arms", "Yes", "Hands close together with index fingers and thumbs touching to make the shape of a diamond\n\nBring your chest close to your hands on each rep\n\nKeep your body straight");
            insertData("Yes","Push-ups", "Yes", "30", "40", "Arms", "Yes", "Bring your chest all the way down in between your hands\n\nkeep your body straight");
            insertData("Yes","Elbow Push-ups", "Yes", "30", "35", "Arms", "Yes", "Start your pushup with your hands slightly in front of your shoulders and lower your elbows to the ground so that your forearms are flat on each rep\n\nKeep the elbows in");
            insertData("No","Bodyweight tricep extensions", "Yes", "30", "35", "Arms", "Yes", "with hands higher than your feet, keep your elbows close to your side and lower until your head is below your hands and your triceps are fully stretched, then push back up");
            insertData("No","Fingertip Push-ups", "Yes", "20", "25", "Arms", "Yes", "Bring your chest all the way down in between your hands\n\nkeep your body straight");
            insertData("No","Push-up 360", "Yes", "14", "16", "Arms", "No", "An explosive push-up with a full-twist/360 at the top");
            insertData("Yes","Push-ups with feet on exercise ball", "Yes", "30", "40", "Arms", "Yes", "Bring your chest all the way down in between your hands\n\nkeep your body straight");
            insertData("No","Push-ups with hands on exercise ball", "Yes", "30", "40", "Arms", "Yes", "Bring your chest all the way down in between your hands\n\nkeep your body straight\n\nDon't bounce your chest on the ball to get back up");
            insertData("No","Maltese Push-ups", "Yes", "20", "30", "Arms", "Yes", "At the bottom of each push-up, shift your body forward and back before pushing back up");
            insertData("No","Handstand Push-ups", "Yes", "10", "12", "Arms", "No", "Best done with a spotter or against a wall, in my opinion with your stomach towards the wall\n\nTry to keep your body straight and use your shoulders/scapula mostly");
            insertData("Yes","Pike Push-ups", "Yes", "12", "15", "Arms", "Yes", "Pike your body so that your body, from your hands to your butt, is vertical");
            insertData("Yes","Inverted Rows", "No", "15", "12", "Arms", "Yes", "Try to have your feet just slightly lower than the bar/rings you're hanging on\n\nKeep your body straight, do not bend your hips and let your butt drop down");
            insertData("No","Undergrip single arm Inverted Rows", "No", "10 Each arm", "12 Each arm", "Arms", "Yes", "If its too hard, pull yourself to the top using both arms, then lower down with one arm as controlled as you can");
            insertData("Yes","Rings Push-ups", "No", "15", "20", "Arms", "Yes", "Turn the rings out - At the top of the pushup try turn your hands so your palms face inward towards eachother\n\nKeep your body straight");
            insertData("No","Reverse push-ups on Rings", "No", "15", "12", "Arms", "Yes", "Rings pushups with a reverse grip - palms face forward and elbows are kept in for the entire pushup\n\nKeep your body straight");
            insertData("Yes","Archer Push-ups", "Yes", "20 [10 / side]", "24 [12 / side]", "Arms", "Yes", "Pushups with hands far apart - In each pushup keep one arm straight and pushup with the other arm\n\nIt's like you're about to fire a bow & arrow");
            insertData("Yes","Archer Push-ups on Rings", "No", "14 [7 / side]", "12 [6 / side]", "Arms", "Yes", "In each pushup keep one arm straight and pushup with the other arm\n\nIt's like you're about to fire a bow & arrow");
            insertData("No","Dips on Rings", "No", "15", "20", "Arms", "No", "Try to keep your body straight\n\nTurn the rings out - At the top of the dip try turn your hands so your palms face inward towards eachother");
            insertData("Yes","Rings Support Hold", "No", "30 Seconds", "35 Seconds", "Arms", "Yes", "Turn the rings out - Turn your hands so your palms face inward towards eachother or even a little bit forwards\n\nArms straight, Legs straight, Legs together");
            insertData("Yes","Clapping Push-ups", "Yes", "20", "30", "Arms", "Yes", "Explosive movements, push up as fast and hard as possible\n\nJust clap once, don't try to be cool");
            insertData("No","Japanese Handstand Push-ups", "No", "5", "6", "Arms", "No", "Dip the handstand all the way down first, only then start to move your shoulders forward until your body is flat, then reverse the process\n\nEnsure your handstand push-ups are very strong as well as planches\n\nKeep your body straight\n\nThis one is hard");
            insertData("No","Bent-arm Straight Body Presses", "No", "6", "5", "Arms", "No", "From an L-sit, slowly lower your legs until your body is straight, then keep your body straight while you bend your arms and press up to handstand\n\nEnsure you can do a press already");
            insertData("Yes","Ice Cream Makers (Rings)", "No", "12", "15", "Arms", "Yes", "Start hanging, then swing to a front lever position, then pull-up while you swing back down\n\nKeep your body straight\n\nkeep the motion smooth and controlled");
            insertData("Yes","Ice Cream Makers (Bar)", "No", "12", "10", "Arms", "Yes", "Start hanging, pull to a front lever, then pull-up while you swing back down\n\nKeep your body straight");
            insertData("No","Side-to-Side Pull-ups", "Yes", "10 [5 / side]", "12 [6 / side]", "Arms", "Yes", "Try to do mostly a one arm pull-up while keeping your other arm straight and only using it to help you just enough");
            insertData("No","Muscle-ups on Bar", "Yes", "10", "8", "Arms", "No", "Ensure your pull-ups are strong for this one\n\nthe key is getting the shoulders on top of the bar, once you can do that well it's easy");
            insertData("Yes","Muscle-ups on Rings", "No", "10", "12", "Arms", "No", "Keep the rings close together when getting your shoulders on top of the rings\n\nIf you let the rings move apart too much it gets very difficult");
            insertData("Yes","Overgrip Pull-up Hold", "Yes", "45 Seconds", "2 x 30 Seconds", "Arms", "Yes", "In overgrip, keep yourself at the top of a pull-up with your chin over the bar for the duration\n\nTry to move as little as possible");
            insertData("Yes","Undergrip Pull-up Hold", "Yes", "45 Seconds", "2 x 30 Seconds", "Arms", "Yes", "In undergrip, keep yourself at the top of a pull-up with your chin over the bar for the duration\n\nTry to move as little as possible");
            insertData("Yes","Plank/Push-up Runs", "Yes", "45 Seconds", "1 Minute", "Arms", "Yes", "Run with your legs while in a push-up position\n\nbring your knees all the way to your chest and all the way back out to straight leg");
            insertData("No","Rope Climb", "No", "1", "1", "Arms", "No", "Climb a rope somewhere\n\nTry to use only your arms, this of course depends on how experienced you are, you are allowed to use your legs");
            insertData("No","Chest Roll to Handstand", "No", "15", "20", "Arms", "No", "You want the surface you're rolling off of to drop off right under your pecks\n\nkeep the arms bent until you get your feet all the way up, then push-up to handstand\nMomentarily hold the handstand and reverse the action back to the starting position");
            insertData("Yes","Wrist Rollers", "No", "Up and Down Each Way Once", "2 Minutes Steady Pace Up and Down for", "Arms", "Yes", "This one requires a unique contraption\nBasically hang weights on a rope from some kind of pipe\nKeep rolling the pipe until all the rope is wrapped around the pipe, then slowly roll it the other way to unravel it\n\nKeep your arms straight out in front of you so that your hands are at shoulder level the entire time");
            insertData("No","Pull-ups on Rope with Vertical Grip", "No", "12", "10", "Arms", "Yes", "Squeeze your scapula together at the top of the pull-up");

            //Back and Shoulders Exercises (35 Total)
            insertData("No","Wall bridge snap-downs", "Yes", "15", "20", "BackAndShoulders", "Yes", "Kic to handstand against a wall with your hands far away from the wall and push your shoulders out until you can pull your feet away bfrom the wall and back down to standing");
            insertData("Yes","Handstand Hold", "Yes", "1 Minute", "2 x 40 Seconds", "BackAndShoulders", "Yes", "Your entire body, from your hands to your feet, should be a straight line\n\nLook at the ground but trying to keep the head as forward as possible.\nSo it's like you're rolling your eyes up\n\nDo it against a wall, with a spotter, or simply on your own\n\nYou can use parallets or do it on flat ground.\nParallets will be better for your wrists");
            insertData("No","Single Rail Handstand Hold", "No", "45 Seconds", "2 x 30 Seconds", "BackAndShoulders", "No", "Your entire body, from your hands to your feet, should be a straight line\n\nLook at the rail but trying to keep the head as forward as possible.\nSo it's like you're rolling your eyes up\n\nDo it with a spotter helping you\n\nKeep your hands in an overgrip position");
            insertData("No","Tuck Planche Hold", "Yes", "2 x 10 Seconds", "3 x 7 Seconds", "BackAndShoulders", "Yes", "Keep your arms straight\n\nKeep your knees by your chest\n\nDon't lift your butt too high, it should be level with your shoulders");
            insertData("No","Straddle Planche Hold", "Yes", "3 x 3 Seconds", "2 x 5 Seconds", "BackAndShoulders", "No", "Keep your arms and legs straight\n\nOpen your legs as wide as you are able to, the wider you can get your legs the easier the hold will be\n\nMay or may not need a spotter");
            insertData("Yes","Planche Push-ups", "No", "12", "10", "BackAndShoulders", "Yes", "Swing the legs up until your body is flat first, then push your arms straight up to a planche\n\nCan be done straddled (Legs open and straight, easier)\nCan be done straight bodied (Legs straight and together, harder)");
            insertData("No","Planche Presses", "No", "10", "5 Holding Each Planche for 3 Seconds", "BackAndShoulders", "No", "Definitely need a spotter for this one\n\nKeep the arms and body straight as you can, only rotate at the shoulders");
            insertData("No","Gym Bridge Hold", "Yes", "3x10 Seconds", "2 x 15 Seconds", "BackAndShoulders", "Yes", "Hold a gym bridge/Back bend");
            insertData("Yes","Presses", "No", "5", "6", "BackAndShoulders", "No", "Lift your butt all the way up first, only then start to lift your legs up until in a handstand");
            insertData("Yes","Standing Presses", "Yes", "6", "8", "BackAndShoulders", "No", "Can be done straddled (Legs straight and together, easier)\nCan be done piked (Legs together and straight, harder)\n\nStart with your hands on the floor close to your feet and your legs straight\n\nLift your butt all the way up first, only then start to lift your legs up until in a handstand");
            insertData("No","Endo Roll to Handstand", "No", "8", "6", "BackAndShoulders", "No", "The earlier you get your hands on the floor between your legs the easier this move is\n\nKep your legs straight and try to keep your feet off the floor the entire move");
            insertData("No","Back Uprise Handstand", "No", "10", "8", "BackAndShoulders", "No", "Try swing the feet all the way up first, so your body's vertical, then push-up to handstand to finish the move\nThen reverse the process\n\nTry and use the momentum from your swing to get your feet all the way up");
            insertData("No","Front Uprise Swing Handstand", "No", "10", "8", "BackAndShoulders", "No", "Push the butt and the shoulders forward as you uprise, this will ensure that you have enough swing to get to handstand");
            insertData("No","Swing to Handstand", "No", "10", "12", "BackAndShoulders", "Yes", "Keep the arms straight as well as your body\n\nFeet must go as high as they can in the front swing as well\n\nEnsure before trying this move that your swings are strong as well as your handstands\n\nHave a spotter if you are inexperienced");
            insertData("No","A-Pivots", "No", "6", "8", "BackAndShoulders", "No", "Start pivoting as you reach the top of the swing, try not to stop in handstand before turning as this creates more opportunity for falling over");
            insertData("Yes","Wall Angels", "Yes", "20", "25", "BackAndShoulders", "Yes", "Forearms should be vertical and elbows should be as far back as you can get them\n\nExtend all the way up and go all the way down");
            insertData("Yes","Floor Angles", "Yes", "12", "10", "BackAndShoulders", "Yes", "Use any kind of pole (weights bar, broom stick, random pipe you found) it must just be long enough so you can grip it widely\n\nKeep your hands as far up off the ground as you can\n\nBring the bar all the way to toughing your traps and extend it all the way out until your arms are straight");
            insertData("No","Front Lever Pulls", "No", "6", "5", "BackAndShoulders", "Yes", "Use a spotter for your legs\n\nKeep the body straight from shoulders to feet, do not bend your hips and drop your butt down");
            insertData("No","Back Lever Pulls", "No", "6", "5", "BackAndShoulders", "Yes", "Use a spotter\n\nKeep your body straight from shoulders to feet");
            insertData("Yes","Chest Raises", "No", "15", "12", "BackAndShoulders", "Yes", "Keep hands behind head and lift until your body is flat, do not go all the way up to an arched position");
            insertData("Yes","Heel Drives", "No", "30", "25", "BackAndShoulders", "Yes", "Kick the legs up fast until your body is flat, do not kick them up above your head into an arched position\n\nKick them up fast, then slowly and controlled lower them down again");
            insertData("No","Stunted Pump Swings to Planche", "No", "10", "8", "BackAndShoulders", "No", "Stunted = Stopping your legs from swinging in front of you, this forces you to use strength as it takes away momentum from a swing\n\nEnsure you have good Dip swings already as well as a strong planche");
            insertData("Yes","Cast to Handstand", "No", "10", "8", "BackAndShoulders", "No", "Keep the arms and body straight\n\nBest to have a spotter catch you once you reach handstand\n\nPush away from the bar and land on your feet on the floor unless you are strong enough to control the motion and reverse the cast back onto the bar from handstand");
            insertData("Yes","Flies with Weights", "Yes", "12", "15", "BackAndShoulders", "Yes", "Keep the arms straight\n\nsqueeze the scapula together at the top and lower slowly");
            insertData("No","Jack-Knife Push-Ups on Rings", "No", "12", "10", "BackAndShoulders", "Yes", "Can be done on the knees instead of the feet to make it easier\n\nKeep your core tight and the rings close together as you do the move\n\nOnly go until you're flat, dont go any lower");
            insertData("No","Flies on Rings", "No", "6", "8", "BackAndShoulders", "Yes", "Can be done on your knees to make it easier\n\nA spotter helping is always nice\n\nArms straight out next to you and back up\n\nTry have straight arms however this puts major strain on the elbows and as such I do these with my arms bent ever so slightly");
            insertData("No","Crescent Push-ups on Rings", "No", "6", "8", "BackAndShoulders", "Yes", "Aim to keep your body flat while moving your hands from out in front of you all the way back next to your hips with your arms straight\n\nit is hard to get them all the way back there, so just get them as far back as you can and push back up\n\nArms are straight this entire move");
            insertData("Yes","Hanging Upside-Down Shrugs", "No", "20", "25", "BackAndShoulders", "Yes", "Keep the body as straight as you can and lift your body up and down by shrugging your shoulders\n\nSomeone can stand above you and spot your feet if you have too much trouble balancing while doing it");
            insertData("No","Planche Rockers", "No", "10", "12", "BackAndShoulders", "Yes", "Keep the body straight as well as the arms\n\nHave the spotter let you forward as far as you can manage and then pull you back, talk to the spotter so they know where your limit is");
            insertData("No","Reverse Planche Rockers", "No", "10", "12", "BackAndShoulders", "Yes", "Keep the body straight as well as the arms\n\nHave the spotter let you back as far as you can manage and then pull you back, talk to the spotter so they know where your limit is");
            insertData("No","Rings Handstand Hold", "No", "40 Seconds", "2 x 25 Seconds", "BackAndShoulders", "No", "Keep the rings close together and turned out (Palms facing each other)\n\nYou can keep your feet on the rings cable but try keep your shoulders off the cable/straps");
            insertData("No","One Arm Handstand Hold", "Yes", "25 Seconds Each Arm", "20 Seconds Each Arm", "BackAndShoulders", "No", "Do it against a wall or with a spotter\n\nBest done using a parallet/handle so as to not hurt the wrist\n\nKeep the other arm by your side");
            insertData("Yes","Pommel Sliders", "No", "4 Lengths [1 of each style]", "4 Lengths [1 of each style]", "BackAndShoulders", "Yes", "Keep the body straight as possible and walk the arms forward with your feet on a slider\n\nIf you don't have sliders then I really don't know");
            insertData("Yes","Weight Plate Lifts", "Yes", "15", "12", "BackAndShoulders", "Yes", "Keep the arms straight and isolate the movement to the shoulders while keeping your core tight\n\n10-15kg is a good weight, 20kg is possible");
            insertData("No","Maltese Presses on Red Blocks", "No", "10", "8", "BackAndShoulders", "No", "Ensure you have a spotter so you can move through the entire motion\n\nkeep the body straight and lift yourself up by pushing with your arms\n\nDon't stick your butt out to help you get up, that's what the spotter's for");
            insertData("No","Maltese Lifts with Weights", "Yes", "10", "12", "BackAndShoulders", "Yes", "Keep your core tight and legs and arms straight\n\nLower weights down next to your side and then bring them back up and together above your legs\n\nTry have straight arms however this puts major strain on the elbows and as such I do these with my arms bent ever so slightly");

            //Core Exercises (25 Total)
            insertData("Yes","Arch Rocks", "No", "40", "45", "BackAndShoulders", "Yes", "Keep your arms straight forward and your hards up off the floor\n\nKeep your feet off the floor and rock back and forth\n\nKeep your rocks small and controlled");
            insertData("Yes","Windscreen Wipers", "Yes", "24 [12 / side]", "20 [10 / side]", "Core", "No", "Keep your legs and arms straight");
            insertData("Yes","Leg Lifts", "Yes", "15", "20", "Core", "Yes", "Lift your legs all the way to your hands and slowly lower them back down\n\nKeep your legs straight the entire time");
            insertData("Yes","Plank Hold", "Yes", "1 Minute", "1 Minute 15 Seconds", "Core", "Yes", "Keep your entire body straight");
            insertData("Yes","Dish Hold", "Yes", "1 Minute", "45 Seconds", "Core", "Yes", "Only butt/lower back on the ground\n\nKeep your arms overhead and keep your feet very close to the floor, but not touching");
            insertData("Yes","Dish Rocks", "No", "60", "50", "Core", "Yes", "Keep your entire body tight\n\nKeep the rocks small and controlled");
            insertData("Yes","Knee Touches", "Yes", "35", "45", "Core", "Yes", "Keep your legs straight, sit up to touch your knees then lie back down");
            insertData("Yes","Sitting Leg Lifts", "Yes", "20", "25", "Core", "Yes", "Hands on the floor next to your knees\n\nLift with yur legs straight as high as you can while keeping pressure on your hands");
            insertData("Yes","Lying Leg Lifts", "Yes", "20", "25", "Core", "Yes", "Keep your arms on the floor by your sides and keep your legs straight\n\nLift your butt off the floor at the top of each leg lift");
            insertData("No","Side Plank Hold", "Yes", "45 Seconds Each Side", "30 Seconds Resisted Each Side", "Core", "Yes", "Keep the body tight\n\nKeep your other arm flat by your side the entire time");
            insertData("No","Side Plank Hold with leg lifted", "Yes", "40 Seconds Each Side", "45 Seconds Each Side", "Core", "Yes", "In a side plank, lift your top leg up while keeping it straight");
            insertData("Yes","Inchworm With Sliders", "No", "2 Lengths", "3 Lengths", "Core", "Yes", "Pull with arms and legs straight");
            insertData("No","Side Rocks", "No", "30 Each Side", "25 Each Side", "Core", "Yes", "Keep your bottom arm off te floor and use your top arm to balance yourself against the floor\n\nHold your shoulder or side to keep your bottom arm out of the way");
            insertData("Yes","Extended Plank Hold", "Yes", "30 Seconds", "40 Seconds", "Core", "Yes", "Arms and legs straight\n\nHands and feet as far apart as you can hold\n\nEars should be brushing against your arms");
            insertData("Yes","Jack Knives", "Yes", "25", "30", "Core", "Yes", "Keep your legs straight\n\nTry touch your toes on each rep");
            insertData("Yes","Hanging Sit-ups", "No", "15", "20", "Core", "Yes", "Go all the way up and all the way down");
            insertData("Yes","Crunches", "Yes", "30", "40", "Core", "Yes", "Lift as though you are trying to keep your chest flat but move it towards the ceiling\nDo not curl your stomach like the regular crunches");
            insertData("No","V-Ups", "No", "10", "12", "Core", "No", "Push your butt off the floor at the top of each jack-knife\n\nKeep the hands close to the floor at all times, ready to push");
            insertData("No","Bruce Lee Sit-ups", "No", "10", "12", "Core", "Yes", "Keep the entire body straight and lower your body as low as you can without losing form\n\nIf your butt is dropping then you're doing it wrong");
            insertData("No","Side Leg Lifts", "No", "20 Each Side", "25 Each Side", "Core", "Yes", "Bend your body sideways such that your feet only go up and down\nThere should be as little forward and backward movement as possible");
            insertData("Yes","Bicycle Crunches", "Yes", "50", "40", "Core", "Yes", "Touch opposite elbow and knee and ensure that you push your other elbow out behind you when at the top");
            insertData("No","Resisted Dish Hold", "No", "40 Seconds", "2 x 25 Seconds", "Core", "Yes", "Hold a tight shape\n\nSpotter must push down on the ankle area as well as the chest area");
            insertData("Yes","Tuck-ups", "No", "30", "25", "Core", "Yes", "Bring the knees all the way up to the chest and lower down all the way to straight body");
            insertData("Yes","L-Sit Hold", "Yes", "3 x 10 Seconds", "2 x 15 Seconds", "Core", "Yes", "Legs should be at a 90 degree angle with your body\n\nDo not drop your feet below the bars");
            insertData("Yes","Reverse Sit-ups", "Yes", "25", "20", "Core", "Yes", "Keep hands on the floor by your sides and lift the butt off the floor\n\nKeep your legs straight and as close to vertical as you can the entire time");
            insertData("No","AbCircuit", "Yes", "2 x 8s", "10s", "Core", "Yes", "Xs AbCircuit means:\n\t-X seconds dish hold\n\t-X dish rocks\n\t-X Jack-knives\n\t-X Reverse sit-ups\n\t-X Crunches\n\t-X Crossovers\n\t-X seconds dish hold\n\nDo not rest until entire circuit is completed");
            insertData("Yes","Grandfather Clock Exercises", "Yes", "20 Each side", "15 Each side", "Core", "Yes", "Lying on your back with your arms straight out to the side and your legs straight up\n\ndrop your legs side to side while keeping them straight");
            insertData("No","Side Jack-Knives", "Yes", "20 Each Side", "25 Each Side", "Core", "Yes", "Keep bottom hand on the floor and touch feet each rep with the top hand");
            insertData("No","Kip Extenders", "Yes", "15", "12", "Core", "Yes", "Keep the bar close to your legs the entire movement\n\nDo not bend your legs");

            //Leg Exercises (23 Total)
            insertData("Yes","Lunges", "Yes", "15 Each Leg", "20 Each Leg", "Legs", "Yes", "Don't put your knee on the floor");
            insertData("Yes","Calf Raises", "Yes", "30-30-30", "50", "Legs", "Yes", "Make sure heels are going as low as possible and as high as possible");
            insertData("Yes","Weighted Thrusts", "No", "15", "12", "Legs", "Yes", "Your legs below the knee should remain more or less vertical\n\nThrust until your body is flat");
            insertData("No","Single Leg Weighted Thrusts", "No", "8 Each Leg", "6 Each Leg", "Legs", "Yes", "Keep your other leg straight and horizontal");
            insertData("Yes","Squats", "Yes", "30", "40", "Legs", "Yes", "Your knees must not move forward past your feet\nKeep your heels on the ground and sit back into the squat");
            insertData("No","Weighted Squats", "No", "12", "15", "Legs", "Yes", "Do not use too much weight\n30-40kg is usually good");
            insertData("Yes","Squat Jumps", "Yes", "18", "20", "Legs", "Yes", "From a low squat, extend your body and jump up in one explosive motion\n\nSwing your arms above your head as you jump");
            insertData("Yes","Broad Jumps", "Yes", "20", "25", "Legs", "Yes", "Jump as far as you can forwards from a static stand\n\nMake sure both feet leave the ground and land at the same time\nBoth legs push equally as hard");
            insertData("Yes","Lunge Jumps", "Yes", "20", "24", "Legs", "Yes", "From a lunge, use an explosive jump and switch your legs to land in another lunge");
            insertData("Yes","Weighted-Lunge Jumps", "Yes", "20", "24", "Legs", "Yes", "From a lunge, use an explosive jump and switch your legs to land in another lunge");
            insertData("Yes","Box Jumps", "Yes", "10", "15", "Legs", "Yes", "Dont jump onto a surface too high for you to comfortably land on\n\nStand up fully once on top of the box before dismounting");
            insertData("No","Pistol Squats", "Yes", "10 Each Leg", "8 Each Leg", "Legs", "No", "Your knees must not move forward past your feet\nKeep your heel on the ground and sit back into the squat while keeping your leg straight out in front of you");
            insertData("Yes","Single Leg Roll-ups", "No", "10 Each Leg", "8 Each Leg", "Legs", "No", "You can use your hands when falling back and rolling but do not use them when rolling back up to stand\n\nKeep your other leg straight out in front of you");
            insertData("No","Resistance Runs 25m", "No", "2", "3", "Legs", "Yes", "Make sure the person pulling against you isn't pulling too hard\n\nYou must still be able to somewhat run, it must just be hard");
            insertData("No","Sled Push 20m", "No", "2", "3", "Legs", "Yes", "Find a low heavy thing and push it about 20m as one rep");
            insertData("No","Standing Back Tucks", "No", "10", "8", "Legs", "No", "Only do this as an exercise if you can safely do a standing back flip and have required safety mats and stuff to perform it");
            insertData("No","Standing Front Tucks", "No", "6", "8", "Legs", "No", "Only do this as an exercise if you can safely do a standing front flip and have required safety mats and stuff to perform it\n\nUse your arms in the regular way (above the head & throw forwards)\nOr add some flair and do a russian lift (Swing your arms down and lift behind you to generate momentum for the flip)");
            insertData("No","Depth Jumps", "No", "8", "6", "Legs", "No", "Jump from a high surface and absorb the landing with a squat motion\nDon't jump from too high unless you have the appropriate landing mats and padding to land on");
            insertData("Yes","Weighted Lunge Walks 12m", "Yes", "3", "4", "Legs", "Yes", "8-10kg in each hand is nice\nOne large 15-20kg plate on the back works as well");
            insertData("No","Duck Walks 12m", "Yes", "3", "4", "Legs", "Yes", "Walk with your legs bent as low as possible");
            insertData("No","Leg Extension", "No", "15", "20", "Legs", "Yes", "Do not overdo the amount of weigh tyou use for this\n\nTry holding the top of each extension just momentarily");
            insertData("No","Hamstring Sliders", "No", "10 Each Leg", "8 Each Leg", "Legs", "Yes", "Keep the other leg straight and out at about a 30-45 degree angle while you slide your other leg in and out\n\nDon't lie down when you slide your leg out/nKeep your butt off the floor");
            insertData("No","Single Leg Broad Jumps", "Yes", "10 Each Leg", "12 Each Leg", "Legs", "Yes", "Static standing jumps on one leg\nUse your other leg to swing your body up and forwards");
            insertData("Yes","Tree-Falls", "No", "10", "15", "Legs", "Yes", "Have a spotter hold your legs down if you cannot find an appropriate place to put your feet\n\nDon't stick your butt out on the way up to help yourself get up, keep your body straight and push off the floor with your arms as little as possible to get back up after lowering down");

            redoRemoval(db);
            //Other Exercises (20 Total)
//            insertData("Push-up Burpees", "Yes", "", "", "Other", "Yes", "Push up, then jump your feet between your hands, then explosively jump up, then put your hands back down and jump your feet out to push-up position and push up");
//            insertData("Fat Mat Sprint", "No", "", "", "Other", "Yes", "Run in place on something somewhat squishy\n\nLift your knees high while running");
//            insertData("Skipping", "Yes", "", "", "Other", "Yes", "Small and quick\n\nYou can do double-unders if you feel like it. Do anything really as long as you keep skipping");
//            insertData("Punch Front Tucks", "No", "", "", "Other", "No", "Tuck front flips from a bounce on a sprung floor");
//            insertData("Squat Jumps", "Yes", "", "", "Other", "Yes", "Squat low keeping your feet flat and jump as high as you can");
//            insertData("Push-ups", "Yes", "", "", "Other", "Yes", "Keep your body straight and touch your chest to the floor at the bottom of each push-up if you're doing them on the floor");
//            insertData("Dips", "No", "", "", "Other", "No", "Keep your body as straight as possible \n\nTry your best to dip down until your shoulders almost touch the bar");
//            insertData("Reverse Dips", "Yes", "", "", "Other", "Yes", "Ensure you are in a comfortable position and the gap between your hands and feet is not too large \n\nYour back should be very close to the bar when you dip down");
//            insertData("Plank", "Yes", "", "", "Other", "Yes", "Keep your butt down and your back flat");
//            insertData("Inverted Rows on Low Rings", "No", "", "", "Other", "Yes", "Try to have your feet just slightly lower than the rings you're hanging on\n\nKeep your body straight, do not bend your hips and let your butt drop down");
//            insertData("Inverted Rows on P-Bar", "No", "", "", "Other", "Yes", "Hook your knees on the inside of the p-bars\n\nKeep your body straight, do not bend your hips and let your butt drop down");
//            insertData("Side-to-Side Bounces Over Rail", "No", "", "", "Other", "No", "Bar should be about knee height");
//            insertData("Box Jumps", "Yes", "", "", "Other", "Yes", "Dont jump onto a surface too high for you to comfortably land on\n\nStand up fully once on top of the box before dismounting");
//            insertData("Standing Back Tucks", "No", "", "", "Other", "No", "Only do this as an exercise if you can do backflips quite nicely");
//            insertData("Crunches", "Yes", "", "", "Other", "Yes", "Tuck the knees into the chest and then extend your body and legs out straight");
//            insertData("Lunges", "Yes", "", "", "Other", "Yes", "Don't put your knee on the floor");
//            insertData("Calf Raises", "Yes", "", "", "Other", "Yes", "Make sure heels are going as low as possible and as high as possible");
//            insertData("Bicycle Crunches", "Yes", "", "", "Other", "Yes", "Touch your knee with your elbow opposite each time");
//            insertData("Hanging Tuck-ups", "No", "", "", "Other", "Yes", "Tuck the knees into the chest and extend your body straight");
//            insertData("Reverse Sit-ups", "Yes", "", "", "Other", "Yes", "Keep your legs vertical, pointing straight up\n\nExtend your body as much as you can in a controlled way while keeping your hands on the floor");
        }
    }
}
