package eu.faircode.email;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HtmlEx {
    private Context context;

    private static final int TO_HTML_PARAGRAPH_FLAG = 0x00000001;

    public HtmlEx(Context context){
        this.context = context;
    }

    /**
     * @deprecated use {@link #toHtml(Spanned, int)} instead.
     */
    @Deprecated
    public /* static */ String toHtml(Spanned text) {
        return toHtml(text, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
    }

    /**
     * Returns an HTML representation of the provided Spanned text. A best effort is
     * made to add HTML tags corresponding to spans. Also note that HTML metacharacters
     * (such as "&lt;" and "&amp;") within the input text are escaped.
     *
     * @param text input text to convert
     * @param option one of {@link #TO_HTML_PARAGRAPH_LINES_CONSECUTIVE} or
     *     {@link #TO_HTML_PARAGRAPH_LINES_INDIVIDUAL}
     * @return string containing input converted to HTML
     */
    public /* static */ String toHtml(Spanned text, int option) {
        if (!Helper.isUiThread())
            text = new SpannableStringBuilderEx(text);
        StringBuilder out = new StringBuilder();
        withinHtml(out, text, option);
        return out.toString();
    }

    /**
     * Returns an HTML escaped representation of the given plain text.
     */
    public /* static */ String escapeHtml(CharSequence text) {
        StringBuilder out = new StringBuilder();
        withinStyle(out, text, 0, text.length());
        return out.toString();
    }

    private /* static */ void withinHtml(StringBuilder out, Spanned text, int option) {
        if ((option & TO_HTML_PARAGRAPH_FLAG) == TO_HTML_PARAGRAPH_LINES_CONSECUTIVE) {
            encodeTextAlignmentByDiv(out, text, option);
            return;
        }

        Log.breadcrumb("withinHtml", "length", Integer.toString(text.length()));
        withinDiv(out, text, 0, text.length(), option);
    }

    private /* static */ void encodeTextAlignmentByDiv(StringBuilder out, Spanned text, int option) {
        int len = text.length();

        int next;
        for (int i = 0; i < len; i = next) {
            next = nextSpanTransition(text, i, len, ParagraphStyle.class);
            ParagraphStyle[] style = getSpans(text, i, next, ParagraphStyle.class);
            String elements = " ";
            boolean needDiv = false;

            for(int j = 0; j < style.length; j++) {
                if (style[j] instanceof AlignmentSpan) {
                    Layout.Alignment align =
                            ((AlignmentSpan) style[j]).getAlignment();
                    needDiv = true;
                    if (align == Layout.Alignment.ALIGN_CENTER) {
                        elements = "align=\"center\" " + elements;
                    } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
                        elements = "align=\"right\" " + elements;
                    } else {
                        elements = "align=\"left\" " + elements;
                    }
                }
            }
            if (needDiv) {
                out.append("<div ").append(elements).append(">");
            }

            withinDiv(out, text, i, next, option);

            if (needDiv) {
                out.append("</div>");
            }
        }
    }

    private /* static */ void withinDiv(StringBuilder out, Spanned text, int start, int end,
                                  int option) {
        int next;
        for (int i = start; i < end; i = next) {
            int n1 = nextSpanTransition(text, i, end, QuoteSpan.class);
            int n2 = nextSpanTransition(text, i, end, eu.faircode.email.IndentSpan.class);
            next = Math.min(n1, n2);
            try {
                List<Object> spans = new ArrayList<>();
                for (Object span : getSpans(text, i, next, LeadingMarginSpan.class))
                    if (span instanceof QuoteSpan ||
                            span instanceof eu.faircode.email.IndentSpan)
                        spans.add(span);

                for (Object span : spans) {
                    if (span instanceof QuoteSpan)
                        out.append("<blockquote style=\"")
                                .append(eu.faircode.email.HtmlHelper.getQuoteStyle(text, next, end))
                                .append("\">");
                    else if (span instanceof eu.faircode.email.IndentSpan)
                        out.append("<blockquote style=\"")
                                .append(eu.faircode.email.HtmlHelper.getIndentStyle(text, next, end))
                                .append("\">");
                }

                withinBlockquote(out, text, i, next, option);

                for (Object span : spans) {
                    out.append("</blockquote>\n");
                }
            } catch (Throwable ex) {
                Log.e("withinDiv " + start + "..." + end + "/" + text.length() +
                        " i=" + i + " n1=" + n1 + " n2=" + n2 +
                        "\n" + android.util.Log.getStackTraceString(ex));
                throw ex;
            }
        }
    }

    private /* static */ String getTextDirection(Spanned text, int start, int end) {
        try {
            if (TextDirectionHeuristics.FIRSTSTRONG_LTR.isRtl(text, start, end - start)) {
                return " dir=\"rtl\"";
            } else {
                return " dir=\"ltr\"";
            }
        } catch (Throwable ex) {
            eu.faircode.email.Log.e(ex);
            return "";
        }
    }

    private /* static */ String getTextStyles(Spanned text, int start, int end,
                                        boolean forceNoVerticalMargin, boolean includeTextAlign) {
        String margin = null;
        String textAlign = null;

        if (forceNoVerticalMargin) {
            margin = "margin-top:0; margin-bottom:0;";
        }
        if (includeTextAlign) {
            final AlignmentSpan[] alignmentSpans = getSpans(text, start, end, AlignmentSpan.class);

            // Only use the last AlignmentSpan with flag SPAN_PARAGRAPH
            for (int i = alignmentSpans.length - 1; i >= 0; i--) {
                AlignmentSpan s = alignmentSpans[i];
                if ((text.getSpanFlags(s) & Spanned.SPAN_PARAGRAPH) == Spanned.SPAN_PARAGRAPH) {
                    final Layout.Alignment alignment = s.getAlignment();
                    if (alignment == Layout.Alignment.ALIGN_NORMAL) {
                        textAlign = "text-align:start;";
                    } else if (alignment == Layout.Alignment.ALIGN_CENTER) {
                        textAlign = "text-align:center;";
                    } else if (alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                        textAlign = "text-align:end;";
                    }
                    break;
                }
            }
        }

        if (margin == null && textAlign == null) {
            return "";
        }

        final StringBuilder style = new StringBuilder(" style=\"");
        if (margin != null && textAlign != null) {
            style.append(margin).append(" ").append(textAlign);
        } else if (margin != null) {
            style.append(margin);
        } else if (textAlign != null) {
            style.append(textAlign);
        }

        return style.append("\"").toString();
    }

    private /* static */ void withinBlockquote(StringBuilder out, Spanned text, int start, int end,
                                         int option) {
        if ((option & TO_HTML_PARAGRAPH_FLAG) == TO_HTML_PARAGRAPH_LINES_CONSECUTIVE) {
            withinBlockquoteConsecutive(out, text, start, end);
        } else {
            withinBlockquoteIndividual(out, text, start, end);
        }
    }

    private /* static */ void withinBlockquoteIndividual(StringBuilder out, Spanned text, int start,
                                                   int end) {
        List<Boolean> levels = new ArrayList<>();

        int next = -1;
        for (int i = start; i <= end; i = next) {
            try {
                next = TextUtils.indexOf(text, '\n', i, end);
                if (next < 0) {
                    next = end;
                }

                if (next == i) {
                    if (levels.size() > 0) {
                        // Current paragraph is no longer a list item; close the previously opened list
                        for (int l = levels.size() - 1; l >= 0; l--)
                            out.append(levels.get(l) ? "</ul>\n" : "</ol>\n");
                        levels.clear();
                    }
                    if (i != text.length())
                        out.append("<br>\n");
                } else {
                    eu.faircode.email.LineSpan[] line = getSpans(text, i, next, eu.faircode.email.LineSpan.class);
                    if (line.length > 0) {
                        for (int l = 0; l < line.length; l++)
                            out.append("<hr>");
                        continue;
                    }

                    int level = 0;
                    Boolean isBulletListItem = null;
                    ParagraphStyle[] paragraphStyles = getSpans(text, i, next, ParagraphStyle.class);
                    for (ParagraphStyle paragraphStyle : paragraphStyles) {
                        final int spanFlags = text.getSpanFlags(paragraphStyle);
                        if ((spanFlags & Spanned.SPAN_PARAGRAPH) == Spanned.SPAN_PARAGRAPH
                                && paragraphStyle instanceof BulletSpan) {
                            isBulletListItem = !(paragraphStyle instanceof eu.faircode.email.NumberSpan);
                            if (paragraphStyle instanceof NumberSpan)
                                level = ((NumberSpan) paragraphStyle).getLevel();
                            else if (paragraphStyle instanceof BulletSpanEx)
                                level = ((BulletSpanEx) paragraphStyle).getLevel();
                            break;
                        }
                    }

                    if (isBulletListItem == null)
                        level = -1;

                    while (levels.size() > level + 1) {
                        Boolean bullet = levels.remove(levels.size() - 1);
                        out.append(bullet ? "</ul>\n" : "</ol>\n");
                    }
                    if (level >= 0 &&
                            levels.size() == level + 1 &&
                            levels.get(level) != isBulletListItem) {
                        Boolean bullet = levels.remove(level);
                        out.append(bullet ? "</ul>\n" : "</ol>\n");
                    }
                    while (levels.size() < level + 1) {
                        levels.add(isBulletListItem);
                        out.append(isBulletListItem ? "<ul" : "<ol")
                                .append(getTextStyles(text, i, next, true, false))
                                .append(">\n");
                    }

                    String tagType = isBulletListItem != null ? "li" : "span";
                    out.append("<").append(tagType)
                            .append(getTextDirection(text, i, next))
                            .append(getTextStyles(text, i, next, isBulletListItem == null, true))
                            .append(">");

                    withinParagraph(out, text, i, next);

                    out.append("</");
                    out.append(tagType);
                    out.append(">\n");
                    if (isBulletListItem == null)
                        out.append("<br>\n");

                    if (next == end && levels.size() > 0) {
                        for (int l = levels.size() - 1; l >= 0; l--)
                            out.append(levels.get(l) ? "</ul>\n" : "</ol>\n");
                        levels.clear();
                    }
                }

                next++;
            } catch (Throwable ex) {
                Log.e("withinBlockquoteIndividual " + start + "..." + end + "/" + text.length() +
                        " i=" + i + " next=" + next +
                        "\n" + android.util.Log.getStackTraceString(ex));
                throw ex;
            }
        }
    }

    private /* static */ void withinBlockquoteConsecutive(StringBuilder out, Spanned text, int start,
                                                    int end) {
        out.append("<span").append(getTextDirection(text, start, end)).append(">");

        int next;
        for (int i = start; i < end; i = next) {
            next = TextUtils.indexOf(text, '\n', i, end);
            if (next < 0) {
                next = end;
            }

            int nl = 0;

            while (next < end && text.charAt(next) == '\n') {
                nl++;
                next++;
            }

            withinParagraph(out, text, i, next - nl);

            if (nl == 0) {
                out.append("<br>\n");
            } else {
                for (int j = 0; j < nl; j++) {
                    out.append("<br>");
                }
                if (next != end) {
                    /* Paragraph should be closed and reopened */
                    out.append("</span>\n");
                    out.append("<span").append(getTextDirection(text, start, end)).append(">");
                }
            }
        }

        out.append("</span>\n");
    }

    private /* static */ void withinParagraph(StringBuilder out, Spanned text, int start, int end) {
        int next;
        for (int i = start; i < end; i = next) {
            next = nextSpanTransition(text, i, end, CharacterStyle.class);
            try {
                CharacterStyle[] style = getSpans(text, i, next, CharacterStyle.class);

                for (int j = 0; j < style.length; j++) {
                    if (style[j] instanceof StyleSpan) {
                        int s = ((StyleSpan) style[j]).getStyle();

                        if ((s & Typeface.BOLD) != 0) {
                            out.append("<b>");
                        }
                        if ((s & Typeface.ITALIC) != 0) {
                            out.append("<i>");
                        }
                    }
                    if (style[j] instanceof TypefaceSpan) {
                        String s = ((TypefaceSpan) style[j]).getFamily();

                        //if ("monospace".equals(s)) {
                        //    out.append("<tt>");
                        //}

                        out.append("<span style='font-family:" + s + ";'>");
                    }
                    if (style[j] instanceof SuperscriptSpan) {
                        out.append("<sup>");
                    }
                    if (style[j] instanceof SubscriptSpan) {
                        out.append("<sub>");
                    }
                    if (style[j] instanceof UnderlineSpan) {
                        out.append("<u>");
                    }
                    if (style[j] instanceof StyleHelper.MarkSpan) {
                        out.append("<mark>");
                    }
                    if (style[j] instanceof StrikethroughSpan) {
                        out.append("<span style=\"text-decoration:line-through;\">");
                    }
                    if (style[j] instanceof URLSpan) {
                        out.append("<a href=\"");
                        out.append(((URLSpan) style[j]).getURL());
                        out.append("\">");
                    }
                    if (style[j] instanceof AbsoluteSizeSpan) {
                        AbsoluteSizeSpan s = ((AbsoluteSizeSpan) style[j]);
                        float sizeDip = s.getSize();
                        if (!s.getDip()) {
                            //Application application = ActivityThread.currentApplication();
                            sizeDip /= context.getResources().getDisplayMetrics().density;
                        }

                        // px in CSS is the equivalance of dip in Android
                        out.append(String.format("<span style=\"font-size:%.0fpx\";>", sizeDip));
                    }
                    if (style[j] instanceof RelativeSizeSpan) {
                        float sizeEm = ((RelativeSizeSpan) style[j]).getSizeChange();
                        if (sizeEm < 1)
                            out.append(String.format("<span style=\"font-size:%s;\">",
                                    sizeEm < HtmlHelper.FONT_SMALL ? "x-small" : "small"));
                        else if (sizeEm > 1)
                            out.append(String.format("<span style=\"font-size:%s;\">",
                                    sizeEm > HtmlHelper.FONT_LARGE ? "x-large" : "large"));
                    }
                    if (style[j] instanceof ForegroundColorSpan) {
                        int color = ((ForegroundColorSpan) style[j]).getForegroundColor();
                        //out.append(String.format("<span style=\"color:#%06X;\">", 0xFFFFFF & color));
                        out.append(String.format("<span style=\"color:%s;\">",
                                eu.faircode.email.HtmlHelper.encodeWebColor(color)));
                    }
                    if (style[j] instanceof BackgroundColorSpan && !(style[j] instanceof StyleHelper.MarkSpan)) {
                        int color = ((BackgroundColorSpan) style[j]).getBackgroundColor();
                        //out.append(String.format("<span style=\"background-color:#%06X;\">",
                        //        0xFFFFFF & color));
                        out.append(String.format("<span style=\"background-color:%s;\">",
                                eu.faircode.email.HtmlHelper.encodeWebColor(color)));
                    }
                }

                for (int j = 0; j < style.length; j++) {
                    if (style[j] instanceof ImageSpan) {
                        out.append("<img src=\"");
                        out.append(((ImageSpan) style[j]).getSource());
                        out.append("\"");

                        if (style[j] instanceof ImageSpanEx) {
                            ImageSpanEx img = (ImageSpanEx) style[j];
                            int w = img.getWidth();
                            if (w > 0)
                                out.append(" width=\"").append(w).append("\"");
                            int h = img.getHeight();
                            if (h > 0)
                                out.append(" height=\"").append(h).append("\"");
                        }

                        out.append(">");

                        // Don't output the dummy character underlying the image.
                        i = next;
                    }
                }

                withinStyle(out, text, i, next);

                for (int j = style.length - 1; j >= 0; j--) {
                    if (style[j] instanceof BackgroundColorSpan && !(style[j] instanceof StyleHelper.MarkSpan)) {
                        out.append("</span>");
                    }
                    if (style[j] instanceof ForegroundColorSpan) {
                        out.append("</span>");
                    }
                    if (style[j] instanceof RelativeSizeSpan) {
                        out.append("</span>");
                    }
                    if (style[j] instanceof AbsoluteSizeSpan) {
                        out.append("</span>");
                    }
                    if (style[j] instanceof URLSpan) {
                        out.append("</a>");
                    }
                    if (style[j] instanceof StrikethroughSpan) {
                        out.append("</span>");
                    }
                    if (style[j] instanceof StyleHelper.MarkSpan) {
                        out.append("</mark>");
                    }
                    if (style[j] instanceof UnderlineSpan) {
                        out.append("</u>");
                    }
                    if (style[j] instanceof SubscriptSpan) {
                        out.append("</sub>");
                    }
                    if (style[j] instanceof SuperscriptSpan) {
                        out.append("</sup>");
                    }
                    if (style[j] instanceof TypefaceSpan) {
                        //String s = ((TypefaceSpan) style[j]).getFamily();

                        //if (s.equals("monospace")) {
                        //    out.append("</tt>");
                        //}

                        out.append("</span>");
                    }
                    if (style[j] instanceof StyleSpan) {
                        int s = ((StyleSpan) style[j]).getStyle();

                        if ((s & Typeface.BOLD) != 0) {
                            out.append("</b>");
                        }
                        if ((s & Typeface.ITALIC) != 0) {
                            out.append("</i>");
                        }
                    }
                }
            } catch (Throwable ex) {
                Log.e("withinParagraph " + start + "..." + end + "/" + text.length() +
                        " i=" + i + " next=" + next +
                        "\n" + android.util.Log.getStackTraceString(ex));
                throw ex;
            }
        }
    }

    //@UnsupportedAppUsage
    private /* static */ void withinStyle(StringBuilder out, CharSequence text,
                                    int start, int end) {
        for (int i = start; i < end; i++) {
            try {
                char c = text.charAt(i);

                if (c == '<') {
                    out.append("&lt;");
                } else if (c == '>') {
                    out.append("&gt;");
                } else if (c == '&') {
                    out.append("&amp;");
                } else if (c >= 0xD800 && c <= 0xDFFF) {
                    if (c < 0xDC00 && i + 1 < end) {
                        char d = text.charAt(i + 1);
                        if (d >= 0xDC00 && d <= 0xDFFF) {
                            i++;
                            int codepoint = 0x010000 | (int) c - 0xD800 << 10 | (int) d - 0xDC00;
                            out.append("&#").append(codepoint).append(";");
                        }
                    }
                } else if (c > 0x7E || c < ' ') {
                    out.append("&#").append((int) c).append(";");
                } else if (c == ' ') {
                    boolean img = (i - 1 >= 0 && text.charAt(i - i) == '\uFFFC');

                    while (i + 1 < end && text.charAt(i + 1) == ' ') {
                        out.append("&nbsp;");
                        i++;
                    }

                    out.append(img ? "&nbsp;" : ' ');
                } else {
                    out.append(c);
                }
            } catch (Throwable ex) {
                Log.e("withinStyle " + start + "..." + end + "/" + text.length() + " i=" + i +
                        "\n" + android.util.Log.getStackTraceString(ex));
                throw ex;
            }
        }
    }

    private static int nextSpanTransition(Spanned text, int start, int limit, Class type) {
        try {
            return text.nextSpanTransition(start, limit, type);
        } catch (Throwable ex) {
            Log.e(ex);
            return limit;
        }
    }

    private static <T> T[] getSpans(Spanned text, int start, int end, Class<T> type) {
        try {
            return text.getSpans(start, end, type);
        } catch (Throwable ex) {
            Log.e(ex);
            /*
                How can this happen?
                java.lang.ArrayStoreException: android.text.style.SpellCheckSpan cannot be stored in an array of type android.text.style.CharacterStyle[]
                    at android.text.SpannableStringBuilder.getSpansRec(SpannableStringBuilder.java:979)
                    at android.text.SpannableStringBuilder.getSpansRec(SpannableStringBuilder.java:946)
                    at android.text.SpannableStringBuilder.getSpansRec(SpannableStringBuilder.java:983)
                    at android.text.SpannableStringBuilder.getSpansRec(SpannableStringBuilder.java:946)
                    at android.text.SpannableStringBuilder.getSpans(SpannableStringBuilder.java:872)
                    at android.text.SpannableStringBuilder.getSpans(SpannableStringBuilder.java:842)
                    at androidx.emoji2.text.SpannableBuilder.getSpans(SourceFile:6)
                    at eu.faircode.email.HtmlEx.withinParagraph(SourceFile:2)
                    at eu.faircode.email.HtmlEx.withinBlockquoteIndividual(SourceFile:37)
                    at eu.faircode.email.HtmlEx.withinBlockquote(SourceFile:2)
                    at eu.faircode.email.HtmlEx.withinDiv(SourceFile:17)
                    at eu.faircode.email.HtmlEx.withinHtml(SourceFile:2)
                    at eu.faircode.email.HtmlEx.toHtml(SourceFile:3)
                    at eu.faircode.email.HtmlHelper.toHtml(SourceFile:2)
                    at eu.faircode.email.FragmentCompose$54.onExecute(SourceFile:18)
                    at eu.faircode.email.FragmentCompose$54.onExecute(SourceFile:1)
                    at eu.faircode.email.SimpleTask$2.run(SourceFile:5)
             */
            return (T[]) Array.newInstance(type, 0);
        }
    }
}
