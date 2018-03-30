package com.sososeen09.run.help.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class RunHelpPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.android.applicationVariants.all { variant ->
            variant.outputs.each { output ->
                def file = output.outputFile
                output.outputFile = new File(file.parent, file.name.replace(".apk", "-${variant.versionName}.apk"))
            }

            if (variant.install) {
                project.tasks.create(name: "run${variant.name.capitalize()}", dependsOn: variant.install) {
                    it.group = "runAppHelp"
                    it.description "Installs the ${variant.description} and runs the main launcher activity."
                    it.doFirst {
                        def classpath = variant.applicationId
                        if (variant.buildType.applicationIdSuffix) {
                            classpath -= "${variant.buildType.applicationIdSuffix}"
                        }

                        def mainActivityPath = PathUtils.getBootActivityPath(variant.outputs.first().processManifest.manifestOutputFile.path)
                        project.logger.quiet("mainActivityPath: ${mainActivityPath}")
                        def launchClass = "${variant.applicationId}/${mainActivityPath}"
                        project.logger.quiet("launchClass: ${launchClass}")
                        project.logger.quiet("taskName: ${name}")

                        project.exec {
                            def adb = PathUtils.getAdbCmdPath(project)
                            it.executable = adb
                            it.args = ['shell', 'am', 'start', '-n', launchClass]
                        }
                    }
                }
            }
        }
    }


}