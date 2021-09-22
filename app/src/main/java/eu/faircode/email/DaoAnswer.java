package eu.faircode.email;

/*

*/

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoAnswer {
    @Query("SELECT * FROM answer" +
            " WHERE :all OR NOT hide" +
            " ORDER BY -favorite, name COLLATE NOCASE")
    List<EntityAnswer> getAnswers(boolean all);

    @Query("SELECT * FROM answer" +
            " WHERE favorite = :favorite" +
            " AND NOT hide" +
            " ORDER BY name COLLATE NOCASE")
    List<EntityAnswer> getAnswersByFavorite(boolean favorite);

    @Query("SELECT * FROM answer" +
            " WHERE external" +
            " AND NOT hide" +
            " ORDER BY name COLLATE NOCASE")
    List<EntityAnswer> getAnswersExternal();

    @Query("SELECT * FROM answer WHERE id = :id")
    EntityAnswer getAnswer(long id);

    @Query("SELECT * FROM answer" +
            " WHERE standard AND NOT hide")
    EntityAnswer getStandardAnswer();

    @Query("SELECT * FROM answer" +
            " WHERE receipt AND NOT hide")
    EntityAnswer getReceiptAnswer();

    @Query("SELECT * FROM answer" +
            " ORDER BY -favorite, name COLLATE NOCASE")
    LiveData<List<EntityAnswer>> liveAnswers();

    @Query("SELECT COUNT(*) FROM answer" +
            " WHERE NOT hide" +
            " AND (:favorite OR NOT favorite)")
    Integer getAnswerCount(boolean favorite);

    @Insert
    long insertAnswer(EntityAnswer answer);

    @Update
    int updateAnswer(EntityAnswer answer);

    @Query("UPDATE answer SET favorite = :favorite WHERE id = :id AND NOT (favorite IS :favorite)")
    int setAnswerFavorite(long id, boolean favorite);

    @Query("UPDATE answer SET hide = :hide WHERE id = :id AND NOT (hide IS :hide)")
    int setAnswerHidden(long id, boolean hide);

    @Query("UPDATE answer SET standard = 0 WHERE NOT (standard IS 0)")
    void resetStandard();

    @Query("UPDATE answer SET receipt = 0 WHERE NOT (receipt IS 0)")
    void resetReceipt();

    @Query("UPDATE answer" +
            " SET applied = applied + 1, last_applied = :time" +
            " WHERE id = :id")
    int applyAnswer(long id, long time);

    @Query("UPDATE answer" +
            " SET applied = 0, last_applied = NULL" +
            " WHERE id = :id AND NOT (applied IS 0)")
    int resetAnswer(long id);

    @Query("DELETE FROM answer WHERE id = :id")
    void deleteAnswer(long id);
}
