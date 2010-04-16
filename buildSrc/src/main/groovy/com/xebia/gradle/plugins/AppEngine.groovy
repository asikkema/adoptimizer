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
  Properties props = new Properties()
  Project project

  def AppEnginePluginConvention(project) {
    this.project = project;
    if (!new File("appengine.properties").exists()) {
      throw new InvalidUserDataException("appengine.properties should exist in build root dir")
    }
    props.load(new FileInputStream("appengine.properties"))
  }

  def init() {
    Properties props = new Properties()
    System.setProperty("appengine.sdk.root", appEngineSdkRoot)
  }

  def propertyMissing(String name) { props[name] }
}