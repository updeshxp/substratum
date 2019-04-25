/*
 * Copyright (c) 2016-2018 Projekt Substratum
 * This file is part of Substratum.
 *
 * SPDX-License-Identifier: GPL-3.0-Or-Later
 */

package projekt.substratum.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import dalvik.system.DexClassLoader;
import projekt.substratum.Substratum;

import java.util.Arrays;

import static projekt.substratum.common.References.INTERFACER_PACKAGE;
import static projekt.substratum.common.References.SUBSTRATUM_LOG;
import static projekt.substratum.common.Systems.checkSubstratumService;
import static projekt.substratum.common.Systems.checkThemeInterfacer;
import static projekt.substratum.common.Systems.isAndromedaDevice;
import static projekt.substratum.common.Systems.isSamsungDevice;

public class Resources {

    public static final String FRAMEWORK = "android";
    public static final String SETTINGS = "com.android.settings";
    public static final String SYSTEMUI = "com.android.systemui";
    public static final String SYSTEMUI_HEADERS = "com.android.systemui.headers";
    public static final String SYSTEMUI_NAVBARS = "com.android.systemui.navbars";
    public static final String SYSTEMUI_STATUSBARS = "com.android.systemui.statusbars";
    public static final String SYSTEMUI_QSTILES = "com.android.systemui.tiles";
    public static final String SETTINGS_ICONS = "com.android.settings.icons";
    public static final String SAMSUNG_FRAMEWORK = "fwk";
    public static final String LG_FRAMEWORK = "common";

    // Filter to adjust Settings elements
    public static final String[] ALLOWED_SETTINGS_ELEMENTS = {
            SETTINGS_ICONS,
    };

    public static final String[] SYSTEM_FAULT_EXCEPTIONS = {
            "null object reference",
            "null object",
            "not attached to Activity",
            "not attached"
    };

    // List of errors to catch
    public static final String[] SUBSTRATUM_OVERLAY_FAULT_EXCEPTIONS = {
            "ResourceNotFoundException",
            "InflateException",
            "UnsupportedOperationException"
    };
    // List of stock overlay packages on Pixel devices
    // We don't want to run any operations on them.
    public static final String[] PIXEL_OVERLAY_PACKAGES = {
            "android.auto_generated_rro__",
            "com.google.android.theme.pixel",
            "com.android.systemui.theme.dark",
    };
    private static final String[] ALLOWED_SOUNDS = {
            "alarm.mp3",
            "alarm.ogg",
            "notification.mp3",
            "notification.ogg",
            "ringtone.mp3",
            "ringtone.ogg",
            "Effect_Tick.mp3",
            "Effect_Tick.ogg",
            "Lock.mp3",
            "Lock.ogg",
            "Unlock.mp3",
            "Unlock.ogg",
    };
    // These packages will be exempt from having the Samsung overlay permission added onto it
    private static final String[] SAMSUNG_PERMISSION_BLACKLIST_PACKAGES = {
            "com.sec.android.app.music",
            "com.sec.android.app.voicenote"
    };

    // Filter to adjust framework elements
    private static final String[] ALLOWED_FRAMEWORK_ELEMENTS = {
            SAMSUNG_FRAMEWORK,
            LG_FRAMEWORK
    };

    // Filter to adjust SystemUI elements
    private static final String[] ALLOWED_SYSTEMUI_ELEMENTS = {
            SYSTEMUI_HEADERS,
            SYSTEMUI_NAVBARS,
            SYSTEMUI_NAVBARS,
            SYSTEMUI_QSTILES,
            SYSTEMUI_STATUSBARS
    };

    // Predetermined list of new Nexus/Pixel Devices
    private static final String[] NEXUS_FILTER = {
            "angler", // Nexus 6P
            "bullhead", // Nexus 5X
            "flounder", // Nexus 9
            "dragon", // Pixel C
            "marlin", // Pixel
            "sailfish", // Pixel XL
            "walleye", // Pixel 2
            "muskie", // The hidden HTC Pixel 2
            "taimen", // Pixel 2 XL
    };

    // Legacy Asset Folder Check
    private static final String[] ALLOWED_LEGACY_ASSETS = {
            "overlays",
            "bootanimation",
    };

