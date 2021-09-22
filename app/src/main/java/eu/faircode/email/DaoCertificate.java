package eu.faircode.email;

/*

*/

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoCertificate {
    @Query("SELECT * FROM certificate")
    List<EntityCertificate> getCertificates();

    @Query("SELECT * FROM certificate" +
            " ORDER BY intermediate, email, subject")
    LiveData<List<EntityCertificate>> liveCertificates();

    @Query("SELECT * FROM certificate" +
            " WHERE fingerprint = :fingerprint" +
            " AND email = :email COLLATE NOCASE")
    EntityCertificate getCertificate(String fingerprint, String email);

    @Query("SELECT * FROM certificate" +
            " WHERE email = :email COLLATE NOCASE")
    List<EntityCertificate> getCertificateByEmail(String email);

    @Query("SELECT * FROM certificate" +
            " WHERE intermediate")
    List<EntityCertificate> getIntermediateCertificate();

    @Insert
    long insertCertificate(EntityCertificate certificate);

    @Query("DELETE FROM certificate WHERE id = :id")
    void deleteCertificate(long id);
}
