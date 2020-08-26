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
package es.sdos.customlogger.util

import android.util.Log

import android.os.SystemClock
import es.sdos.customlogger.BuildConfig

/**
 * A utility class to help log timings splits throughout a method call.
 * Typical usage is:
 *
 * <pre>
 *     TimingLogger timings = new TimingLogger(TAG, "methodA");
 *     // ... do some work A ...
 *     timings.addSplit("work A");
 *     // ... do some work B ...
 *     timings.addSplit("work B");
 *     // ... do some work C ...
 *     timings.addSplit("work C");
 *     timings.dumpToLog();
 * </pre>
 *
 * <p>The dumpToLog call would add the following to the log:</p>
 *
 * <pre>
 *     D/TAG     ( 3459): methodA: begin
 *     D/TAG     ( 3459): methodA:      9 ms, work A
 *     D/TAG     ( 3459): methodA:      1 ms, work B
 *     D/TAG     ( 3459): methodA:      6 ms, work C
 *     D/TAG     ( 3459): methodA: end, 16 ms
 * </pre>
 */
class CustomTimingLogger(tag: String?, label: String?) {
    /**
     * The Log tag to use for checking Log.isLoggable and for
     * logging the timings.
     */
    private var mTag: String? = null

    /** A label to be included in every log.  */
    private var mLabel: String? = null

    /** Used to track whether Log.isLoggable was enabled at reset time.  */
    private var mDisabled: Boolean = false

    /** Stores the time of each split.  */
    var mSplits: MutableList<Long> = mutableListOf()

    /** Stores the labels for each split.  */
    var mSplitLabels: MutableList<String?> = mutableListOf()

    /**
     * Create and initialize a TimingLogger object that will log using
     * the specific tag. If the Log.isLoggable is not enabled to at
     * least the Log.VERBOSE level for that tag at creation time then
     * the addSplit and dumpToLog call will do nothing.
     * @param tag the log tag to use while logging the timings
     * @param label a string to be displayed with each log
     */
    init {
        if (tag != null &&
            label != null) {
            reset(tag, label)

        } else {
            reset()
        }
    }

    /**
     * Clear and initialize a TimingLogger object that will log using
     * the specific tag. If the Log.isLoggable is not enabled to at
     * least the Log.VERBOSE level for that tag at creation time then
     * the addSplit and dumpToLog call will do nothing.
     * @param tag the log tag to use while logging the timings
     * @param label a string to be displayed with each log
     */
    fun reset(tag: String, label: String) {
        mTag = tag
        mLabel = label
        reset()
    }

    /**
     * Clear and initialize a TimingLogger object that will log using
     * the tag and label that was specified previously, either via
     * the constructor or a call to reset(tag, label). If the
     * Log.isLoggable is not enabled to at least the Log.VERBOSE
     * level for that tag at creation time then the addSplit and
     * dumpToLog call will do nothing.
     */
    fun reset() {
        mDisabled = !BuildConfig.DEBUG
        if (mDisabled) return
        mSplits.clear()
        mSplitLabels.clear()
        addSplit(null)
    }

    /**
     * Add a split for the current time, labeled with splitLabel. If
     * Log.isLoggable was not enabled to at least the Log.VERBOSE for
     * the specified tag at construction or reset() time then this
     * call does nothing.
     * @param splitLabel a label to associate with this split.
     */
    fun addSplit(splitLabel: String?) {
        if (mDisabled) return
        val now = SystemClock.elapsedRealtime()
        mSplits.add(now)
        mSplitLabels.add(splitLabel)
    }

    /**
     * Dumps the timings to the log using Log.d(). If Log.isLoggable was
     * not enabled to at least the Log.VERBOSE for the specified tag at
     * construction or reset() time then this call does nothing.
     */
    fun dumpToLog(): String {
        if (mDisabled) return "Debug disable"
        val stringBuilder = StringBuilder()
        stringBuilder.append("Start: ").append("\n")
        Log.d(mTag, mLabel!! + ": begin")
        val first = mSplits[0]
        var now = first
        for (i in 1 until mSplits.size) {
            now = mSplits[i]
            val splitLabel = mSplitLabels[i]
            val prev = mSplits[i - 1]
            stringBuilder.append(mTag + ": " + mLabel + ":      " + (now - prev) + " ms, " + splitLabel).append("\n")
        }
        stringBuilder.append(mTag + ": " + mLabel + ": end, " + (now - first) + " ms")
        return stringBuilder.toString()
    }
}