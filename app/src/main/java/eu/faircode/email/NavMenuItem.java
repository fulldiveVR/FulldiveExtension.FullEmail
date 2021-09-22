package eu.faircode.email;

/*
   
*/

import java.util.Objects;

public class NavMenuItem {
    private int icon;
    private Integer color;
    private int title;
    private String subtitle = null;
    private int extraicon;
    private Integer count = null;
    private boolean warning = false;
    private boolean separated = false;
    private Runnable click;
    private Runnable longClick;

    NavMenuItem(int icon, int title, Runnable click) {
        this.icon = icon;
        this.title = title;
        this.click = click;
    }

    NavMenuItem(int icon, int title, Runnable click, Runnable longClick) {
        this.icon = icon;
        this.title = title;
        this.click = click;
        this.longClick = longClick;
    }

    NavMenuItem setColor(Integer color) {
        this.color = color;
        return this;
    }

    NavMenuItem setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    NavMenuItem setExtraIcon(int icon) {
        this.extraicon = icon;
        return this;
    }

    void setCount(Integer count) {
        if (count != null && count == 0)
            count = null;
        this.count = count;
    }

    NavMenuItem setExternal(boolean external) {
        setExtraIcon(external ? R.drawable.twotone_open_in_new_24 : 0);
        return this;
    }

    NavMenuItem setWarning(boolean warning) {
        this.warning = warning;
        return this;
    }

    NavMenuItem setSeparated() {
        this.separated = true;
        return this;
    }

    int getIcon() {
        return this.icon;
    }

    Integer getColor() {
        return this.color;
    }

    int getTitle() {
        return this.title;
    }

    String getSubtitle() {
        return this.subtitle;
    }

    int getExtraIcon() {
        return this.extraicon;
    }

    Integer getCount() {
        return this.count;
    }

    boolean isSeparated() {
        return this.separated;
    }

    boolean hasWarning() {
        return this.warning;
    }

    void onClick() {
        try {
            click.run();
        } catch (Throwable ex) {
            Log.e(ex);
        }
    }

    boolean onLongClick() {
        try {
            if (longClick != null)
                longClick.run();
            return (longClick != null);
        } catch (Throwable ex) {
            Log.e(ex);
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof NavMenuItem) {
            NavMenuItem other = (NavMenuItem) object;
            return (this.icon == other.icon &&
                    Objects.equals(this.color, other.color) &&
                    this.title == other.title &&
                    Objects.equals(this.subtitle, other.subtitle) &&
                    this.extraicon == other.extraicon &&
                    Objects.equals(this.count, other.count) &&
                    this.warning == other.warning &&
                    this.separated == other.separated);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(icon, color, title, subtitle, extraicon, count, warning, separated);
    }
}
