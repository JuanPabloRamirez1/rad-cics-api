package utils

import play.api.Logger
import scala.collection.mutable.ListBuffer
import java.io.Reader

import scala.util.{Success, Try}

/**
  * FixedLengthReader para strings
  */
class FixedLengthStringReader(str: String, logger: Logger)
  extends FixedLengthReader(new java.io.StringReader(str), logger: Logger) {

  def reuseWith(string: String): Unit = {
    resetDebug
    reader = new java.io.StringReader(string)
  }

}

/**
  * Lectura de archivos (o cualquier stream de campo delimitado)
  */
class FixedLengthReader(var reader: Reader, logger: Logger) {

  var debug = true // volver a false
  var debugList = new ListBuffer[String]

  var debugField = false


  var linesRead = 0l

  var sections = new scala.collection.mutable.Stack[ListBuffer[String]]

  //  var field = 1

  def field = sections.foldLeft(new StringBuffer)((sb: StringBuffer, list: Iterable[String]) => sb.append(".").append(list.size+1))

  def close = try{reader.close}catch{case e: Exception => logger.error("Error cerrando archivo", e)}

  var readerPosition: Long = 0

  var bufferedRead: Int = -2

  protected var dateFormatCache = new scala.collection.mutable.HashMap[String, java.text.SimpleDateFormat]


  def resetDebug = {
    debugList.clear()
    sections.clear()
  }

  def hasMore(): Boolean = {
    if(bufferedRead == -2) {
      bufferedRead = readerRead
    }
    bufferedRead >= 0
  }

  protected def readerRead(): Int = {
    if(bufferedRead != -2) {
      val ret = bufferedRead
      bufferedRead = -2
      ret
    }
    else {
      readerPosition += 1
      reader.read
    }
  }

  def position(): Long = {
    readerPosition
  }

  /**
    * Saltea len caracteres
    */
  def skipTo(len: Long): Unit = {
    reader.skip(len)
    readerPosition = len
  }

  /**
    * Info para debugging
    */
  def openSection(name: String) = {
    if(debug) {
      debugList += "Seccion " + name
      sections.push(debugList)
      debugList = new ListBuffer
    }
  }

  /**
    * Info para debugging
    */
  def closeSection = {
    if(debug) {
      var str = dumpReadFields()
      debugList = sections.pop()
      debugList = debugList.reverse.tail.reverse += (debugList.last + ":\n" + str)
    }
  }

  //  /**
  //   * Saltear linea
  //   */
  //  def skipCRCN: Boolean = {
  //    linesRead += 1
  //    skipCR
  //    skipCR
  //  }

  //  /**
  //   * Saltear linea
  //   */
  //  private def skipCR: Boolean = {
  //    val c = readerRead()
  //    //println("skipCR: " + c)
  //    c == 10 || c == 13
  //  }

  /**
    * Saltear resto de la linea
    */
  def skipLine: Boolean = {
    var i = 0
    var c: Int = readerRead
    while(c != -1 && c != 10) {
      c = readerRead
      i += 1
    }

    linesRead += 1

    //    println("Cantidad de caracteres salteados: " + i)
    if(debug) debugList += "Skipped(" + i + ") // till eol"
    c == 10

  }

  /**
    * Saltea len caracteres (si hay excepcion la ignora)
    */
  def safeSkip(len: Int): FixedLengthReader = {
    try {
      skip(len)
    }
    catch {
      case _: Exception => this
    }
  }

  /**
    * Saltea len caracteres
    */
  def skip(len: Int): FixedLengthReader = {
    try {
      val str = read(len)
      //    	println("[%s]:%d".format(str, str.length()))
      if(debug) debugList += "Skipped(" + len + "): [" + str + "]"
      this
    }
    catch {
      case e: Exception => throw new RuntimeException("Error parseando campo " + field + "\n" + dumpReadFields(), e)
    }
  }

  /**
    * Leer string de len caracteres
    */
  def s(len: Int): String = {
    readString(len)
  }

  /**
    * Leer un caracter
    */
  protected def readChar(): String = {
    val i = readerRead()
    if(i == -1) null else new String(Character.toChars(i))
  }

  /**
    * Leer String de n caracteres
    */
  protected def read(len: Int): String = {
    var sb = new java.lang.StringBuilder
    for(i <- 0 to len-1) {
      var chr: Int = readerRead
      try {
        sb.append(new String(Character.toChars(chr)))
      }
      catch {
        case e: IllegalArgumentException => {
          //logger.error("Error tratando de convertir " + chr + " a Char", e)
          throw new RuntimeException("Error tratando de convertir " + chr + " a Char", e)
        }
      }
    }
    //println(sb)
    if(debugField) print("[" + sb + "]")
    sb.toString
  }

