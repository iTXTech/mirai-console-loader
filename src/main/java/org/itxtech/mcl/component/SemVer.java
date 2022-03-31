package org.itxtech.mcl.component;

import java.util.Objects;

/*
 *
 * Mirai Console Loader
 *
 * Copyright (C) 2020-2022 iTX Technologies
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author JetBrains, PeratX
 * @website https://github.com/iTXTech/mirai-console-loader
 *
 */
public final class SemVer implements Comparable<SemVer> {

    private final String myRawVersion;
    private final int myMajor;
    private final int myMinor;
    private final int myPatch;

    private final String myPreRelease;

    public SemVer(String rawVersion, int major, int minor, int patch) {
        this(rawVersion, major, minor, patch, null);
    }

    public SemVer(String rawVersion, int major, int minor, int patch, String preRelease) {
        myRawVersion = rawVersion;
        myMajor = major;
        myMinor = minor;
        myPatch = patch;
        myPreRelease = preRelease;
    }

    public String getRawVersion() {
        return myRawVersion;
    }

    public int getMajor() {
        return myMajor;
    }

    public int getMinor() {
        return myMinor;
    }

    public int getPatch() {
        return myPatch;
    }

    public String getPreRelease() {
        return myPreRelease;
    }

    public String getParsedVersion() {
        return myMajor + "." + myMinor + "." + myPatch + (myPreRelease != null ? "-" + myPreRelease : "");
    }

    @Override
    public int compareTo(SemVer other) {
        int diff = myMajor - other.myMajor;
        if (diff != 0) return diff;

        diff = myMinor - other.myMinor;
        if (diff != 0) return diff;

        diff = myPatch - other.myPatch;
        if (diff != 0) return diff;

        return comparePrerelease(myPreRelease, other.myPreRelease);
    }

    public boolean isGreaterOrEqualThan(int major, int minor, int patch) {
        if (myMajor != major) return myMajor > major;
        if (myMinor != minor) return myMinor > minor;
        return myPatch >= patch;
    }

    public boolean isGreaterOrEqualThan(SemVer version) {
        return compareTo(version) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SemVer semVer = (SemVer) o;
        return myMajor == semVer.myMajor
                && myMinor == semVer.myMinor
                && myPatch == semVer.myPatch
                && Objects.equals(myPreRelease, semVer.myPreRelease);
    }

