package eu.faircode.email;

/*

*/

import android.content.Context;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

public class EncryptionHelper {
    static {
        try {
            Provider[] providers = Security.getProviders();
            for (int p = 0; p < providers.length; p++)
                if (BouncyCastleProvider.PROVIDER_NAME.equals(providers[p].getName())) {
                    Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
                    Provider bc = new BouncyCastleProvider();
                    Security.insertProviderAt(bc, p + 1);
                    Log.i("Replacing security provider " + providers[p] + " at " + p + " by " + bc);
                    break;
                }
        } catch (Throwable ex) {
            Log.e(ex);
        }
    }

    static void init(Context context) {

    }
}

