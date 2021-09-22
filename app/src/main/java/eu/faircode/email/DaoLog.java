package eu.faircode.email;

/*
   
*/

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoLog {
    @Query("SELECT * FROM log" +
            " WHERE time > :from" +
            " AND (:type IS NULL OR type = :type)" +
            " ORDER BY time DESC" +
            " LIMIT 2000")
    LiveData<List<EntityLog>> liveLogs(long from, Integer type);

    @Query("SELECT * FROM log" +
            " WHERE time > :from" +
            " AND (:type IS NULL OR type = :type)" +
            " ORDER BY time DESC")
    List<EntityLog> getLogs(long from, Integer type);

    @Insert
    long insertLog(EntityLog log);

    @Query("DELETE FROM log" +
            " WHERE id IN (SELECT id FROM log" +
            " WHERE time < :before ORDER BY time LIMIT :limit)")
    int deleteLogs(long before, int limit);
}
