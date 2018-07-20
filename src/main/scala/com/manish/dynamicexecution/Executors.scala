package com.manish.dynamicexecution

import java.nio.file.{FileSystems, _}
import java.util.Collections

import scala.collection.JavaConverters._
import scala.collection.immutable.SortedSet
import scala.reflect.runtime.universe
import com.manish.dynamicexecution.util.Util


object Executors{

  private final val EXECUTORS_PACKAGE = "com.manish.dynamicexecution.executors"
  private final val EXECUTORS_DIRECTORY = EXECUTORS_PACKAGE.replaceAllLiterally(".", "/")
  

  def execute(): Unit = {
    getExecutorsObjectNames().foreach { e =>
      //println(s"Executor : ${e} ----------------------------------- START")
      getObject(e).execute()
      //println(s"Executor : ${e} ----------------------------------- END")
    }
  }
  

  private def getExecutorsObjectNames(): SortedSet[String] = {
    val uri = getClass.getClassLoader.getResource(EXECUTORS_DIRECTORY).toURI

    val fileSystem = uri.getScheme match {
      case "jar" => Some(FileSystems.newFileSystem(uri, Collections.emptyMap[String, Any]))
      case _ => None
    }

    Util.cleanly(fileSystem)(_.foreach(_.close())){fileSystem=>
      val directoryPath = fileSystem.map(_.getPath(EXECUTORS_DIRECTORY)).getOrElse(Paths.get(uri))
      Files.walk(directoryPath, 1).iterator().asScala.toList
        .filter(path => path.toString.endsWith(".class"))
        .map(path => getClassName(path)).to[SortedSet]
    }
  }

  
  private def getClassName(path: Path): String={
    val classStr= path.getFileName.toString
    val index= classStr.indexOf("$")
    val className= index match {
      case -1 => classStr.replaceAll(".class", "")
      case _ => classStr.substring(0, index)
    }
    EXECUTORS_PACKAGE+"."+className
  }

  
  private def getObject(executorObjName: String) ={
    val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
    val module = runtimeMirror.staticModule(executorObjName)
    val obj = runtimeMirror.reflectModule(module)
    obj.instance.asInstanceOf[BaseExecutor]
  }


}