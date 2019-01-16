package com.gurpreetsk.android_starter._http.utils

import android.os.Build
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import timber.log.Timber
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

/**
 * Enables TLS v1.2 when creating SSLSockets.
 *
 *
 * For some reason, android supports TLS v1.2 from API 16, but enables it by
 * default only from API 20.
 * @link https://developer.android.com/reference/javax/net/ssl/SSLSocket.html
 * @see SSLSocketFactory
 */
class Tls12SocketFactory(private val delegate: SSLSocketFactory) : SSLSocketFactory() {
  override fun getDefaultCipherSuites(): Array<String> {
    return delegate.defaultCipherSuites
  }

  override fun getSupportedCipherSuites(): Array<String> {
    return delegate.supportedCipherSuites
  }

  @Throws(IOException::class)
  override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket? {
    return patch(delegate.createSocket(s, host, port, autoClose))
  }

  @Throws(IOException::class, UnknownHostException::class)
  override fun createSocket(host: String, port: Int): Socket? {
    return patch(delegate.createSocket(host, port))
  }

  @Throws(IOException::class, UnknownHostException::class)
  override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket? {
    return patch(delegate.createSocket(host, port, localHost, localPort))
  }

  @Throws(IOException::class)
  override fun createSocket(host: InetAddress, port: Int): Socket? {
    return patch(delegate.createSocket(host, port))
  }

  @Throws(IOException::class)
  override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket? {
    return patch(delegate.createSocket(address, port, localAddress, localPort))
  }

  private fun patch(s: Socket): Socket {
    if (s is SSLSocket) {
      s.enabledProtocols = TLS_V12_ONLY
    }
    return s
  }

  companion object {
    private val TLS_V12_ONLY = arrayOf("TLSv1.2")
  }
}

/**
 * return [OkHttpClient.Builder] for preLollipop devices with TLS v1.2.
 */
fun OkHttpClient.Builder.enableTls12OnPreLollipop(): OkHttpClient.Builder {
  if (Build.VERSION.SDK_INT in 16..21) {
    try {
      val sslContext = SSLContext.getInstance("TLSv1.2")
      sslContext.init(null, null, null)
      this.sslSocketFactory(Tls12SocketFactory(sslContext.socketFactory))

      val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
          .tlsVersions(TlsVersion.TLS_1_2)
          .build()

      val connectionSpecs = ArrayList<ConnectionSpec>()
      connectionSpecs.add(connectionSpec)
      connectionSpecs.add(ConnectionSpec.COMPATIBLE_TLS)
      connectionSpecs.add(ConnectionSpec.CLEARTEXT)

      this.connectionSpecs(connectionSpecs)
          .followRedirects(true)
          .followSslRedirects(true)
    } catch (exception: Exception) {
      Timber.e("OkHttpTLSCompat: Error while setting TLS 1.2, $exception")
    }
  }

  return this
}
