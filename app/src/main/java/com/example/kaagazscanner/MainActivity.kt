package com.example.kaagazscanner
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
typealias LumaListener = (luma: Double) -> Unit

class MainActivity : AppCompatActivity() {
   private var camera:Camera?=null
    private var preview:Preview?=null
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PERMISSION_GRANTED){
            startCamera()

        }
        else
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),0)

        }
        camera_capture_button.setOnClickListener {
            takePhoto()

        }

    }

    private fun takePhoto() {
        //save photo
        outputDirectory=File(externalMediaDirs.get(0),"kaagazScanner-${System.currentTimeMillis()}.jpg")
        val output=ImageCapture.OutputFileOptions.Builder(outputDirectory).build()
        imageCapture?.takePicture(output,ContextCompat.getMainExecutor(this),object :ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
               Toast.makeText(applicationContext,"Image Saved",Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {

            }

        })


    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PERMISSION_GRANTED){

startCamera()
        }
        else
        {
           Toast.makeText(this,"please accept the permission",Toast.LENGTH_SHORT).show()

        }
    }

    private fun startCamera() {
       val cameraProviderFuture=ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
                val cameraProvider=cameraProviderFuture.get()
            preview=Preview.Builder().build()
          preview?.setSurfaceProvider(viewFinder.createSurfaceProvider(camera?.cameraInfo))
imageCapture=ImageCapture.Builder().build()
 val cameraSelector=CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            cameraProvider.unbindAll()
            camera=cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
        },ContextCompat.getMainExecutor(this))

    }


}