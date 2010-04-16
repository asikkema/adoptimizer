import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ProjectPluginsContainer

class GwtCompile implements Plugin {

  void use(Project project, ProjectPluginsContainer projectPluginsContainer) {
    project.convention.plugins.gwt = new GwtPluginConvention(project)

    project.configurations {
      gwtCompile
    }

    project.task("gwtCompile") << {
      System.out.println("Compiling GWT")
//      project.ant.java(classname: 'com.google.gwt.dev.Compiler', failOnError: 'true', fork: 'true') {
//        jvmarg(value: '-Xmx184M')
//        arg(line: '-war ' + project.convention.plugins.gwt.gwtBuildDir)
//        arg(line: '-logLevel INFO')
//        arg(line: '-style PRETTY')
//        arg(value: 'me.trond.app.MyApp')
//        classpath {
//          pathElement(location: srcRootName + '/' + srcDirNames[0])
//          pathElement(path: configurations.compile.asPath)
//          pathElement(path: configurations.gwtCompile.asPath)
//        }
//      }
    }

    def war = project.tasks.findByName('war')
    war.dependsOn project.tasks.findByName('gwtCompile')

    war {
      fileSet(dir: file(project.convention.plugins.gwt.gwtBuildDir))
    }
  }
}

class GwtPluginConvention {
  String gwtBuildDir
  Project project


  def GwtPluginConvention(Project project) {
    this.project = project;
    gwtBuildDir = new File(project.buildDir, "gwt-compile")
  }
}