package com.example.sergey.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.sergey.myapplication.DataBase.DBCard;
import com.example.sergey.myapplication.DataBase.DataBaseHelper;
import com.example.sergey.myapplication.adapters.MyScrollListener;
import com.example.sergey.myapplication.adapters.ResAdapter;
import com.example.sergey.myapplication.comparaters.PercentVkladsCompatrator;
import com.example.sergey.myapplication.comparaters.PercentsCreditsComparator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by sergey on 12.03.2018.
 */

public class LoadingThread extends AsyncTask<Void, Void, Void> {
    List<DBCard> main_array;
    ShimmerRecyclerView recyclerView;
    PercentVkladsCompatrator compatrator;
    PercentsCreditsComparator percentsCreditsComparator;
    ResAdapter adapter;
    Context context;
    String child;
    String ChildChild;
    String whatComp;


    public LoadingThread(List<DBCard> main_array, ShimmerRecyclerView recyclerView, PercentVkladsCompatrator compatrator,PercentsCreditsComparator percentsCreditsComparator, ResAdapter adapter, Context context, String child, String ChildChild, String whatComp) {
        this.recyclerView = recyclerView;
        this.main_array = main_array;
        this.compatrator = compatrator;
        this.percentsCreditsComparator = percentsCreditsComparator;
        this.adapter = adapter;
        this.context = context;
        this.child = child;
        this.ChildChild = ChildChild;
        this.whatComp = whatComp;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(Void... voids) {
            getMain_array();


        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)


    public void getMain_array (){

        final DatabaseReference myBase = FirebaseDatabase.getInstance().getReference();



        myBase.child(child).child(ChildChild).addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<DBCard>> t = new GenericTypeIndicator<List<DBCard>>(){};
                main_array = dataSnapshot.getValue(t);
                recyclerView.setAdapter(new ResAdapter(context, main_array, DataBaseHelper.TABLE_VKLADS));
            //    updateUI();

 /*               GlobalFunctions.getData(main_array, dataSnapshot, recyclerView);
                DataBaseHelper helper = new DataBaseHelper(context);
                SQLiteDatabase db = helper.getWritableDatabase();
                try{
                    db.delete(DataBaseHelper.TABLE_CACHE, null, null);
                } catch (SQLiteException e){};

                for (DBCard card:
                        main_array) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DataBaseHelper.KEY_TITLE, card.title);
                    contentValues.put(DataBaseHelper.KEY_BANK, card.bank);
                    contentValues.put(DataBaseHelper.KEY_SROK_IN_RUB, card.srok);
                    contentValues.put(DataBaseHelper.KEY_SUM_IN_RUB, card.sum);
                    contentValues.put(DataBaseHelper.KEY_PERCENTS, card.percents);
                    contentValues.put(DataBaseHelper.KEY_LINK, card.link);


                    db.insert(DataBaseHelper.TABLE_CACHE, null, contentValues);

                }
                updateUI(); */



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MyLog", "Failed to read value.", error.toException());
            }
        });


    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateUI(){
  /*      List<DBCard> list = new ArrayList<>();
        DataBaseHelper helper = new DataBaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(DataBaseHelper.TABLE_CACHE, null, null, null, null, null,null);
        if (cursor.moveToFirst()){
            int titleIndex = cursor.getColumnIndex(DataBaseHelper.KEY_TITLE);
            int idIndex = cursor.getColumnIndex(DataBaseHelper.KEY_ID);
            int sumIndex = cursor.getColumnIndex(DataBaseHelper.KEY_SUM_IN_RUB);
            int srokIndex = cursor.getColumnIndex(DataBaseHelper.KEY_SROK_IN_RUB);
            int bankID = cursor.getColumnIndex(DataBaseHelper.KEY_BANK);
            int linkIndex = cursor.getColumnIndex(DataBaseHelper.KEY_LINK);
            int percentsIndex = cursor.getColumnIndex(DataBaseHelper.KEY_PERCENTS);
            while(cursor.moveToNext()){
                DBCard card = new DBCard(cursor.getString(titleIndex),
                                        cursor.getDouble(percentsIndex),
                                        cursor.getInt(sumIndex),
                                        cursor.getInt(srokIndex),
                                        cursor.getString(bankID),
                                        cursor.getString(linkIndex));
                list.add(card);
            }
        }
        cursor.close();
        db.close(); */
        if (Objects.equals(whatComp, "vklads")){
            compatrator = new PercentVkladsCompatrator();
            Collections.sort(main_array, compatrator);
        } else if (Objects.equals(whatComp, "kredits")) {
            percentsCreditsComparator = new PercentsCreditsComparator();
            Collections.sort(main_array, percentsCreditsComparator);
        }
        List<DBCard> showArray = new ArrayList<>();

        adapter = new ResAdapter(context, main_array, DataBaseHelper.TABLE_CREDITS);
        GlobalFunctions.loadMore(main_array, showArray, adapter, 0, 15);

        recyclerView.setAdapter(adapter);
    //    recyclerView.addOnScrollListener(new MyScrollListener(recyclerView, main_array, adapter, showArray));
        recyclerView.hideShimmerAdapter();
    }
}