    // Do not theme these packages
    private static final String[] BLACKLIST_THEME_TARGET_APPS = {
            "com.android.cts.verifier",
            INTERFACER_PACKAGE
    };

    // This method configures the new devices and their configuration of their vendor folders
    public static Boolean inNexusFilter() {
        return Arrays.asList(NEXUS_FILTER).contains(Build.DEVICE);
    }

    // This string array contains all the SystemUI acceptable overlay packs
    public static Boolean allowedSounds(final String current) {
        return Arrays.asList(ALLOWED_SOUNDS).contains(current);
    }

    // This string array contains all the SystemUI acceptable overlay packs
    public static Boolean allowedSystemUIOverlay(final String current) {
        return Arrays.asList(ALLOWED_SYSTEMUI_ELEMENTS).contains(current);
    }

    // This string array contains all the Settings acceptable overlay packs
    public static Boolean allowedSettingsOverlay(final String current) {
        return Arrays.asList(ALLOWED_SETTINGS_ELEMENTS).contains(current);
    }

    // This string array contains all the framework acceptable overlay packs
    public static Boolean allowedFrameworkOverlay(final String current) {
        return Arrays.asList(ALLOWED_FRAMEWORK_ELEMENTS).contains(current);
    }

    // This string array contains all the legacy allowed folders
    public static Boolean allowedForLegacy(final String targetValue) {
        return Arrays.asList(ALLOWED_LEGACY_ASSETS).contains(targetValue);
    }

    // This string array contains all blacklisted app for theme
    public static Boolean allowedAppOverlay(final String targetValue) {
        return !Arrays.asList(BLACKLIST_THEME_TARGET_APPS).contains(targetValue);
    }

    // This string array checks whether the listing package contains a blacklisted Samsung package
    public static Boolean allowedForSamsungPermission(final String targetValue) {
        return !Arrays.asList(SAMSUNG_PERMISSION_BLACKLIST_PACKAGES).contains(targetValue);
    }

    // This method checks whether custom fonts is supported by the system
    @SuppressWarnings("JavaReflectionMemberAccess")
    public static boolean isFontsSupported(Context context) {
        if (checkSubstratumService(context)) {
            Substratum.log(SUBSTRATUM_LOG, "This system fully supports font hotswapping.");
            return true;
        } else if (Systems.IS_OREO && !checkSubstratumService(context)) {
            return false;
        }
        try {
            final Class<?> cls = Class.forName("android.graphics.Typeface");
            cls.getDeclaredMethod("getSystemFontDirLocation");
            cls.getDeclaredMethod("getThemeFontConfigLocation");
            cls.getDeclaredMethod("getThemeFontDirLocation");
            Substratum.log(SUBSTRATUM_LOG, "This system fully supports font hotswapping.");
            return true;
        } catch (final Exception ignored) {
            return false;
        }
    }

    // This method checks whether custom sounds is supported by the system
    public static boolean isSoundsSupported(final Context context) {
        return !isAndromedaDevice(context) && !isSamsungDevice(context) &&
                (checkThemeInterfacer(context) || checkSubstratumService(context));
    }

    // This method checks whether custom boot animation is supported by the system
    public static boolean isBootAnimationSupported(final Context context) {
        return !isAndromedaDevice(context) && !isSamsungDevice(context);
    }

    // This method checks whether custom shutdown animation is supported by the system
    public static boolean isShutdownAnimationSupported(Context context) {
        if (checkSubstratumService(context)) {
            Substratum.log(SUBSTRATUM_LOG, "This system fully supports theme shutdown animation.");
            return true;
        }
        try {
            @SuppressLint("PrivateApi") final Class<?> cls = new DexClassLoader
                    ("/system/framework/services.jar",
                            "/data/tmp/", "/data/tmp/", ClassLoader.getSystemClassLoader())
                    .loadClass("com.android.server.power.ShutdownThread");
            cls.getDeclaredMethod("themeShutdownAnimationExists");
            cls.getDeclaredMethod("startShutdownAnimation");
            cls.getDeclaredMethod("stopShutdownAnimation");
            Substratum.log(SUBSTRATUM_LOG, "This system fully supports theme shutdown animation.");
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}