  /**
    * Lectura segura de como mucho len caracteres
    */
  protected def safeReadWithAtMost(len: Int): String = {
    var sb = new StringBuilder
    var j = 0
    try {
      for(i <- 0 to len-1) {
        j = i
        var chr: Int = readerRead
        val chars = new String(Character.toChars(chr))
        sb.append(chars)
      }
    }
    catch {
      case _: Exception => {
        // No hacemos nada
        logger.debug("Leidos " + j + " caracteres de " + len + " pedidos: " + sb)
      }
    }

    sb.toString
  }

  /**
    * Leer string de len caracteres
    */
  def readString(len: Int): String = {
    try {
      val str = read(len)
      //    	println("[%s]:%d".format(str, str.length()))
      //    	if(debugField)
      //    	  print("["+str+"]")
      if(debug) debugList += "String(" + len + ")[" + str + "]"
      str
    }
    catch {
      case e: Exception => throw new RuntimeException("Error parseando campo " + field + "\n" + dumpReadFields(), e)
    }
    //    finally {
    //      	field += 1
    //    }
  }

  /**
    * Lectura segura de como mucho len caracteres
    *
    */
  def safeReadStringWithAtMost(len: Int): String = {
    try {
      val str = safeReadWithAtMost(len)
      if(debug) debugList += "String(" + len + " => " + str.length() + ")[" + str + "]"
      str
    }
    catch {
      case e: Exception => throw new RuntimeException("Error parseando campo " + field + "\n" + dumpReadFields(), e)
    }
  }

  def readRest: String = {

    var sb = new StringBuilder
    var char = readerRead
    while(char >= 0) {
      //	  println(char)
      sb.append(new String(Character.toChars(char)))
      char = readerRead
    }
    if(debugField) print("[" + sb + "]")
    sb.toString
  }

  /**
    * Lectura segura de string de len caracteres
    */
  def safeReadString(len: Int): String = {
    try {
      readString(len)
    }
    catch {
      case _: Exception => ""
    }
  }

  /**
    * Leer un string de longitud igual a simil
    */
  def readStringLike(simil: String): String = {
    readString(simil.length())
  }

  /**
    * Devolver un string con la lista de campos leidos
    */
  def dumpReadFields(): String = {

    //    def g = (p: (StringBuffer, Int), s: String) => (p._1.append("(").append(p._2).append(", [").append(s).append("])"), p._2 + 1)
    ////    def f = ((b: StringBuffer, field: Int), s: String) => (b.append("(" + field + ", [" + s + "])"), field+1)
    //
    //   val res = debugList.foldLeft((new StringBuffer, 1)) (p, s) => (p._1.append("(").append(p._2).append(", [").append(s).append("])"), p._2 + 1)
    //
    ////    res._1.toString

    // Me canse de remar el fold...
    var buffer = new StringBuffer
    var ndx = 1
    for(s <- debugList) {
      //      buffer.append("(").append(ndx).append(", [").append(s).append("])\n")
      buffer.append(ndx).append(": ").append(s).append("\n")
      ndx += 1
    }

    buffer.toString()
  }

  /**
    * Lee tantos caracteres como requiera el formato y parsea la fecha
    */
  def readDate(format: String): java.util.Date = {
    parseDate(readString(format.length()), format)
  }

  /**
    * Lee fieldLength caracteres, trimea y parsea una fecha con formato format
    */
  def readDateWithTrim(fieldLength: Int, format: String): java.util.Date = {
    parseDate(readString(fieldLength).trim, format)
  }

  /**
    * Parsea dateString en fecha de formato format
    */
  def parseDate(dateString: String, format: String): java.util.Date = {
    val sdf = dateFormatCache.getOrElseUpdate(format, new java.text.SimpleDateFormat(format))
    sdf.parse(dateString)
  }

  /**
    * Lee fieldLength caracteres y parsea la línea como un número decimal
    *
    * @param intCount cantidad de caracteres a ser interpretados como la parte entera
    * @param decimalsCount cantidad de caracteres a ser interpretados como la parte decimal
    * @param splittingChar caracter que separa la parte entera de la decimal. Por omisión se toma '.'
    * @return String
    */
  def readBigDecimalAsString(intCount: Int, decimalsCount: Int, splittingChar: Char = '.'): String = {
    readString(intCount) + splittingChar.toString + readString(decimalsCount)
  }

  /**
    * Lee fieldLength caracteres y parsea la línea como un número decimal de forma segura, esto es, en caso de error
    * retorna un string con partes entera y decimal en 0
    *
    * @param intCount cantidad de caracteres a ser interpretados como la parte entera
    * @param decimalsCount cantidad de caracteres a ser interpretados como la parte decimal
    * @param splittingChar caracter que separa la parte entera de la decimal. Por omisión se toma '.'
    * @return String
    */
  def safeReadBigDecimalAsString(intCount: Int, decimalsCount: Int, splittingChar: Char = '.'): String = {
    Try{readBigDecimalAsString(intCount, decimalsCount, splittingChar)} match {
      case Success(s: String) => s
      case _ =>
        if (intCount <= 0 || decimalsCount <= 0) s"0${splittingChar.toString}00"
        else "0" * intCount + splittingChar.toString + "0" * decimalsCount
    }
  }

}
