package com.hardik.mlfacerecognitiondemo

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    private lateinit var txtResult:TextView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var imgcamera = findViewById<ImageView>(R.id.img_camera)
        txtResult = findViewById<TextView>(R.id.txt_Result)

        imgcamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, 1)
            }else{
                Toast.makeText(this, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            val extras = data.extras
            val bitmap = extras?.get("data") as? Bitmap
            if (bitmap != null) {
                detectFace(bitmap)
            }
        }
    }

    private fun detectFace(bitmap:Bitmap) {
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        val detector = FaceDetection.getClient(highAccuracyOpts)
        val image = InputImage.fromBitmap(bitmap, 0)

        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully, face is detected successfully...
                var resultText = " "
                var i = 1
                for(face in faces){
                    resultText = "Number of Faces : $i" +
                            "\n Smile Percentage : ${face.smilingProbability?.times(100)}%" +
                            "\n Left Eye Open Percentage : ${face.leftEyeOpenProbability?.times(100)}%" +
                            "\n Right Eye Open Percentage : ${face.rightEyeOpenProbability?.times(100)}%" +
                            "\n head Euler From X Axis : ${face.headEulerAngleX.times(100)}%"
                    i++
                }
                if (faces.isEmpty()){
                    Toast.makeText(this, "Face Cannot Detected!", Toast.LENGTH_LONG).show()
                }else{
                    txtResult.text = resultText
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
            }
    }
}