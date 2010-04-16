package com.xebia.gradle.plugins

import org.gradle.api.*
import org.gradle.api.plugins.*
import com.google.appengine.tools.admin.AppCfg

class AppEngine implements Plugin {
  void use(Project project, ProjectPluginsContainer pluginContainer) {
    if (!project.plugins.hasPlugin('war')) {
      throw new InvalidUserDataException("For AppEngine plugin, the war plugin should be enabled!")
    }

    project.convention.plugins.appengine = new AppEnginePluginConvention(project)

    def war = project.getTasks().findByName("war")

    // Add the exploded-war to the war task
    def exploded = new File(project.buildDir, "exploded-war")
    war.doLast {
      ant.unzip(src: war.archivePath, dest: exploded)
    }


    project.task('upload') << {
      project.convention.plugins.appengine.init()
      AppCfg.main("update", exploded.toString())
    }

    project.getTasks().findByName('upload').dependsOn war
  }
}

class AppEnginePluginConvention {
  String appEngineSdkRoot = ''
  Project project

  def AppEnginePluginConvention(project) {
    this.project = project;
  }

  def init() {
    if (appEngineSdkRoot == '') {
      throw new InvalidUserDataException("Should set the appEngine SDK Root")
    }
    System.setProperty("appengine.sdk.root", appEngineSdkRoot)
  }
}