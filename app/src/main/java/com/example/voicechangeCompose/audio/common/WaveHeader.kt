package com.example.voicechangeCompose.audio.common

import java.io.ByteArrayOutputStream
import java.io.IOException

class WaveHeader {
    private val fileID = charArrayOf('R', 'I', 'F', 'F')
    private var fileLength: Int
    private val wavTag = charArrayOf('W', 'A', 'V', 'E')
    private val fmtHdrID = charArrayOf('f', 'm', 't', ' ')
    private val fmtHdrLen = 16
    private val formatTag: Short = 1
    var channels: Short = 1
    var sampleRate: Short = 16000
    var bitsPerSample: Short = 16
    private var blockAlign = (channels * bitsPerSample / 8).toShort()
    private var avgBytesPerSec = blockAlign * sampleRate
    private val dataHdrID = charArrayOf('d', 'a', 't', 'a')
    private var dataHdrLen: Int

    constructor(fileLength: Int) {
        this.fileLength = fileLength + (44 - 8)
        dataHdrLen = fileLength
    }

    constructor(fileLength: Int, channels: Short, sampleRate: Short, bitsPerSample: Short) {
        this.fileLength = fileLength + (44 - 8)
        dataHdrLen = fileLength
        this.channels = channels
        this.sampleRate = sampleRate
        this.bitsPerSample = bitsPerSample
        blockAlign = (channels * bitsPerSample / 8).toShort()
        avgBytesPerSec = blockAlign * sampleRate
    }

    /**
     * @return byte[] 44个字节
     * @throws IOException
     */
    @get:Throws(IOException::class)
    val header: ByteArray
        get() {
            val bos = ByteArrayOutputStream()
            writeChar(bos, fileID)
            writeInt(bos, fileLength)
            writeChar(bos, wavTag)
            writeChar(bos, fmtHdrID)
            writeInt(bos, fmtHdrLen)
            writeShort(bos, formatTag.toInt())
            writeShort(bos, channels.toInt())
            writeInt(bos, sampleRate.toInt())
            writeInt(bos, avgBytesPerSec)
            writeShort(bos, blockAlign.toInt())
            writeShort(bos, bitsPerSample.toInt())
            writeChar(bos, dataHdrID)
            writeInt(bos, dataHdrLen)
            bos.flush()
            val r = bos.toByteArray()
            bos.close()
            return r
        }

    @Throws(IOException::class)
    private fun writeShort(bos: ByteArrayOutputStream, s: Int) {
        val myByte = ByteArray(2)
        myByte[1] = (s shl 16 shr 24).toByte()
        myByte[0] = (s shl 24 shr 24).toByte()
        bos.write(myByte)
    }

    @Throws(IOException::class)
    private fun writeInt(bos: ByteArrayOutputStream, n: Int) {
        val buf = ByteArray(4)
        buf[3] = (n shr 24).toByte()
        buf[2] = (n shl 8 shr 24).toByte()
        buf[1] = (n shl 16 shr 24).toByte()
        buf[0] = (n shl 24 shr 24).toByte()
        bos.write(buf)
    }

    private fun writeChar(bos: ByteArrayOutputStream, id: CharArray) {
        for (i in id.indices) {
            val c = id[i]
            bos.write(c.code)
        }
    }
}