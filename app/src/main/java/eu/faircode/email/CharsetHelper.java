package eu.faircode.email;

/*
   
*/

import android.text.TextUtils;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CharsetHelper {
    private static final int MAX_SAMPLE_SIZE = 8192;
    private static String CHINESE = new Locale("zh").getLanguage();
    private static final List<String> COMMON = Collections.unmodifiableList(Arrays.asList(
            "US-ASCII", "ISO-8859-1", "ISO-8859-2", "windows-1250", "windows-1252", "windows-1257", "UTF-8"
    ));

    static {
        System.loadLibrary("fairemail");
    }

    private static native DetectResult jni_detect_charset(byte[] octets);

    static boolean isUTF8(String text) {
        // Get extended ASCII characters
        byte[] octets = text.getBytes(StandardCharsets.ISO_8859_1);
        return isUTF8(octets);
    }

    static boolean isUTF8(byte[] octets) {
        CharsetDecoder utf8Decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            utf8Decoder.decode(ByteBuffer.wrap(octets));
            return true;
        } catch (CharacterCodingException ex) {
            Log.w(ex);
            return false;
        }
    }

    public static Charset detect(String text) {
        try {
            byte[] octets = text.getBytes(StandardCharsets.ISO_8859_1);

            byte[] sample;
            if (octets.length < MAX_SAMPLE_SIZE)
                sample = octets;
            else {
                sample = new byte[MAX_SAMPLE_SIZE];
                System.arraycopy(octets, 0, sample, 0, MAX_SAMPLE_SIZE);
            }

            Log.i("compact_enc_det sample=" + sample.length);
            DetectResult detected = jni_detect_charset(sample);

            if (TextUtils.isEmpty(detected.charset)) {
                Log.e("compact_enc_det result=" + detected);
                return null;
            } else if (COMMON.contains(detected.charset))
                Log.w("compact_enc_det result=" + detected);
            else if ("GB18030".equals(detected.charset)) {
                boolean chinese = Locale.getDefault().getLanguage().equals(CHINESE);
                // https://github.com/google/compact_enc_det/issues/8
                Log.e("compact_enc_det result=" + detected + " chinese=" + chinese);
                if (!chinese)
                    return null;
            } else // GBK, Big5, ISO-2022-JP, HZ-GB-2312, Shift_JIS
                Log.e("compact_enc_det result=" + detected);

            return Charset.forName(detected.charset);
        } catch (Throwable ex) {
            Log.w(ex);
            return null;
        }
    }

    private static class DetectResult {
        String charset;
        int sample_size;
        int bytes_consumed;
        boolean is_reliable;

        DetectResult(String charset, int sample_size, int bytes_consumed, boolean is_reliable) {
            this.charset = charset;
            this.sample_size = sample_size;
            this.bytes_consumed = bytes_consumed;
            this.is_reliable = is_reliable;
        }

        @Override
        public String toString() {
            return charset + " s=" + bytes_consumed + "/" + sample_size + " r=" + is_reliable;
        }
    }
}
