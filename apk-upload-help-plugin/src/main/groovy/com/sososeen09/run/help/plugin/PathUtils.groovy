package com.sososeen09.run.help.plugin

import groovy.xml.QName
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project

class PathUtils {

    public static final getBootActivityPath(String manifestPath) {
        def bootActivityName = ""
        def xml = new XmlParser().parse(new InputStreamReader(new FileInputStream(manifestPath), "utf-8"))
        def application = xml.application[0]

        if (application) {
            def activities = application.activity
            QName androidNameAttr = new QName("http://schemas.android.com/apk/res/android", 'name', 'android');

            try {
                activities.each { activity ->
                    def activityName = activity.attribute(androidNameAttr)

                    if (activityName) {
                        def intentFilters = activity."intent-filter"
                        if (intentFilters) {
                            intentFilters.each { intentFilter ->
                                def actions = intentFilter.action
                                def categories = intentFilter.category
                                if (actions && categories) {
                                    //android.intent.action.MAIN
                                    //android.intent.category.LAUNCHER

                                    boolean hasMainAttr = false
                                    boolean hasLauncherAttr = false

                                    actions.each { action ->
                                        def attr = action.attribute(androidNameAttr)
                                        if ("android.intent.action.MAIN".equals(attr.toString())) {
                                            hasMainAttr = true
                                        }
                                    }

                                    categories.each { categoriy ->
                                        def attr = categoriy.attribute(androidNameAttr)
                                        if ("android.intent.category.LAUNCHER".equals(attr.toString())) {
                                            hasLauncherAttr = true
                                        }
                                    }

                                    if (hasMainAttr && hasLauncherAttr) {
                                        bootActivityName = activityName
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace()
            }
        }
        return bootActivityName
    }

    public static final String getAdbCmdPath(Project project) {
        File adb = new File(getSdkDirectory(project), "platform-tools${File.separator}adb")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${adb.absolutePath}.exe"
        }
        return adb.getAbsolutePath()
    }

    public static final String getSdkDirectory(Project project) {
        String sdkDirectory = project.android.getSdkDirectory()
        if (sdkDirectory.contains("\\")) {
            sdkDirectory = sdkDirectory.replace("\\", "/");
        }
        return sdkDirectory
    }
}