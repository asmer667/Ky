package org.futo.ml.inference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SwipeDecoder implements AutoCloseable {
    public static final Companion Companion = new Companion();
    private static String defaultAffinity;
    private static final Set<Long> pinnedThreads;
    private final List<String> contextWords;
    private long handle;

    public static final class Companion {
        public Companion() {
        }

        public final String getDefaultAffinity() {
            return SwipeDecoder.defaultAffinity;
        }

        public final void installCaseMappingFor(String vocabPath) {
            // Simplified version for compilation
        }

        public final int pinCores(String affinity) {
            return nativePinCores(affinity);
        }

        public final void pinCoresOnce$futo_swipe_release() {
        }

        public final void setDefaultAffinity(String str) {
            SwipeDecoder.defaultAffinity = str;
        }

        private final int nativePinCores(String str) {
            return SwipeDecoder.nativePinCores(str);
        }

        private final void nativeSetCaseMapping(int[] iArr, int[] iArr2) {
            SwipeDecoder.nativeSetCaseMapping(iArr, iArr2);
        }
    }

    public static final class Result {
        private final float ctcScore;
        private final float lmScore;
        private final float score;
        private final String word;

        public Result(String word, float f, float f2, float f3) {
            this.word = word;
            this.score = f;
            this.ctcScore = f2;
            this.lmScore = f3;
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
    }

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
    }

    public static final class SwipeSeg {
        private final float[] t;
        private final float[] x;
        private final float[] y;

        public SwipeSeg(float[] x, float[] y, float[] t) {
            this.x = x;
            this.y = y;
            this.t = t;
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
    }

    public static final class Timing {
        private final float beamUs;
        private final float decoderUs;
        private final float encoderUs;
        private final float lmUs;
        private final float resampleUs;
        private final float totalUs;

        public Timing(float f, float f2, float f3, float f4, float f5, float f6) {
            this.resampleUs = f;
            this.encoderUs = f2;
            this.decoderUs = f3;
            this.beamUs = f4;
            this.lmUs = f5;
            this.totalUs = f6;
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
    }

    static {
        System.loadLibrary("swipe_jni");
        defaultAffinity = "little";
        pinnedThreads = Collections.newSetFromMap(new ConcurrentHashMap());
    }

    public SwipeDecoder(String encoderPath, String str, int i, int i2, int i3, boolean z, String freqKey, String str2, String str3) {
        this.contextWords = new ArrayList();
        long nativeInit = nativeInit(encoderPath, str, i, i2, i3, z, freqKey, str2, str3);
        this.handle = nativeInit;
        if (nativeInit == 0) {
            throw new IllegalArgumentException("Failed to initialize SwipeDecoder from " + encoderPath);
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

    public static final void setDefaultAffinity(String str) {
        Companion.setDefaultAffinity(str);
    }

    public final void addContext(String word) {
        if (word.length() == 0) {
            return;
        }
        this.contextWords.add(word.toLowerCase(Locale.ROOT));
    }

    public final void clearContext() {
        this.contextWords.clear();
    }

    @Override
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
                return new Timing(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
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
                    return Arrays.asList(nativePredictNext);
                }
                return Collections.emptyList();
            }
            throw new IllegalArgumentException("topK must be positive, got " + i);
        }
        throw new IllegalStateException("SwipeDecoder has been closed");
    }

    public final List<Result> recognize(SwipeSeg[] leftSegs, SwipeSeg[] rightSegs, int i, int i2, float[] fArr) {
        if (this.handle != 0) {
            if (leftSegs.length == 0) {
                if (rightSegs.length == 0) {
                    throw new IllegalArgumentException("must have at least one segment");
                }
            }
            if (i > 0) {
                Result[] nativeRecognize = nativeRecognize(this.handle, leftSegs, rightSegs, i, !this.contextWords.isEmpty() ? (String[]) this.contextWords.toArray(new String[0]) : null, i2, fArr);
                return nativeRecognize != null ? Arrays.asList(nativeRecognize) : Collections.emptyList();
            }
            throw new IllegalArgumentException("topK must be positive, got " + i);
        }
        throw new IllegalStateException("SwipeDecoder has been closed");
    }

    public final void setContext(List<String> words) {
        this.contextWords.clear();
        for (String str : words) {
            if (str.length() > 0) {
                this.contextWords.add(str.toLowerCase(Locale.ROOT));
            }
        }
    }

    public final boolean setMode(String str, float[] fArr, float[] fArr2, long[] jArr, String str2, String str3, String str4) {
        if (this.handle != 0) {
            if (str != null || fArr != null || fArr2 != null) {
                if (str != null && fArr != null && fArr2 != null) {
                    if (fArr.length == fArr2.length) {
                        if (str.codePointCount(0, str.length()) != fArr.length) {
                            throw new IllegalArgumentException("setMode: letters codepoint count (" + str.codePointCount(0, str.length()) + ") must match cx/cy size (" + fArr.length + ")");
                        }
                    } else {
                        throw new IllegalArgumentException("setMode: cx and cy must have same size (" + fArr.length + " != " + fArr2.length + ")");
                    }
                } else {
                    throw new IllegalArgumentException("setMode: letters, cx, cy must be provided together");
                }
            }
            return nativeSetMode(this.handle, str, fArr, fArr2, jArr, str2, str3, str4);
        }
        throw new IllegalStateException("SwipeDecoder has been closed");
    }

    public final List<Result> recognize(float[] x, float[] y, float[] t, int i, int i2, float[] fArr) {
        return recognize(new SwipeSeg[]{new SwipeSeg(x, y, t)}, new SwipeSeg[0], i, i2, fArr);
    }
}