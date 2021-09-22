package eu.faircode.email;

/*

*/

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;
import androidx.room.Ignore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.mail.Address;

public class TupleMessageEx extends EntityMessage {
    public Integer accountProtocol;
    public String accountName;
    public Integer accountColor;
    public boolean accountNotify;
    public boolean accountLeaveDeleted;
    public boolean accountAutoSeen;
    public String folderName;
    public Integer folderColor;
    public String folderDisplay;
    public String folderType;
    public boolean folderUnified;
    public boolean folderReadOnly;
    public String identityName;
    public String identityEmail;
    public Integer identityColor;
    public Boolean identitySynchronize;
    public Address[] senders;
    public Address[] recipients;
    public int count;
    public int unseen;
    public int unflagged;
    public int drafts;
    public int signed;
    public int encrypted;
    public int visible;
    public int visible_unseen;
    public Long totalSize;
    public Integer ui_priority;
    public Integer ui_importance;

    @Ignore
    boolean duplicate;

    @Ignore
    public Integer[] keyword_colors;
    @Ignore
    public String[] keyword_titles;

    String getFolderName(Context context) {
        return (folderDisplay == null
                ? EntityFolder.localizeName(context, folderName)
                : folderDisplay);
    }

    void resolveKeywordColors(Context context) {
        List<Integer> color = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        for (int i = 0; i < this.keywords.length; i++) {
            String keyword = this.keywords[i];

            String keyColor1 = "kwcolor." + keyword;
            String keyColor2 = "keyword." + keyword; // legacy
            if (prefs.contains(keyColor1))
                color.add(prefs.getInt(keyColor1, Color.GRAY));
            else if (prefs.contains(keyColor2))
                color.add(prefs.getInt(keyColor2, Color.GRAY));
            else
                color.add(null);

            String keyTitle = "kwtitle." + keyword;
            String def = TupleKeyword.getDefaultKeywordAlias(context, keyword);
            titles.add(prefs.getString(keyTitle, def));
        }

        this.keyword_colors = color.toArray(new Integer[0]);
        this.keyword_titles = titles.toArray(new String[0]);
    }

    String getRemark() {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageHelper.formatAddresses(from));
        if (!TextUtils.isEmpty(subject)) {
            if (sb.length() > 0)
                sb.append('\n');
            sb.append(subject);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TupleMessageEx) {
            TupleMessageEx other = (TupleMessageEx) obj;
            return (super.equals(obj) &&
                    this.accountProtocol.equals(other.accountProtocol) &&
                    Objects.equals(this.accountName, other.accountName) &&
                    Objects.equals(this.accountColor, other.accountColor) &&
                    this.accountNotify == other.accountNotify &&
                    this.accountLeaveDeleted == other.accountLeaveDeleted &&
                    this.accountAutoSeen == other.accountAutoSeen &&
                    this.folderName.equals(other.folderName) &&
                    Objects.equals(this.folderDisplay, other.folderDisplay) &&
                    this.folderType.equals(other.folderType) &&
                    this.folderUnified == other.folderUnified &&
                    this.folderReadOnly == other.folderReadOnly &&
                    Objects.equals(this.identityName, other.identityName) &&
                    Objects.equals(this.identityEmail, other.identityEmail) &&
                    Objects.equals(this.identityColor, other.identityColor) &&
                    Objects.equals(this.identitySynchronize, other.identitySynchronize) &&
                    MessageHelper.equal(this.senders, other.senders) &&
                    MessageHelper.equal(this.recipients, other.recipients) &&
                    this.count == other.count &&
                    this.unseen == other.unseen &&
                    this.unflagged == other.unflagged &&
                    this.drafts == other.drafts &&
                    this.signed == other.signed &&
                    this.encrypted == other.encrypted &&
                    this.visible == other.visible &&
                    this.visible_unseen == other.visible_unseen &&
                    Objects.equals(this.totalSize, other.totalSize) &&
                    Objects.equals(this.ui_priority, other.ui_priority) &&
                    Objects.equals(this.ui_importance, other.ui_importance) &&
                    this.duplicate == other.duplicate);
        }
        return false;
    }
}
