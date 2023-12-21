package com.dicoding.edival.ui.ui

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dicoding.edival.databinding.FragmentCameraBinding
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
//import com.google.firebase.ml.custom.FirebaseCustomLocalModel
//import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
//import com.google.firebase.ml.custom.FirebaseModelDataType
//import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions
//import com.google.firebase.ml.custom.FirebaseModelInputs
//import com.google.firebase.ml.custom.FirebaseModelInterpreter
//import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions
//import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
//    var interpreter: FirebaseModelInterpreter? = null
    var modelFile: File? = null
//    var options: FirebaseModelInterpreterOptions? = null

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
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        // Implementasi logika inisialisasi kamera
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        // download the tflite model and set the interpreter
//        setInterpreter()

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    requireActivity(), cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Implementasi logika pengambilan foto
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        try {
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val msg = "Photo capture succeeded: ${output.savedUri}"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, msg)

//                        TODO:
//                        convert image to bitmap and pass it to predict function
//                        predict(image)
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error taking photo: ${e.message}", e)
        }
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
                startCamera()
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
        cameraExecutor.shutdown()
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
//            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 10))
//            .setOutputFormat(1, FirebaseModelDataType.FLOAT32, intArrayOf(1, 10, 4))
//            .setOutputFormat(2, FirebaseModelDataType.FLOAT32, intArrayOf(1))
//            .setOutputFormat(3, FirebaseModelDataType.FLOAT32, intArrayOf(1, 10))
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
