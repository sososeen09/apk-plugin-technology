package com.sososeen09.apk.upload.plugin

import com.sososeen09.run.help.plugin.PathUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApkUploadPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("pluginApkOutputPath", PluginApkPathExtentions)

        project.android.applicationVariants.all { variant ->
//            variant.outputs.each { output ->
//                def file = output.outputFile
//                output.outputFile = new File(file.parent, file.name.replace(".apk", "-${variant.versionName}.apk"))
//            }

            if (variant.assemble) {
                project.tasks.create(name: "upload${variant.name.capitalize()}PluginApk", dependsOn: variant.assemble) {
                    it.group = "upload plugin apk"
                    it.description "upload the ${variant.description} to sdcard"
                    it.doLast {
                        def outputFile=variant.outputs[0].outputFile
                        def adb = PathUtils.getAdbCmdPath(project)
                        def path = project.pluginApkOutputPath.path
                        project.exec {
                            it.executable = adb
                            it.args = ['shell', 'mkdir', path]
                        }
                        project.exec {
                            it.executable = adb
                            it.args = ['push', outputFile.absoluteFile, path]
                        }
                    }
                }
            }
        }
    }
}