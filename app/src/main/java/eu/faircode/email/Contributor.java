package eu.faircode.email;

/*

*/

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Contributor {
    public String name;
    public String alias;
    public String contribution;

    private Contributor() {
    }

    static List<Contributor> loadContributors(Context context) {
        List<Contributor> result = new ArrayList<>();
        try {
            Contributor contributor = null;
            XmlResourceParser xml = context.getResources().getXml(R.xml.contributors);
            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String name = xml.getName();
                    if ("contributors".equals(name))
                        result = new ArrayList<>();
                    else if ("contributor".equals(name)) {
                        contributor = new Contributor();
                        contributor.name = xml.getAttributeValue(null, "name");
                        contributor.alias = xml.getAttributeValue(null, "alias");
                        contributor.contribution = xml.getAttributeValue(null, "contribution");
                    } else
                        throw new IllegalAccessException(name);
                } else if (eventType == XmlPullParser.END_TAG) {
                    if ("contributor".equals(xml.getName())) {
                        result.add(contributor);
                        contributor = null;
                    }
                }

                eventType = xml.next();
            }
        } catch (Throwable ex) {
            Log.e(ex);
        }

        final Collator collator = Collator.getInstance(Locale.getDefault());
        collator.setStrength(Collator.SECONDARY); // Case insensitive, process accents etc
        Collections.sort(result, new Comparator<Contributor>() {
            @Override
            public int compare(Contributor c1, Contributor c2) {
                String n1 = (TextUtils.isEmpty(c1.name) ? c1.alias : c1.name);
                String n2 = (TextUtils.isEmpty(c2.name) ? c2.alias : c2.name);
                return collator.compare(
                        n1 == null ? "" : n1,
                        n2 == null ? "" : n2);
            }
        });

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (!TextUtils.isEmpty(name))
            sb.append(name);

        if (!TextUtils.isEmpty(alias)) {
            if (sb.length() > 0)
                sb.append(' ');
            if (!TextUtils.isEmpty(name))
                sb.append('(');
            sb.append(alias);
            if (!TextUtils.isEmpty(name))
                sb.append(')');
        }

        sb.append(" - ");

        if (!TextUtils.isEmpty(contribution)) {
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(contribution);
        }

        return sb.toString();
    }
}
