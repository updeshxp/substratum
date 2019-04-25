/*
 * Copyright (c) 2016-2018 Projekt Substratum
 * This file is part of Substratum.
 *
 * SPDX-License-Identifier: GPL-3.0-Or-Later
 */

package projekt.substratum.common.commands;

import android.content.Context;
import android.content.pm.PackageManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import projekt.substratum.BuildConfig;
import projekt.substratum.common.Packages;
import projekt.substratum.common.References;
import projekt.substratum.common.Systems;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static projekt.substratum.common.References.COMMON_PACKAGE;
import static projekt.substratum.common.References.ENABLE_AAPT_OUTPUT;
import static projekt.substratum.common.References.SAMSUNG_OVERLAY_PERMISSION;
import static projekt.substratum.common.Resources.allowedForSamsungPermission;
import static projekt.substratum.common.Systems.getDeviceID;
import static projekt.substratum.common.Systems.isNewSamsungDevice;

public class CompilerCommands {

    /**
     * Create the overlay's manifest
     *
     * @param context             Context
     * @param overlayPackage      Overlay's package name
     * @param themeName           Theme name
     * @param variantName         Variant name
     * @param baseVariantName     Base variant name
     * @param versionName         Version
     * @param targetPackage       Target package
     * @param themeParent         Theme Parent
     * @param themeOms            OMS Support
     * @param legacyPriority      Legacy Priority
     * @param baseVariantNull     Base variant available?
     * @param type1a              Type 1a
     * @param type1b              Type 1b
     * @param type1c              Type 1c
     * @param type2               Type 2
     * @param type3               Type 3
     * @param type4               Type 4
     * @param packageNameOverride Override package name
     * @return Returns a string that contains the full manifest file
     */
    public static String createOverlayManifest(Context context,
                                               String overlayPackage,
                                               String themeName,
                                               String variantName,
                                               String baseVariantName,
                                               String versionName,
                                               String overlayVersion,
                                               String targetPackage,
                                               String themeParent,
                                               boolean themeOms,
                                               Integer legacyPriority,
                                               boolean baseVariantNull,
                                               String type1a,
                                               String type1b,
                                               String type1c,
                                               String type2,
                                               String type3,
                                               String type4,
                                               String packageNameOverride) {
        String packageName = overlayPackage + '.' + themeName;
        if (!baseVariantNull) packageName = packageName + variantName + baseVariantName;
        if (isNotNullOrEmpty(packageNameOverride)) packageName = packageNameOverride;

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // root elements
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("manifest");
            rootElement.setAttribute("xmlns:android",
                    "http://schemas.android.com/apk/res/android");
            rootElement.setAttribute("package", packageName);
            rootElement.setAttribute("android:versionName", versionName);

            // Special permissions for special devices
            if (isNewSamsungDevice() && allowedForSamsungPermission(targetPackage)) {
                Element permissionElement = document.createElement("uses-permission");
                permissionElement.setAttribute("android:name", SAMSUNG_OVERLAY_PERMISSION);
                rootElement.appendChild(permissionElement);
            }

            Element overlayElement = document.createElement("overlay");
            if (!themeOms)
                overlayElement.setAttribute("android:priority", String.valueOf(legacyPriority));
            overlayElement.setAttribute("android:targetPackage", targetPackage);
            if (Systems.IS_OREO) overlayElement.setAttribute("android:isStatic", "false");
            rootElement.appendChild(overlayElement);

            Element applicationElement = document.createElement("application");
            applicationElement.setAttribute("android:label", packageName);
            applicationElement.setAttribute("allowBackup", "false");
            applicationElement.setAttribute("android:hasCode", "false");

            Element metadataOverlayDevice = document.createElement("meta-data");
            metadataOverlayDevice.setAttribute("android:name", References.metadataOverlayDevice);
            metadataOverlayDevice.setAttribute("android:value", getDeviceID(context));
            applicationElement.appendChild(metadataOverlayDevice);

            Element metadataOverlayParent = document.createElement("meta-data");
            metadataOverlayParent.setAttribute("android:name", References.metadataOverlayParent);
            metadataOverlayParent.setAttribute("android:value", themeParent);
            applicationElement.appendChild(metadataOverlayParent);

            Element metadataOverlayTarget = document.createElement("meta-data");
            metadataOverlayTarget.setAttribute("android:name", References.metadataOverlayTarget);
            metadataOverlayTarget.setAttribute("android:value", targetPackage);
            applicationElement.appendChild(metadataOverlayTarget);

            Element metadataOverlayType1a = document.createElement("meta-data");
            metadataOverlayType1a.setAttribute("android:name", References.metadataOverlayType1a);
            metadataOverlayType1a.setAttribute("android:value", type1a);
            applicationElement.appendChild(metadataOverlayType1a);

            Element metadataOverlayType1b = document.createElement("meta-data");
            metadataOverlayType1b.setAttribute("android:name", References.metadataOverlayType1b);
            metadataOverlayType1b.setAttribute("android:value", type1b);
            applicationElement.appendChild(metadataOverlayType1b);

            Element metadataOverlayType1c = document.createElement("meta-data");
            metadataOverlayType1c.setAttribute("android:name", References.metadataOverlayType1c);
            metadataOverlayType1c.setAttribute("android:value", type1c);
            applicationElement.appendChild(metadataOverlayType1c);

            Element metadataOverlayType2 = document.createElement("meta-data");
            metadataOverlayType2.setAttribute("android:name", References.metadataOverlayType2);
            metadataOverlayType2.setAttribute("android:value", type2);
            applicationElement.appendChild(metadataOverlayType2);

            Element metadataOverlayType3 = document.createElement("meta-data");
            metadataOverlayType3.setAttribute("android:name", References.metadataOverlayType3);
            metadataOverlayType3.setAttribute("android:value", type3);
            applicationElement.appendChild(metadataOverlayType3);

            Element metadataOverlayType4 = document.createElement("meta-data");
            metadataOverlayType4.setAttribute("android:name", References.metadataOverlayType4);
            metadataOverlayType4.setAttribute("android:value", type4);
            applicationElement.appendChild(metadataOverlayType4);

            Element metadataThemeVersion = document.createElement("meta-data");
            metadataThemeVersion.setAttribute("android:name", References.metadataThemeVersion);
            metadataThemeVersion.setAttribute("android:value", String.valueOf(BuildConfig.VERSION_CODE));
            applicationElement.appendChild(metadataThemeVersion);

            Element metadataOverlayVersion = document.createElement("meta-data");
            metadataOverlayVersion.setAttribute("android:name", References.metadataOverlayVersion);
            metadataOverlayVersion.setAttribute("android:value", String.valueOf(overlayVersion));
            applicationElement.appendChild(metadataOverlayVersion);

            rootElement.appendChild(applicationElement);
            document.appendChild(rootElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            Source domSource = new DOMSource(document);
            StringWriter outWriter = new StringWriter();
            Result streamResult = new StreamResult(outWriter);
            transformer.transform(domSource, streamResult);

            return outWriter.getBuffer().toString();
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Helper function to easily check whether a String object is null or empty
     *
     * @param string String object
     * @return True, then it is Null or Empty
     */
    private static boolean isNotNullOrEmpty(CharSequence string) {
        return (string != null) && (string.length() != 0);
    }

    /**
     * Create the AAPT working shell commands
     *
     * @param workArea          Working area
     * @param targetPackage     Target package to build against
     * @param overlayPackage    Overlay package
     * @param themeName         Theme name
     * @param legacySwitch      Fallback support
     * @param additionalVariant Additional variant (type2)
     * @param assetReplacement  Asset replacement (type4)
     * @param context           Context
     * @param dir               Volatile directory to keep changes in
     * @return Returns a string to allow the app to execute
     */
    // This is necessary to avoid making a massive unreadable soup
    // inside the method.
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public static String createAAPTShellCommands(String workArea,
                                                 String targetPackage,
                                                 String overlayPackage,
                                                 String themeName,
                                                 boolean legacySwitch,
                                                 CharSequence additionalVariant,
                                                 CharSequence assetReplacement,
                                                 Context context,
                                                 String dir) {
        StringBuilder sb = new StringBuilder();
        // Initialize the AAPT command
        sb.append(context.getFilesDir().getAbsolutePath() + "/aapt p ");
        // Compile with specified manifest
        sb.append("-M ").append(workArea).append("/AndroidManifest.xml ");
        // If the user picked a variant (type2), compile multiple directories
        if (isNotNullOrEmpty(additionalVariant))
            sb.append("-S ").append(workArea).append("/").append("type2_").append(additionalVariant).append("/ ");
        // If the user picked an asset variant (type4), compile multiple directories
        if (isNotNullOrEmpty(assetReplacement))
            sb.append("-A ").append(workArea).append("/assets/ ");
        // We will compile a volatile directory where we make temporary changes to
        sb.append("-S ").append(workArea).append(dir).append("/ ");
        // Build upon the system's Android framework
        sb.append("-I ").append("/system/framework/framework-res.apk ");
        // Build upon the common Substratum framework
        if (Packages.isPackageInstalled(context, COMMON_PACKAGE)) {
            sb.append("-I " + Packages.getInstalledDirectory(context, COMMON_PACKAGE) + ' ');
        }
        String[] splitLocations = getSplitLocations(context, targetPackage);
        if (splitLocations != null) {
            for (String split : splitLocations) {
                sb.append("-I ").append(split).append(" ");
            }
        }
        final String packagePath = Packages.getInstalledDirectory(context, targetPackage);
        // If running on the AppCompat commits (first run), it will build upon the app too
        if (packagePath != null && !packagePath.equals("null")) {
            if (!legacySwitch)
                sb.append("-I ").append(packagePath).append(" ");
        }
        // Specify the file output directory
        sb.append("-F ").append(workArea).append("/").append(overlayPackage)
                .append(".").append(themeName).append("-unsigned.apk ");
        // arguments to conclude the AAPT build
        if (ENABLE_AAPT_OUTPUT) {
            sb.append("-v ");
        }
        // Allow themers to append new resources
        sb.append("--include-meta-data ");
        sb.append("--auto-add-overlay ");
        // Overwrite all the files in the internal storage
        sb.append("-f ");
        sb.append('\n');

        return sb.toString();
    }

    private static String[] getSplitLocations(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationInfo(packageName, 0).splitSourceDirs;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new String[0];
    }

    /**
     * Create the ZipAlign shell commands
     *
     * @param context     Context
     * @param source      Source
     * @param destination Destination
     * @return Returns a string that is executable by the application
     */
    public static String createZipAlignShellCommands(Context context,
                                                     String source,
                                                     String destination) {
        // Initialize the ZipAlign command
        String ret = context.getFilesDir().getAbsolutePath() + "/zipalign 4 ";
        // Supply the source
        ret += source + ' ';
        // Supply the destination
        ret += destination;

        return ret;
    }
}