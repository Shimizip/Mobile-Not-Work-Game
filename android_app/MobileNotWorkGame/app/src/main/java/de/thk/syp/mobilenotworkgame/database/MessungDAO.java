package de.thk.syp.mobilenotworkgame.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MessungDAO {
    @Query("SELECT * FROM messung")
    List<Messung> getAll();

    @Insert
    void insert(Messung messung);

    @Query("DELETE FROM messung")
    void deleteAll();
    @Query("DELETE FROM messung WHERE id = :messungId")
    void deleteById(int messungId);

    @Delete
    void delete(Messung messung);

    // Weitere Queries nach Bedarf
}
