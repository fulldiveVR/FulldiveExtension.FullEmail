package eu.faircode.email;

/*
   
*/

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.security.KeyChain;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import org.openintents.openpgp.IOpenPgpService2;
import org.openintents.openpgp.util.OpenPgpApi;
import org.openintents.openpgp.util.OpenPgpServiceConnection;

import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class FragmentOptionsEncryption extends FragmentBase implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ImageButton ibInfo;
    private SwitchCompat swSign;
    private SwitchCompat swEncrypt;
    private SwitchCompat swAutoDecrypt;
    private SwitchCompat swAutoUndoDecrypt;

    private Spinner spOpenPgp;
    private ImageButton ibOpenKeychain;
    private TextView tvOpenPgpStatus;
    private SwitchCompat swAutocrypt;
    private SwitchCompat swAutocryptMutual;
    private SwitchCompat swEncryptSubject;

    private Spinner spSignAlgoSmime;
    private Spinner spEncryptAlgoSmime;
    private SwitchCompat swCheckCertificate;
    private Button btnManageCertificates;
    private Button btnImportKey;
    private Button btnManageKeys;
    private Button btnCa;

    private CardView cardDebug;
    private TextView tvKeySize;
    private TextView tvProviders;

    private OpenPgpServiceConnection pgpService;
    private List<String> openPgpProvider = new ArrayList<>();

    private final static String[] RESET_OPTIONS = new String[]{
            "sign_default", "encrypt_default", "auto_decrypt", "auto_undecrypt",
            "openpgp_provider", "autocrypt", "autocrypt_mutual", "encrypt_subject",
            "sign_algo_smime", "encrypt_algo_smime", "check_certificate"
    };

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setSubtitle(R.string.title_setup);
        setHasOptionsMenu(true);

        PackageManager pm = getContext().getPackageManager();
        View view = inflater.inflate(R.layout.fragment_options_encryption, container, false);

        // Get controls
        ibInfo = view.findViewById(R.id.ibInfo);
        swSign = view.findViewById(R.id.swSign);
        swEncrypt = view.findViewById(R.id.swEncrypt);
        swAutoDecrypt = view.findViewById(R.id.swAutoDecrypt);
        swAutoUndoDecrypt = view.findViewById(R.id.swAutoUndoDecrypt);

        spOpenPgp = view.findViewById(R.id.spOpenPgp);
        ibOpenKeychain = view.findViewById(R.id.ibOpenKeychain);
        tvOpenPgpStatus = view.findViewById(R.id.tvOpenPgpStatus);
        swAutocrypt = view.findViewById(R.id.swAutocrypt);
        swAutocryptMutual = view.findViewById(R.id.swAutocryptMutual);
        swEncryptSubject = view.findViewById(R.id.swEncryptSubject);

        spSignAlgoSmime = view.findViewById(R.id.spSignAlgoSmime);
        spEncryptAlgoSmime = view.findViewById(R.id.spEncryptAlgoSmime);
        swCheckCertificate = view.findViewById(R.id.swCheckCertificate);
        btnManageCertificates = view.findViewById(R.id.btnManageCertificates);
        btnImportKey = view.findViewById(R.id.btnImportKey);
        btnManageKeys = view.findViewById(R.id.btnManageKeys);
        btnCa = view.findViewById(R.id.btnCa);

        cardDebug = view.findViewById(R.id.cardDebug);
        tvKeySize = view.findViewById(R.id.tvKeySize);
        tvProviders = view.findViewById(R.id.tvProviders);

        try {
            openPgpProvider.clear();
            Intent intent = new Intent(OpenPgpApi.SERVICE_INTENT_2);
            List<ResolveInfo> ris = pm.queryIntentServices(intent, 0); // package whitelisted
            if (ris != null)
                for (ResolveInfo ri : ris)
                    if (ri.serviceInfo != null)
                        openPgpProvider.add(ri.serviceInfo.packageName);
        } catch (Throwable ex) {
            Log.e(ex);
        }

        if (BuildConfig.DEBUG)
            openPgpProvider.add("eu.faircode.test");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(openPgpProvider);
        spOpenPgp.setAdapter(adapter);

        setOptions();

        // Wire controls

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        ibInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.viewFAQ(v.getContext(), 12);
            }
        });

        swSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                prefs.edit().putBoolean("sign_default", checked).apply();
            }
        });

        swEncrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                prefs.edit().putBoolean("encrypt_default", checked).apply();
                swSign.setEnabled(!checked);
            }
        });

        swAutoDecrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                prefs.edit().putBoolean("auto_decrypt", checked).apply();
            }
        });

        swAutoUndoDecrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                prefs.edit().putBoolean("auto_undecrypt", checked).apply();
            }
        });

        // PGP

        spOpenPgp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String pkg = openPgpProvider.get(position);
                prefs.edit().putString("openpgp_provider", pkg).apply();

                String tag = (String) spOpenPgp.getTag();
                if (tag != null && !tag.equals(pkg)) {
                    spOpenPgp.setTag(pkg);
                    testOpenPgp(pkg);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prefs.edit().remove("openpgp_provider").apply();
            }
        });

        ibOpenKeychain.setVisibility(openPgpProvider.size() > 0 ? View.VISIBLE : View.INVISIBLE);
        ibOpenKeychain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pkg = (String) spOpenPgp.getTag();
                if (pkg == null)
                    return;

                PackageManager pm = v.getContext().getPackageManager();
                if (pm == null)
                    return;

                Intent intent = pm.getLaunchIntentForPackage(pkg);
                if (intent == null)
                    return;

                v.getContext().startActivity(intent);
            }
        });

        swAutocrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                prefs.edit().putBoolean("autocrypt", checked).apply();
                swAutocryptMutual.setEnabled(checked);
            }
        });

        swAutocryptMutual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                prefs.edit().putBoolean("autocrypt_mutual", checked).apply();
            }
        });

        swEncryptSubject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                prefs.edit().putBoolean("encrypt_subject", checked).apply();
            }
        });

        // S/MIME

        spSignAlgoSmime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String[] values = getResources().getStringArray(R.array.smimeSignAlgo);
                prefs.edit().putString("sign_algo_smime", values[position]).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prefs.edit().remove("sign_algo_smime").apply();
            }
        });

        spEncryptAlgoSmime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String[] values = getResources().getStringArray(R.array.smimeEncryptAlgo);
                prefs.edit().putString("encrypt_algo_smime", values[position]).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prefs.edit().remove("encrypt_algo_smime").apply();
            }
        });

        swCheckCertificate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                prefs.edit().putBoolean("check_certificate", checked).apply();
            }
        });

        btnManageCertificates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
                lbm.sendBroadcast(new Intent(ActivitySetup.ACTION_MANAGE_CERTIFICATES));
            }
        });

        final Intent importKey = KeyChain.createInstallIntent();
        btnImportKey.setEnabled(importKey.resolveActivity(pm) != null); // system whitelisted
        btnImportKey.setVisibility(Build.VERSION.SDK_INT < Build.VERSION_CODES.R ? View.VISIBLE : View.GONE);
        btnImportKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(importKey);
            }
        });

        final Intent security = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        btnImportKey.setEnabled(security.resolveActivity(pm) != null); // system whitelisted
        btnManageKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(security);
            }
        });

        btnCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleTask<List<String>>() {
                    @Override
                    protected List<String> onExecute(Context context, Bundle args) throws Throwable {
                        KeyStore ks = KeyStore.getInstance("AndroidCAStore");
                        ks.load(null, null);

                        List<String> issuers = new ArrayList<>();
                        Enumeration<String> aliases = ks.aliases();
                        while (aliases.hasMoreElements()) {
                            String alias = aliases.nextElement();
                            Certificate kcert = ks.getCertificate(alias);
                            if (kcert instanceof X509Certificate) {
                                Principal issuer = ((X509Certificate) kcert).getIssuerDN();
                                if (issuer != null) {
                                    String name = issuer.getName();
                                    if (name != null)
                                        issuers.add(name);
                                }
                            }
                        }

                        Collections.sort(issuers);
                        return issuers;
                    }

                    @Override
                    protected void onExecuted(Bundle args, List<String> issuers) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.title_advanced_ca)
                                .setMessage(TextUtils.join("\r\n", issuers))
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing
                                    }
                                })
                                .show();
                    }

                    @Override
                    protected void onException(Bundle args, Throwable ex) {
                        Log.unexpectedError(getParentFragmentManager(), ex);
                    }
                }.execute(FragmentOptionsEncryption.this, new Bundle(), "ca");
            }
        });

        // Initialize
        FragmentDialogTheme.setBackground(getContext(), view, false);

        boolean debug = prefs.getBoolean("debug", false);
        cardDebug.setVisibility(debug || BuildConfig.DEBUG ? View.VISIBLE : View.GONE);

        try {
            int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
            tvKeySize.setText(getString(R.string.title_advanced_aes_key_size,
                    Helper.humanReadableByteCount(maxKeySize, false)));
        } catch (NoSuchAlgorithmException ex) {
            tvKeySize.setText(Log.formatThrowable(ex));
        }

        tvProviders.setText(null);

        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean debug = prefs.getBoolean("debug", false);

        Bundle args = new Bundle();
        args.putBoolean("debug", debug);

        new SimpleTask<Spanned>() {
            @Override
            protected Spanned onExecute(Context context, Bundle args) {
                boolean debug = args.getBoolean("debug");
                SpannableStringBuilder ssb = new SpannableStringBuilderEx();

                int dp24 = Helper.dp2pixels(context, 24);

                Provider[] providers = Security.getProviders();
                for (int p = 0; p < providers.length; p++) {
                    Provider provider = providers[p];
                    ssb.append(Integer.toString(p + 1)).append(' ')
                            .append(provider.toString()).append('\n');
                    String info = provider.getInfo();
                    if (info != null) {
                        int line = ssb.length();
                        ssb.append(info).append('\n');
                        ssb.setSpan(new IndentSpan(dp24), line, ssb.length(), 0);
                        ssb.setSpan(new StyleSpan(Typeface.ITALIC), line, ssb.length(), 0);
                    }
                    if (debug) {
                        int start = ssb.length();
                        for (Enumeration<Object> e = provider.keys(); e.hasMoreElements(); ) {
                            int line = ssb.length();
                            ssb.append(e.nextElement().toString()).append('\n');
                            ssb.setSpan(new IndentSpan(dp24), line, ssb.length(), 0);
                        }
                        ssb.setSpan(new RelativeSizeSpan(HtmlHelper.FONT_SMALL), start, ssb.length(), 0);
                    }
                }

                return ssb;
            }

            @Override
            protected void onExecuted(Bundle args, Spanned providers) {
                tvProviders.setText(providers);
            }

            @Override
            protected void onException(Bundle args, Throwable ex) {
                tvProviders.setText(Log.formatThrowable(ex));
            }
        }.execute(this, args, "encryption:providers");
    }

    @Override
    public void onDestroyView() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);

        if (pgpService != null && pgpService.isBound()) {
            Log.i("PGP unbinding");
            pgpService.unbindFromService();
        }
        pgpService = null;

        super.onDestroyView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED))
            setOptions();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_options, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_default) {
            FragmentOptions.reset(getContext(), RESET_OPTIONS, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setOptions() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        swSign.setChecked(prefs.getBoolean("sign_default", false));
        swEncrypt.setChecked(prefs.getBoolean("encrypt_default", false));
        swSign.setEnabled(!swEncrypt.isChecked());
        swAutoDecrypt.setChecked(prefs.getBoolean("auto_decrypt", false));
        swAutoUndoDecrypt.setChecked(prefs.getBoolean("auto_undecrypt", false));

        String provider = prefs.getString("openpgp_provider", "org.sufficientlysecure.keychain");
        spOpenPgp.setTag(provider);
        for (int pos = 0; pos < openPgpProvider.size(); pos++)
            if (provider.equals(openPgpProvider.get(pos))) {
                spOpenPgp.setSelection(pos);
                break;
            }
        testOpenPgp(provider);

        swAutocrypt.setChecked(prefs.getBoolean("autocrypt", true));
        swAutocryptMutual.setChecked(prefs.getBoolean("autocrypt_mutual", true));
        swAutocryptMutual.setEnabled(swAutocrypt.isChecked());
        swEncryptSubject.setChecked(prefs.getBoolean("encrypt_subject", false));

        String signAlgorithm = prefs.getString("sign_algo_smime", "SHA-256");
        String[] smimeSignAlgo = getResources().getStringArray(R.array.smimeSignAlgo);
        for (int pos = 0; pos < smimeSignAlgo.length; pos++)
            if (smimeSignAlgo[pos].equals(signAlgorithm)) {
                spSignAlgoSmime.setSelection(pos);
                break;
            }

        String encryptAlgorithm = prefs.getString("encrypt_algo_smime", "AES-128");
        String[] smimeEncryptAlgo = getResources().getStringArray(R.array.smimeEncryptAlgo);
        for (int pos = 0; pos < smimeEncryptAlgo.length; pos++)
            if (smimeEncryptAlgo[pos].equals(encryptAlgorithm)) {
                spEncryptAlgoSmime.setSelection(pos);
                break;
            }

        swCheckCertificate.setChecked(prefs.getBoolean("check_certificate", true));
    }

    private void testOpenPgp(String pkg) {
        Log.i("Testing OpenPGP pkg=" + pkg);
        try {
            if (pgpService != null && pgpService.isBound())
                pgpService.unbindFromService();

            tvOpenPgpStatus.setText("Connecting to " + pkg);
            pgpService = new OpenPgpServiceConnection(getContext(), pkg, new OpenPgpServiceConnection.OnBound() {
                @Override
                public void onBound(IOpenPgpService2 service) {
                    tvOpenPgpStatus.setText("Connected to " + pkg);
                }

                @Override
                public void onError(Exception ex) {
                    if ("bindService() returned false!".equals(ex.getMessage()))
                        tvOpenPgpStatus.setText("Not connected");
                    else {
                        Log.e(ex);
                        tvOpenPgpStatus.setText(ex.toString());
                    }
                }
            });
            pgpService.bindToService();
        } catch (Throwable ex) {
            Log.e(ex);
        }
    }
}