    @Override
    public int hashCode() {
        int result = myMajor;
        result = 31 * result + myMinor;
        result = 31 * result + myPatch;
        if (myPreRelease != null) {
            result = 31 * result + myPreRelease.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return myRawVersion;
    }

    private static int comparePrerelease(String pre1, String pre2) {
        if (pre1 == null) {
            return pre2 == null ? 0 : 1;
        } else if (pre2 == null) {
            return -1;
        }
        int length1 = pre1.length();
        int length2 = pre2.length();

        if (length1 == length2 && pre1.equals(pre2)) return 0;

        int start1 = 0;
        int start2 = 0;
        int diff;

        // compare each segment separately
        do {
            int end1 = pre1.indexOf('.', start1);
            int end2 = pre2.indexOf('.', start2);

            if (end1 < 0) end1 = length1;
            if (end2 < 0) end2 = length2;


            CharSequence segment1 = pre1.subSequence(start1, end1);
            CharSequence segment2 = pre2.subSequence(start2, end2);
            if (isNotNegativeNumber(segment1)) {
                if (!isNotNegativeNumber(segment2)) {
                    // According to SemVer specification numeric segments has lower precedence
                    // than non-numeric segments
                    return -1;
                }
                diff = compareNumeric(segment1, segment2);
            } else if (isNotNegativeNumber(segment2)) {
                return 1;
            } else {
                diff = compare(segment1, segment2, false);
            }
            start1 = end1 + 1;
            start2 = end2 + 1;
        }
        while (diff == 0 && start1 < length1 && start2 < length2);

        if (diff != 0) return diff;

        return start1 < length1 ? 1 : -1;
    }

    private static int compareNumeric(CharSequence segment1, CharSequence segment2) {
        int length1 = segment1.length();
        int length2 = segment2.length();
        int diff = Integer.compare(length1, length2);
        for (int i = 0; i < length1 && diff == 0; i++) {
            diff = segment1.charAt(i) - segment2.charAt(i);
        }
        return diff;
    }


    public static SemVer parseFromText(String text) {
        if (text != null) {
            int majorEndIdx = text.indexOf('.');
            if (majorEndIdx >= 0) {
                int minorEndIdx = text.indexOf('.', majorEndIdx + 1);
                var hasPatch = true;
                if (minorEndIdx == -1) {
                    minorEndIdx = text.indexOf('-', majorEndIdx + 1);
                    hasPatch = false;
                }
                if (minorEndIdx >= 0) {
                    int preReleaseIdx, patch;
                    if (hasPatch) {
                        preReleaseIdx = text.indexOf('-', minorEndIdx + 1);
                        int patchEndIdx = preReleaseIdx >= 0 ? preReleaseIdx : text.length();
                        patch = parseInt(text.substring(minorEndIdx + 1, patchEndIdx), -1);
                    } else {
                        preReleaseIdx = minorEndIdx;
                        patch = 0;
                    }

                    int major = parseInt(text.substring(0, majorEndIdx), -1);
                    int minor = parseInt(text.substring(majorEndIdx + 1, minorEndIdx), -1);
                    String preRelease = preReleaseIdx >= 0 ? text.substring(preReleaseIdx + 1) : null;

                    if (major >= 0 && minor >= 0) {
                        return new SemVer(text, major, minor, patch, preRelease);
                    }
                }
            }
        }

        return null;
    }

    public static int parseInt(String string, int defaultValue) {
        if (string != null) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    public static int compare(CharSequence s1, CharSequence s2, boolean ignoreCase) {
        if (s1 == s2) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;

        int length1 = s1.length();
        int length2 = s2.length();
        int i = 0;
        for (; i < length1 && i < length2; i++) {
            int diff = compare(s1.charAt(i), s2.charAt(i), ignoreCase);
            if (diff != 0) {
                return diff;
            }
        }
        return length1 - length2;
    }

    public static int compare(char c1, char c2, boolean ignoreCase) {
        // duplicating String.equalsIgnoreCase logic
        int d = c1 - c2;
        if (d == 0 || !ignoreCase) {
            return d;
        }
        // If characters don't match but case may be ignored,
        // try converting both characters to uppercase.
        // If the results match, then the comparison scan should
        // continue.
        char u1 = toUpperCase(c1);
        char u2 = toUpperCase(c2);
        d = u1 - u2;
        if (d != 0) {
            // Unfortunately, conversion to uppercase does not work properly
            // for the Georgian alphabet, which has strange rules about case
            // conversion.  So we need to make one last check before
            // exiting.
            d = toLowerCase(u1) - toLowerCase(u2);
        }
        return d;
    }

    public static char toLowerCase(char a) {
        if (a <= 'z') {
            return a >= 'A' && a <= 'Z' ? (char) (a + ('a' - 'A')) : a;
        }
        return Character.toLowerCase(a);
    }

    public static char toUpperCase(char a) {
        if (a < 'a') return a;
        if (a <= 'z') return (char) (a + ('A' - 'a'));
        return Character.toUpperCase(a);
    }

    public static boolean isNotNegativeNumber(CharSequence s) {
        if (s == null) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!isDecimalDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDecimalDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public enum VersionKind {
        Stable(0),
        PreRelease(1),
        Nightly(2);

        public final int id;

        VersionKind(int id) {
            this.id = id;
        }
    }

    public static VersionKind getVersionKindFromChannel(String kind) {
        if (kind.equals("stable")) {
            return VersionKind.Stable;
        }
        if (kind.equals("beta") || kind.equals("prerelease")) {
            return VersionKind.PreRelease;
        }
        return VersionKind.Nightly;
    }

    public static VersionKind getVersionKind(String ver) {
        ver = ver.toLowerCase();
        if (ver.matches("^\\d+\\.\\d+(?:\\.\\d+)?$")) {
            return VersionKind.Stable;
        }
        if ((ver.contains("-m") || ver.contains("-rc") || ver.contains("-beta")) && !ver.contains("-dev")) {
            return VersionKind.PreRelease;
        }
        return VersionKind.Nightly;
    }

    public static boolean isKind(String ver, VersionKind kind) {
        return getVersionKind(ver).id <= kind.id;
    }
}
