package com.dicoding.edival.ui.ui

import android.Manifest
//import android.R.attr.height
//import android.R.attr.width
import android.annotation.SuppressLint
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
import android.os.Handler
import android.os.HandlerThread
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
    lateinit var labels: List<String>
    lateinit var model: Detect
    var colors = listOf<Int>(Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK, Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)
    val paint = Paint()
    lateinit var imageProcessor: ImageProcessor
    lateinit var bitmap: Bitmap
    lateinit var cameraDevice: CameraDevice
    lateinit var cameraManager: CameraManager
    lateinit var handler: Handler
    lateinit var textureView: TextureView
    lateinit var imageView: ImageView
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
                bitmap = textureView.bitmap!!
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
                        paint.style = Paint.Style.STROKE
                        canvas.drawRect(RectF(outputFeature1[x+1]*w, outputFeature1[x]*h, outputFeature1[x+3]*w, outputFeature1[x+2]*h), paint)
                        paint.style = Paint.Style.FILL
                        canvas.drawText(labels[outputFeature3[index].toInt()] +" "+ "%.3f".format(outputFeature0[index]), outputFeature1[x+1]*w, outputFeature1[x]*h, paint)
                    }
                }
                imageView.setImageBitmap(mutable)
            }
        }
        cameraManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        /////

//        // Set up the listeners for take photo and video capture buttons
//        binding.imageCaptureButton.setOnClickListener { takePhoto() }
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @SuppressLint("MissingPermission")
    private fun openCam() {
        cameraManager.openCamera(cameraManager.cameraIdList[0], object:CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0
                var surfaceTexture = textureView.surfaceTexture
                var surface = Surface(surfaceTexture)
                var captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
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

//    private fun startCamera() {
//        // Implementasi logika inisialisasi kamera
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//
//        // download the tflite model and set the interpreter
//        setInterpreter()
//
//        cameraProviderFuture.addListener({
//            // Used to bind the lifecycle of cameras to the lifecycle owner
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//            // Preview
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//                }
//
//            imageCapture = ImageCapture.Builder()
//                .build()
//
//            // Select back camera as a default
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                // Unbind use cases before rebinding
//                cameraProvider.unbindAll()
//
//                // Bind use cases to camera
//                cameraProvider.bindToLifecycle(
//                    requireActivity(), cameraSelector, preview, imageCapture)
//
//            } catch(exc: Exception) {
//                Log.e(TAG, "Use case binding failed", exc)
//            }
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }

//    private fun takePhoto() {
//        // Implementasi logika pengambilan foto
//        val imageCapture = imageCapture ?: return
//
//        // Create time stamped name and MediaStore entry.
//        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//            .format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }
//        }
//
//        // Create output options object which contains file + metadata
//        val outputOptions = ImageCapture.OutputFileOptions
//            .Builder(requireContext().contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues)
//            .build()
//
//        // Set up image capture listener, which is triggered after photo has
//        // been taken
//        try {
//            imageCapture.takePicture(
//                outputOptions,
//                ContextCompat.getMainExecutor(requireContext()),
//                object : ImageCapture.OnImageSavedCallback {
//                    override fun onError(exc: ImageCaptureException) {
//                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                    }
//
//                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                        val msg = "Photo capture succeeded: ${output.savedUri}"
//                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
//                        Log.d(TAG, msg)
//
////                        TODO:
//
////                        convert image to bitmap and pass it to predict function
////                        predict(image)
//                    }
//                }
//            )
//        } catch (e: Exception) {
//            Log.e(TAG, "Error taking photo: ${e.message}", e)
//        }
//    }

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

//    private fun setInterpreter() {
//        Log.i("info", "setInterpreter started")
//        val remoteModel = FirebaseCustomRemoteModel.Builder("edival-model").build()
//        val conditions = FirebaseModelDownloadConditions.Builder()
//            .requireWifi()
//            .build()
//        FirebaseModelManager.getInstance().download(remoteModel, conditions)
//            .addOnCompleteListener {
//                // Success.
//                Log.i("Info", "Successfully downloaded model");
//            }
//        val localModel = FirebaseCustomLocalModel.Builder()
//            .setAssetFilePath("detect.tflite")
//            .build()
//        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
//            .addOnSuccessListener { isDownloaded ->
//                options =
//                    if (isDownloaded) {
//                        FirebaseModelInterpreterOptions.Builder(remoteModel).build()
//                    } else {
//                        FirebaseModelInterpreterOptions.Builder(localModel).build()
//                    }
//                interpreter = FirebaseModelInterpreter.getInstance(options!!)
//                Log.i("info", "setInterpreter executed")
//            }
//    }
//
//    private fun predict(image: Bitmap) {
//        Log.i("info", "predict started")
//        val bitmap = Bitmap.createScaledBitmap(image, 320, 320, true)
//
//        val batchNum = 0
//        val input = Array(1) { Array(320) { Array(320) { FloatArray(3) } } }
//        for (x in 0..223) {
//            for (y in 0..223) {
//                val pixel = bitmap.getPixel(x, y)
//                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
//                // model. For example, some models might require values to be normalized
//                // to the range [0.0, 1.0] instead.
//                input[batchNum][x][y][0] = ((Color.red(pixel) - 127.5) / 127.5).toFloat()
//                input[batchNum][x][y][1] = ((Color.green(pixel) - 127.5) / 127.5).toFloat()
//                input[batchNum][x][y][2] = ((Color.blue(pixel) - 127.5) / 127.5).toFloat()
//            }
//        }
//        val inputs = FirebaseModelInputs.Builder()
//            .add(input) // add() as many input arrays as your model requires
//            .build()
//        val inputOutputOptions = FirebaseModelInputOutputOptions.Builder()
//            .setInputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 224, 224, 3))
//            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 10))       // confidence
//            .setOutputFormat(1, FirebaseModelDataType.FLOAT32, intArrayOf(1, 10, 4))    // bounding box
//            .setOutputFormat(2, FirebaseModelDataType.FLOAT32, intArrayOf(1))
//            .setOutputFormat(3, FirebaseModelDataType.FLOAT32, intArrayOf(1, 10))       // class index
//            .build()
//        interpreter?.run(inputs, inputOutputOptions)
//            ?.addOnSuccessListener { result ->
//                // ...
//                val output = result.getOutput<Array<FloatArray>>(0)
//                val probabilities = output[0]
//                Log.i("Info", "probability"+probabilities.toString());
//            }
//            ?.addOnFailureListener { e ->
//                // Task failed with an exception
//                // ...
//                Log.i("Info", "error task:"+e);
//            }
//    }

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
