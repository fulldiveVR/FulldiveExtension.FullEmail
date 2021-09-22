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
public interface DaoRule {
    @Query("SELECT * FROM rule" +
            " WHERE folder = :folder" +
            " ORDER BY `order`, name COLLATE NOCASE")
    List<EntityRule> getRules(long folder);

    @Query("SELECT * FROM rule" +
            " WHERE folder = :folder" +
            " AND enabled" +
            " ORDER BY `order`, name COLLATE NOCASE")
    List<EntityRule> getEnabledRules(long folder);

    @Query("SELECT rule.*, folder.account, folder.name AS folderName, account.name AS accountName FROM rule" +
            " JOIN folder ON folder.id = rule.folder" +
            " JOIN account ON account.id = folder.account" +
            " WHERE rule.id = :id")
    TupleRuleEx getRule(long id);

    @Query("SELECT rule.*, folder.account, folder.name AS folderName, account.name AS accountName FROM rule" +
            " JOIN folder ON folder.id = rule.folder" +
            " JOIN account ON account.id = folder.account" +
            " WHERE rule.folder = :folder" +
            " ORDER BY `order`, name COLLATE NOCASE")
    LiveData<List<TupleRuleEx>> liveRules(long folder);

    @Insert
    long insertRule(EntityRule rule);

    @Update
    int updateRule(EntityRule rule);

    @Query("UPDATE rule" +
            " SET folder = :folder" +
            " WHERE id = :id AND NOT (folder IS :folder)")
    int setRuleFolder(long id, long folder);

    @Query("UPDATE rule" +
            " SET enabled = :enabled" +
            " WHERE id = :id AND NOT (enabled IS :enabled)")
    int setRuleEnabled(long id, boolean enabled);

    @Query("UPDATE rule" +
            " SET applied = applied + 1, last_applied = :time" +
            " WHERE id = :id")
    int applyRule(long id, long time);

    @Query("UPDATE rule" +
            " SET applied = 0, last_applied = NULL" +
            " WHERE id = :id AND NOT (applied IS 0)")
    int resetRule(long id);

    @Query("DELETE FROM rule WHERE id = :id")
    void deleteRule(long id);

    @Query("DELETE FROM rule WHERE folder = :folder")
    void deleteRules(long folder);
}
