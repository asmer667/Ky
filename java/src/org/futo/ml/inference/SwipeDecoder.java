package org.futo.ml.inference;

import androidx.compose.animation.Scale$$ExternalSyntheticOutline0;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.collections.ArraysKt;
import kotlin.collections.EmptyList;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.RegexKt;
import kotlin.uuid.UuidKt;
import org.mozc.android.inputmethod.japanese.protobuf.ProtoCommands;

/* loaded from: classes.dex */
public final class SwipeDecoder implements AutoCloseable {
    public static final Companion Companion = new Companion(null);
    private static String defaultAffinity;
    private static final Set<Long> pinnedThreads;
    private final List<String> contextWords;
    private long handle;

    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public static /* synthetic */ void getDefaultAffinity$annotations() {
        }

        private final int nativePinCores(String str) {
            return SwipeDecoder.nativePinCores(str);
        }

        private final void nativeSetCaseMapping(int[] iArr, int[] iArr2) {
            SwipeDecoder.nativeSetCaseMapping(iArr, iArr2);
        }

        public final String getDefaultAffinity() {
            return SwipeDecoder.defaultAffinity;
        }

        /* JADX WARN: Type inference failed for: r6v3, types: [java.io.OutputStream, java.io.ByteArrayOutputStream, kotlin.io.ExposingBufferByteArrayOutputStream] */
        public final void installCaseMappingFor(String vocabPath) {
            int i;
            int lowerCase;
            int i2;
            boolean z;
            Intrinsics.checkNotNullParameter(vocabPath, "vocabPath");
            File file = new File(vocabPath);
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                long length = file.length();
                if (length <= 2147483647L) {
                    int i3 = (int) length;
                    byte[] bArr = new byte[i3];
                    int i4 = 0;
                    int i5 = i3;
                    int i6 = 0;
                    while (i5 > 0) {
                        int read = fileInputStream.read(bArr, i6, i5);
                        if (read < 0) {
                            break;
                        }
                        i5 -= read;
                        i6 += read;
                    }
                    if (i5 > 0) {
                        bArr = Arrays.copyOf(bArr, i6);
                        Intrinsics.checkNotNullExpressionValue(bArr, "copyOf(...)");
                    } else {
                        int read2 = fileInputStream.read();
                        if (read2 != -1) {
                            ?? byteArrayOutputStream = new ByteArrayOutputStream(8193);
                            byteArrayOutputStream.write(read2);
                            UuidKt.copyTo(fileInputStream, byteArrayOutputStream, 8192);
                            int size = byteArrayOutputStream.size() + i3;
                            if (size >= 0) {
                                byte[] buffer = byteArrayOutputStream.getBuffer();
                                bArr = Arrays.copyOf(bArr, size);
                                Intrinsics.checkNotNullExpressionValue(bArr, "copyOf(...)");
                                ArraysKt.copyInto(i3, 0, byteArrayOutputStream.size(), buffer, bArr);
                            } else {
                                throw new OutOfMemoryError("File " + file + " is too big to fit in memory.");
                            }
                        }
                    }
                    fileInputStream.close();
                    boolean[] zArr = new boolean[65536];
                    int length2 = bArr.length;
                    int i7 = 0;
                    while (true) {
                        if (i7 >= length2) {
                            break;
                        }
                        byte b = bArr[i7];
                        int i8 = b & 255;
                        if (i8 >= 128) {
                            if ((b & 224) == 192) {
                                i2 = 1;
                            } else if ((b & 240) == 224) {
                                i2 = 2;
                            } else if ((b & 248) == 240) {
                                i2 = 3;
                            }
                            if (i7 + i2 >= length2) {
                                break;
                            }
                            int i9 = i2 + 1;
                            int i10 = i8 & (255 >>> i9);
                            if (1 <= i2) {
                                int i11 = 1;
                                while (true) {
                                    byte b2 = bArr[i7 + i11];
                                    if ((b2 & 192) != 128) {
                                        z = false;
                                        break;
                                    }
                                    i10 = (i10 << 6) | (b2 & 63);
                                    if (i11 == i2) {
                                        break;
                                    } else {
                                        i11++;
                                    }
                                }
                                if (z && i10 < 65536) {
                                    zArr[i10] = true;
                                }
                                i7 += i9;
                            }
                            z = true;
                            if (z) {
                                zArr[i10] = true;
                            }
                            i7 += i9;
                        }
                        i7++;
                    }
                    int i12 = 0;
                    for (int i13 = 128; i13 < 65536; i13++) {
                        if (zArr[i13] && Character.toLowerCase(i13) != i13) {
                            i12++;
                        }
                    }
                    int[] iArr = new int[i12];
                    int[] iArr2 = new int[i12];
                    for (i = ProtoCommands.KeyEvent.ModifierKey.LEFT_SHIFT_VALUE; i < 65536; i++) {
                        if (zArr[i] && (lowerCase = Character.toLowerCase(i)) != i) {
                            iArr[i4] = i;
                            iArr2[i4] = lowerCase;
                            i4++;
                        }
                    }
                    nativeSetCaseMapping(iArr, iArr2);
                    return;
                }
                throw new OutOfMemoryError("File " + file + " is too big (" + length + " bytes) to fit in memory.");
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    RegexKt.closeFinally(fileInputStream, th);
                    throw th2;
                }
            }
        }

        public final int pinCores(String affinity) {
            Intrinsics.checkNotNullParameter(affinity, "affinity");
            return nativePinCores(affinity);
        }

        public final void pinCoresOnce$futo_swipe_release() {
        }

        public final void setDefaultAffinity(String str) {
            Intrinsics.checkNotNullParameter(str, "<set-?>");
            SwipeDecoder.defaultAffinity = str;
        }

        private Companion() {
        }
    }

    /* loaded from: classes.dex */
    public static final class Result {
        private final float ctcScore;
        private final float lmScore;
        private final float score;
        private final String word;

        public Result(String word, float f, float f2, float f3) {
            Intrinsics.checkNotNullParameter(word, "word");
            this.word = word;
            this.score = f;
            this.ctcScore = f2;
            this.lmScore = f3;
        }

        public static /* synthetic */ Result copy$default(Result result, String str, float f, float f2, float f3, int i, Object obj) {
            if ((i & 1) != 0) {
                str = result.word;
            }
            if ((i & 2) != 0) {
                f = result.score;
            }
            if ((i & 4) != 0) {
                f2 = result.ctcScore;
            }
            if ((i & 8) != 0) {
                f3 = result.lmScore;
            }
            return result.copy(str, f, f2, f3);
        }

        public final String component1() {
            return this.word;
        }

        public final float component2() {
            return this.score;
        }

        public final float component3() {
            return this.ctcScore;
        }

        public final float component4() {
            return this.lmScore;
        }

        public final Result copy(String word, float f, float f2, float f3) {
            Intrinsics.checkNotNullParameter(word, "word");
            return new Result(word, f, f2, f3);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Result)) {
                return false;
            }
            Result result = (Result) obj;
            return Intrinsics.areEqual(this.word, result.word) && Float.compare(this.score, result.score) == 0 && Float.compare(this.ctcScore, result.ctcScore) == 0 && Float.compare(this.lmScore, result.lmScore) == 0;
        }

        public final float getCtcScore() {
            return this.ctcScore;
        }

        public final float getLmScore() {
            return this.lmScore;
        }

        public final float getScore() {
            return this.score;
        }

        public final String getWord() {
            return this.word;
        }

        public int hashCode() {
            return Float.hashCode(this.lmScore) + Scale$$ExternalSyntheticOutline0.m(this.ctcScore, Scale$$ExternalSyntheticOutline0.m(this.score, this.word.hashCode() * 31, 31), 31);
        }

        public String toString() {
            return "Result(word=" + this.word + ", score=" + this.score + ", ctcScore=" + this.ctcScore + ", lmScore=" + this.lmScore + ")";
        }

        public /* synthetic */ Result(String str, float f, float f2, float f3, int i, DefaultConstructorMarker defaultConstructorMarker) {
            this(str, f, (i & 4) != 0 ? 0.0f : f2, (i & 8) != 0 ? 0.0f : f3);
        }
    }

    /* loaded from: classes.dex */
    public static final class Scoring {
        private final float alpha;
        private final float beta;
        private final float gamma;
        private final float lambda;

        public Scoring(float f, float f2, float f3, float f4) {
            this.gamma = f;
            this.lambda = f2;
            this.beta = f3;
            this.alpha = f4;
        }

        public static /* synthetic */ Scoring copy$default(Scoring scoring, float f, float f2, float f3, float f4, int i, Object obj) {
            if ((i & 1) != 0) {
                f = scoring.gamma;
            }
            if ((i & 2) != 0) {
                f2 = scoring.lambda;
            }
            if ((i & 4) != 0) {
                f3 = scoring.beta;
            }
            if ((i & 8) != 0) {
                f4 = scoring.alpha;
            }
            return scoring.copy(f, f2, f3, f4);
        }

        public final float component1() {
            return this.gamma;
        }

        public final float component2() {
            return this.lambda;
        }

        public final float component3() {
            return this.beta;
        }

        public final float component4() {
            return this.alpha;
        }

        public final Scoring copy(float f, float f2, float f3, float f4) {
            return new Scoring(f, f2, f3, f4);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Scoring)) {
                return false;
            }
            Scoring scoring = (Scoring) obj;
            return Float.compare(this.gamma, scoring.gamma) == 0 && Float.compare(this.lambda, scoring.lambda) == 0 && Float.compare(this.beta, scoring.beta) == 0 && Float.compare(this.alpha, scoring.alpha) == 0;
        }

        public final float getAlpha() {
            return this.alpha;
        }

        public final float getBeta() {
            return this.beta;
        }

        public final float getGamma() {
            return this.gamma;
        }

        public final float getLambda() {
            return this.lambda;
        }

        public int hashCode() {
            return Float.hashCode(this.alpha) + Scale$$ExternalSyntheticOutline0.m(this.beta, Scale$$ExternalSyntheticOutline0.m(this.lambda, Float.hashCode(this.gamma) * 31, 31), 31);
        }

        public String toString() {
            return "Scoring(gamma=" + this.gamma + ", lambda=" + this.lambda + ", beta=" + this.beta + ", alpha=" + this.alpha + ")";
        }
    }

    /* loaded from: classes.dex */
    public static final class SwipeSeg {
        private final float[] t;
        private final float[] x;
        private final float[] y;

        public SwipeSeg(float[] x, float[] y, float[] t) {
            Intrinsics.checkNotNullParameter(x, "x");
            Intrinsics.checkNotNullParameter(y, "y");
            Intrinsics.checkNotNullParameter(t, "t");
            this.x = x;
            this.y = y;
            this.t = t;
        }

        public static /* synthetic */ SwipeSeg copy$default(SwipeSeg swipeSeg, float[] fArr, float[] fArr2, float[] fArr3, int i, Object obj) {
            if ((i & 1) != 0) {
                fArr = swipeSeg.x;
            }
            if ((i & 2) != 0) {
                fArr2 = swipeSeg.y;
            }
            if ((i & 4) != 0) {
                fArr3 = swipeSeg.t;
            }
            return swipeSeg.copy(fArr, fArr2, fArr3);
        }

        public final float[] component1() {
            return this.x;
        }

        public final float[] component2() {
            return this.y;
        }

        public final float[] component3() {
            return this.t;
        }

        public final SwipeSeg copy(float[] x, float[] y, float[] t) {
            Intrinsics.checkNotNullParameter(x, "x");
            Intrinsics.checkNotNullParameter(y, "y");
            Intrinsics.checkNotNullParameter(t, "t");
            return new SwipeSeg(x, y, t);
        }

        public boolean equals(Object obj) {
            Class<?> cls;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                cls = obj.getClass();
            } else {
                cls = null;
            }
            if (!SwipeSeg.class.equals(cls)) {
                return false;
            }
            Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type org.futo.ml.inference.SwipeDecoder.SwipeSeg");
            SwipeSeg swipeSeg = (SwipeSeg) obj;
            if (Arrays.equals(this.x, swipeSeg.x) && Arrays.equals(this.y, swipeSeg.y) && Arrays.equals(this.t, swipeSeg.t)) {
                return true;
            }
            return false;
        }

        public final float[] getT() {
            return this.t;
        }

        public final float[] getX() {
            return this.x;
        }

        public final float[] getY() {
            return this.y;
        }

        public int hashCode() {
            return Arrays.hashCode(this.t) + ((Arrays.hashCode(this.y) + (Arrays.hashCode(this.x) * 31)) * 31);
        }

        public String toString() {
            String arrays = Arrays.toString(this.x);
            String arrays2 = Arrays.toString(this.y);
            String arrays3 = Arrays.toString(this.t);
            StringBuilder sb = new StringBuilder("SwipeSeg(x=");
            sb.append(arrays);
            sb.append(", y=");
            sb.append(arrays2);
            sb.append(", t=");
            return Scale$$ExternalSyntheticOutline0.m(sb, arrays3, ")");
        }
    }

    /* loaded from: classes.dex */
    public static final class Timing {
        private final float beamUs;
        private final float decoderUs;
        private final float encoderUs;
        private final float lmUs;
        private final float resampleUs;
        private final float totalUs;

        public Timing() {
            this(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 63, null);
        }

        public static /* synthetic */ Timing copy$default(Timing timing, float f, float f2, float f3, float f4, float f5, float f6, int i, Object obj) {
            if ((i & 1) != 0) {
                f = timing.resampleUs;
            }
            if ((i & 2) != 0) {
                f2 = timing.encoderUs;
            }
            if ((i & 4) != 0) {
                f3 = timing.decoderUs;
            }
            if ((i & 8) != 0) {
                f4 = timing.beamUs;
            }
            if ((i & 16) != 0) {
                f5 = timing.lmUs;
            }
            if ((i & 32) != 0) {
                f6 = timing.totalUs;
            }
            float f7 = f5;
            float f8 = f6;
            return timing.copy(f, f2, f3, f4, f7, f8);
        }

        public final float component1() {
            return this.resampleUs;
        }

        public final float component2() {
            return this.encoderUs;
        }

        public final float component3() {
            return this.decoderUs;
        }

        public final float component4() {
            return this.beamUs;
        }

        public final float component5() {
            return this.lmUs;
        }

        public final float component6() {
            return this.totalUs;
        }

        public final Timing copy(float f, float f2, float f3, float f4, float f5, float f6) {
            return new Timing(f, f2, f3, f4, f5, f6);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Timing)) {
                return false;
            }
            Timing timing = (Timing) obj;
            return Float.compare(this.resampleUs, timing.resampleUs) == 0 && Float.compare(this.encoderUs, timing.encoderUs) == 0 && Float.compare(this.decoderUs, timing.decoderUs) == 0 && Float.compare(this.beamUs, timing.beamUs) == 0 && Float.compare(this.lmUs, timing.lmUs) == 0 && Float.compare(this.totalUs, timing.totalUs) == 0;
        }

        public final float getBeamUs() {
            return this.beamUs;
        }

        public final float getDecoderUs() {
            return this.decoderUs;
        }

        public final float getEncoderUs() {
            return this.encoderUs;
        }

        public final float getLmUs() {
            return this.lmUs;
        }

        public final float getResampleUs() {
            return this.resampleUs;
        }

        public final float getTotalUs() {
            return this.totalUs;
        }

        public int hashCode() {
            return Float.hashCode(this.totalUs) + Scale$$ExternalSyntheticOutline0.m(this.lmUs, Scale$$ExternalSyntheticOutline0.m(this.beamUs, Scale$$ExternalSyntheticOutline0.m(this.decoderUs, Scale$$ExternalSyntheticOutline0.m(this.encoderUs, Float.hashCode(this.resampleUs) * 31, 31), 31), 31), 31);
        }

        public String toString() {
            return "Timing(resampleUs=" + this.resampleUs + ", encoderUs=" + this.encoderUs + ", decoderUs=" + this.decoderUs + ", beamUs=" + this.beamUs + ", lmUs=" + this.lmUs + ", totalUs=" + this.totalUs + ")";
        }

        public Timing(float f, float f2, float f3, float f4, float f5, float f6) {
            this.resampleUs = f;
            this.encoderUs = f2;
            this.decoderUs = f3;
            this.beamUs = f4;
            this.lmUs = f5;
            this.totalUs = f6;
        }

        public /* synthetic */ Timing(float f, float f2, float f3, float f4, float f5, float f6, int i, DefaultConstructorMarker defaultConstructorMarker) {
            this((i & 1) != 0 ? 0.0f : f, (i & 2) != 0 ? 0.0f : f2, (i & 4) != 0 ? 0.0f : f3, (i & 8) != 0 ? 0.0f : f4, (i & 16) != 0 ? 0.0f : f5, (i & 32) != 0 ? 0.0f : f6);
        }
    }

    static {
        System.loadLibrary("swipe_jni");
        defaultAffinity = "little";
        pinnedThreads = Collections.newSetFromMap(new ConcurrentHashMap());
    }

    public SwipeDecoder(String encoderPath, String str, int i, int i2, int i3, boolean z, String freqKey, String str2, String str3) {
        Intrinsics.checkNotNullParameter(encoderPath, "encoderPath");
        Intrinsics.checkNotNullParameter(freqKey, "freqKey");
        this.contextWords = new ArrayList();
        long nativeInit = nativeInit(encoderPath, str, i, i2, i3, z, freqKey, str2, str3);
        this.handle = nativeInit;
        if (nativeInit == 0) {
            throw new IllegalArgumentException("Failed to initialize SwipeDecoder from ".concat(encoderPath).toString());
        }
    }

    public static final String getDefaultAffinity() {
        return Companion.getDefaultAffinity();
    }

    public static final void installCaseMappingFor(String str) {
        Companion.installCaseMappingFor(str);
    }

    private final native void nativeDestroy(long j);

    private final native float[] nativeGetLastTiming(long j);

    private final native float[] nativeGetScoring(long j);

    private final native boolean nativeHasDecoder(long j);

    private final native boolean nativeHasLm(long j);

    private final native long nativeInit(String str, String str2, int i, int i2, int i3, boolean z, String str3, String str4, String str5);

    public static final native int nativePinCores(String str);

    private final native Result[] nativePredictNext(long j, String[] strArr, int i);

    private final native Result[] nativeRecognize(long j, SwipeSeg[] swipeSegArr, SwipeSeg[] swipeSegArr2, int i, String[] strArr, int i2, float[] fArr);

    public static final native void nativeSetCaseMapping(int[] iArr, int[] iArr2);

    private final native boolean nativeSetMode(long j, String str, float[] fArr, float[] fArr2, long[] jArr, String str2, String str3, String str4);

    public static final int pinCores(String str) {
        return Companion.pinCores(str);
    }

    public static /* synthetic */ List predictNext$default(SwipeDecoder swipeDecoder, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = 10;
        }
        return swipeDecoder.predictNext(i);
    }

    public static /* synthetic */ List recognize$default(SwipeDecoder swipeDecoder, SwipeSeg[] swipeSegArr, SwipeSeg[] swipeSegArr2, int i, int i2, float[] fArr, int i3, Object obj) {
        if ((i3 & 4) != 0) {
            i = 4;
        }
        int i4 = i;
        if ((i3 & 8) != 0) {
            i2 = 100;
        }
        int i5 = i2;
        if ((i3 & 16) != 0) {
            fArr = null;
        }
        return swipeDecoder.recognize(swipeSegArr, swipeSegArr2, i4, i5, fArr);
    }

    public static final void setDefaultAffinity(String str) {
        Companion.setDefaultAffinity(str);
    }

    public static /* synthetic */ boolean setMode$default(SwipeDecoder swipeDecoder, String str, float[] fArr, float[] fArr2, long[] jArr, String str2, String str3, String str4, int i, Object obj) {
        if ((i & 1) != 0) {
            str = null;
        }
        if ((i & 2) != 0) {
            fArr = null;
        }
        if ((i & 4) != 0) {
            fArr2 = null;
        }
        if ((i & 8) != 0) {
            jArr = null;
        }
        if ((i & 16) != 0) {
            str2 = null;
        }
        if ((i & 32) != 0) {
            str3 = null;
        }
        if ((i & 64) != 0) {
            str4 = null;
        }
        return swipeDecoder.setMode(str, fArr, fArr2, jArr, str2, str3, str4);
    }

    public final void addContext(String word) {
        Intrinsics.checkNotNullParameter(word, "word");
        if (word.length() == 0) {
            return;
        }
        List<String> list = this.contextWords;
        Locale ROOT = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue(ROOT, "ROOT");
        String lowerCase = word.toLowerCase(ROOT);
        Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
        list.add(lowerCase);
    }

    public final void clearContext() {
        this.contextWords.clear();
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        long j = this.handle;
        if (j != 0) {
            nativeDestroy(j);
            this.handle = 0L;
        }
    }

    public final Scoring getScoring() {
        long j = this.handle;
        if (j != 0) {
            float[] nativeGetScoring = nativeGetScoring(j);
            if (nativeGetScoring == null) {
                return new Scoring(0.0f, 0.0f, 0.0f, 0.0f);
            }
            return new Scoring(nativeGetScoring[0], nativeGetScoring[1], nativeGetScoring[2], nativeGetScoring[3]);
        }
        throw new IllegalStateException("SwipeDecoder has been closed");
    }

    public final boolean hasDecoder() {
        long j = this.handle;
        if (j != 0) {
            return nativeHasDecoder(j);
        }
        throw new IllegalStateException("SwipeDecoder has been closed");
    }

    public final boolean hasLm() {
        long j = this.handle;
        if (j != 0) {
            return nativeHasLm(j);
        }
        throw new IllegalStateException("SwipeDecoder has been closed");
    }

    public final Timing lastTiming() {
        long j = this.handle;
        if (j != 0) {
            float[] nativeGetLastTiming = nativeGetLastTiming(j);
            if (nativeGetLastTiming == null) {
                return new Timing(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 63, null);
            }
            return new Timing(nativeGetLastTiming[0], nativeGetLastTiming[1], nativeGetLastTiming[2], nativeGetLastTiming[3], nativeGetLastTiming[4], nativeGetLastTiming[5]);
        }
        throw new IllegalStateException("SwipeDecoder has been closed");
    }

    public final List<Result> predictNext(int i) {
        String[] strArr;
        if (this.handle != 0) {
            if (i > 0) {
                if (!this.contextWords.isEmpty()) {
                    strArr = (String[]) this.contextWords.toArray(new String[0]);
                } else {
                    strArr = null;
                }
                Result[] nativePredictNext = nativePredictNext(this.handle, strArr, i);
                if (nativePredictNext != null) {
                    return ArraysKt.toList(nativePredictNext);
                }
                return EmptyList.INSTANCE;
            }
            throw new IllegalArgumentException(Scale$$ExternalSyntheticOutline0.m(i, "topK must be positive, got ").toString());
        }
        throw new IllegalStateException("SwipeDecoder has been closed");
    }

    public final List<Result> recognize(SwipeSeg[] leftSegs, SwipeSeg[] rightSegs, int i, int i2, float[] fArr) {
        Intrinsics.checkNotNullParameter(leftSegs, "leftSegs");
        Intrinsics.checkNotNullParameter(rightSegs, "rightSegs");
        if (this.handle != 0) {
            if (leftSegs.length == 0) {
                if (rightSegs.length == 0) {
                    throw new IllegalArgumentException("must have at least one segment");
                }
            }
            if (i > 0) {
                Result[] nativeRecognize = nativeRecognize(this.handle, leftSegs, rightSegs, i, !this.contextWords.isEmpty() ? (String[]) this.contextWords.toArray(new String[0]) : null, i2, fArr);
                return nativeRecognize != null ? ArraysKt.toList(nativeRecognize) : EmptyList.INSTANCE;
            }
            throw new IllegalArgumentException(Scale$$ExternalSyntheticOutline0.m(i, "topK must be positive, got ").toString());
        }
        throw new IllegalStateException("SwipeDecoder has been closed");
    }

    public final void setContext(List<String> words) {
        Intrinsics.checkNotNullParameter(words, "words");
        this.contextWords.clear();
        for (String str : words) {
            if (str.length() > 0) {
                List<String> list = this.contextWords;
                Locale ROOT = Locale.ROOT;
                Intrinsics.checkNotNullExpressionValue(ROOT, "ROOT");
                String lowerCase = str.toLowerCase(ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
                list.add(lowerCase);
            }
        }
    }

    public final boolean setMode(String str, float[] fArr, float[] fArr2, long[] jArr, String str2, String str3, String str4) {
        if (this.handle != 0) {
            if (str != null || fArr != null || fArr2 != null) {
                if (str != null && fArr != null && fArr2 != null) {
                    if (fArr.length == fArr2.length) {
                        if (str.codePointCount(0, str.length()) != fArr.length) {
                            throw new IllegalArgumentException(("setMode: letters codepoint count (" + str.codePointCount(0, str.length()) + ") must match cx/cy size (" + fArr.length + ")").toString());
                        }
                    } else {
                        throw new IllegalArgumentException(("setMode: cx and cy must have same size (" + fArr.length + " != " + fArr2.length + ")").toString());
                    }
                } else {
                    throw new IllegalArgumentException("setMode: letters, cx, cy must be provided together");
                }
            }
            return nativeSetMode(this.handle, str, fArr, fArr2, jArr, str2, str3, str4);
        }
        throw new IllegalStateException("SwipeDecoder has been closed");
    }

    public static /* synthetic */ List recognize$default(SwipeDecoder swipeDecoder, float[] fArr, float[] fArr2, float[] fArr3, int i, int i2, float[] fArr4, int i3, Object obj) {
        if ((i3 & 8) != 0) {
            i = 4;
        }
        int i4 = i;
        if ((i3 & 16) != 0) {
            i2 = 100;
        }
        int i5 = i2;
        if ((i3 & 32) != 0) {
            fArr4 = null;
        }
        return swipeDecoder.recognize(fArr, fArr2, fArr3, i4, i5, fArr4);
    }

    public /* synthetic */ SwipeDecoder(String str, String str2, int i, int i2, int i3, boolean z, String str3, String str4, String str5, int i4, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, (i4 & 2) != 0 ? null : str2, (i4 & 4) != 0 ? 1 : i, (i4 & 8) != 0 ? 100 : i2, (i4 & 16) != 0 ? 4 : i3, (i4 & 32) != 0 ? true : z, (i4 & 64) != 0 ? "f" : str3, (i4 & ProtoCommands.KeyEvent.ModifierKey.LEFT_SHIFT_VALUE) != 0 ? null : str4, (i4 & ProtoCommands.KeyEvent.ModifierKey.RIGHT_CTRL_VALUE) != 0 ? null : str5);
    }

    public final List<Result> recognize(float[] x, float[] y, float[] t, int i, int i2, float[] fArr) {
        Intrinsics.checkNotNullParameter(x, "x");
        Intrinsics.checkNotNullParameter(y, "y");
        Intrinsics.checkNotNullParameter(t, "t");
        return recognize(new SwipeSeg[]{new SwipeSeg(x, y, t)}, new SwipeSeg[0], i, i2, fArr);
    }
}
