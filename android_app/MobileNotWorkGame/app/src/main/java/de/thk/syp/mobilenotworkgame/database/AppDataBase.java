package de.thk.syp.mobilenotworkgame.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Messung.class}, version = 2, exportSchema = true)
public abstract class AppDataBase extends RoomDatabase {
    public abstract MessungDAO messungDao();
}

