package utils

import java.net.URLEncoder

import org.apache.commons.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

import play.api.Logger

object Encryptor {

  def encrypt(value: String, key: String, initVector: String): String = {
    try {
      val skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES")
      val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
      val iv = new IvParameterSpec(initVector.getBytes("UTF-8"))
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
      val encrypted = cipher.doFinal(value.getBytes)
      System.out.println("encrypted string: " + Base64.encodeBase64String(encrypted))
      val urlEncodedToken = URLEncoder.encode(Base64.encodeBase64String(encrypted),"UTF-8")
      return urlEncodedToken
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
    null
  }


  def decrypt(encrypted: String, key: String, initVector: String): String = {
    try {
      val skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES")
      val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
      val iv = new IvParameterSpec(initVector.getBytes("UTF-8"))
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
      val original = cipher.doFinal(Base64.decodeBase64(encrypted))
      return new String(original)
    } catch {
      case ex: Exception =>
       Logger.error(ex.getMessage)
        throw ex
    }

  }
}
