package com.dicoding.edival.ui.ui

import android.Manifest
//import android.R.attr.height
//import android.R.attr.width
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
//import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dicoding.edival.R
import com.dicoding.edival.databinding.FragmentCameraBinding
import com.dicoding.edival.ml.Detect
//import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
//import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
//import com.google.firebase.ml.custom.FirebaseCustomLocalModel
//import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
//import com.google.firebase.ml.custom.FirebaseModelDataType
//import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions
//import com.google.firebase.ml.custom.FirebaseModelInputs
//import com.google.firebase.ml.custom.FirebaseModelInterpreter
//import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
//import java.math.BigDecimal
//import java.math.RoundingMode
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Arrays
//import java.util.concurrent.ExecutorService


class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
//    private var imageCapture: ImageCapture? = null
//    private lateinit var cameraExecutor: ExecutorService
//    var interpreter: FirebaseModelInterpreter? = null
//    var options: FirebaseModelInterpreterOptions? = null

    ////
    private lateinit var labels: List<String>
    private lateinit var model: Detect
    private var colors = listOf(Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK, Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)
    private val paint = Paint()
    private lateinit var imageProcessor: ImageProcessor
    lateinit var bitmap: Bitmap
    private lateinit var result: Bitmap
    private var pausePredict: Boolean = false
    lateinit var detectedFood: List<String>             // INFO: List of detected foods that will be used in Result Page
    private lateinit var detectedFoodMap: MutableMap<String,Int>
    lateinit var cameraDevice: CameraDevice
    private lateinit var cameraManager: CameraManager
    lateinit var handler: Handler
    lateinit var textureView: TextureView
    private lateinit var imageView: ImageView
    ////

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Request camera permissions
        if (allPermissionsGranted()) {
//            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        /////
        pausePredict = false
        labels = FileUtil.loadLabels(this.requireContext(), "labelmap.txt")
        imageProcessor = ImageProcessor.Builder().add(ResizeOp(320, 320, ResizeOp.ResizeMethod.BILINEAR)).build()
        model = Detect.newInstance(this.requireContext())
        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        imageView = view.findViewById(R.id.imageView)

        textureView = view.findViewById(R.id.textureView)
        textureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                openCam()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                if(!pausePredict) {
                    bitmap = textureView.bitmap!!
                    predict()
                }
            }
        }
        cameraManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        /////

        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener { capture() }

//        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @SuppressLint("MissingPermission")
    private fun openCam() {
        cameraManager.openCamera(cameraManager.cameraIdList[0], object:CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0
                val surfaceTexture = textureView.surfaceTexture
                val surface = Surface(surfaceTexture)
                val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)
                cameraDevice.createCaptureSession(listOf(surface), object:CameraCaptureSession.StateCallback(){
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(), null, null)
                    }
                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                    }
                }, handler)
            }

            override fun onDisconnected(p0: CameraDevice) {
            }

            override fun onError(p0: CameraDevice, p1: Int) {
            }

        }, handler)
    }

    private fun capture() {
        pausePredict = true
        saveBitmapImage(result)
        // TODO: NAVIGATE TO RESULT PAGE AND PASS THE detectedFood variable. The annotated image can be found in Pictures/Edival/
    }

    private fun saveBitmapImage(bitmap: Bitmap) {
        val timestamp = System.currentTimeMillis()

        //Tell the media scanner about the new file so that it is immediately available to the user.
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name))
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            val uri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                try {
                    val outputStream = activity?.contentResolver?.openOutputStream(uri)
                    if (outputStream != null) {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            outputStream.close()
                        } catch (e: Exception) {
                            Log.e(TAG, "saveBitmapImage: ", e)
                        }
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    activity?.contentResolver?.update(uri, values, null, null)

                    Toast.makeText(requireContext(), "Saved...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e(TAG, "saveBitmapImage: ", e)
                }
            }
        } else {
            val imageFileFolder = File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name))
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdirs()
            }
            val mImageName = "$timestamp.png"
            val imageFile = File(imageFileFolder, mImageName)
            try {
                val outputStream: OutputStream = FileOutputStream(imageFile)
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    Log.e(TAG, "saveBitmapImage: ", e)
                }
                values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                Toast.makeText(requireContext(), "Saved...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "saveBitmapImage: ", e)
            }
        }
    }

    private fun predict() {
        detectedFood = emptyList()
        detectedFoodMap = mutableMapOf()
        val initialWidth = bitmap.width
        val initialHeight = bitmap.height
        bitmap = Bitmap.createScaledBitmap(bitmap, 320, 320, true)
//                var byteBuffer = ByteBuffer.allocate(bitmap.byteCount)
//                bitmap.copyPixelsToBuffer(byteBuffer)
//                byteBuffer.rewind()
        val byteBuffer = ByteBuffer.allocateDirect(3 * 320 * 320 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        // Copy only the RGB channels from the Bitmap into the ByteBuffer
        val pixels = IntArray(320 * 320)
        bitmap.getPixels(pixels, 0, 320, 0, 0, 320, 320)

        for (pixel in pixels) {
            // Extract RGB channels and discard alpha channel
            byteBuffer.put((pixel shr 16 and 0xFF).toByte()) // Red
            byteBuffer.put((pixel shr 8 and 0xFF).toByte()) // Green
            byteBuffer.put((pixel and 0xFF).toByte()) // Blue
        }

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 320, 320, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray   // confidence score
        val outputFeature1 = outputs.outputFeature1AsTensorBuffer.floatArray   // bounding box
        val outputFeature2 = outputs.outputFeature2AsTensorBuffer.floatArray   // number of detection
        val outputFeature3 = outputs.outputFeature3AsTensorBuffer.floatArray   // classes
        Log.i("Confidence", Arrays.toString(outputFeature0))
        Log.i("Box", Arrays.toString(outputFeature1))
        Log.i("NumDetection", Arrays.toString(outputFeature2))
        Log.i("ClassIdx", Arrays.toString(outputFeature3))

        val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutable)

        val h = mutable.height
        val w = mutable.width
        paint.textSize = h/15f
        paint.strokeWidth = h/85f
        var x = 0
        outputFeature0.forEachIndexed { index, f1 ->
            x = index
            x *= 4
            if (f1 > 0.5) {
                paint.color = colors[index]
                // draw box
                paint.style = Paint.Style.STROKE
                canvas.drawRect(RectF(outputFeature1[x+1]*w, outputFeature1[x]*h, outputFeature1[x+3]*w, outputFeature1[x+2]*h), paint)
                // label naming
                var label = labels[outputFeature3[index].toInt()]
                detectedFoodMap[label] = detectedFoodMap.getOrDefault(label, 0) + 1
                label += detectedFoodMap[label]
                detectedFood += label
//                Log.i("DetectedFood", detectedFood.toString())
                // draw label and confidence level
                paint.style = Paint.Style.FILL
                canvas.drawText(labels[outputFeature3[index].toInt()] +" "+ "%.3f".format(outputFeature0[index]), outputFeature1[x+1]*w, outputFeature1[x]*h, paint)
            }
        }
        result = Bitmap.createScaledBitmap(mutable, initialWidth, initialHeight, true)
        imageView.setImageBitmap(result)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
//                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        model.close()
//        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}
