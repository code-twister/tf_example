package ro.softvision.tensorflow.demo.detection.classifier.impl;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ro.softvision.tensorflow.demo.detection.classifier.Classifier;
import ro.softvision.tensorflow.demo.detection.classifier.Recognition;

import static ro.softvision.tensorflow.demo.detection.classifier.impl.TensorFlowObjectDetectionUtil.INPUT_NAME;
import static ro.softvision.tensorflow.demo.detection.classifier.impl.TensorFlowObjectDetectionUtil.OUTPUTS;
import static ro.softvision.tensorflow.demo.detection.classifier.impl.TensorFlowObjectDetectionUtil.OUTPUT_DETECTION_BOXES;
import static ro.softvision.tensorflow.demo.detection.classifier.impl.TensorFlowObjectDetectionUtil.OUTPUT_DETECTION_CLASSES;
import static ro.softvision.tensorflow.demo.detection.classifier.impl.TensorFlowObjectDetectionUtil.OUTPUT_DETECTION_SCORES;
import static ro.softvision.tensorflow.demo.detection.classifier.impl.TensorFlowObjectDetectionUtil.OUTPUT_NUM_DETECTION;
import static ro.softvision.tensorflow.demo.detection.classifier.impl.TensorFlowObjectDetectionUtil.initializeLabels;
import static ro.softvision.tensorflow.demo.detection.classifier.impl.TensorFlowObjectDetectionUtil.populateRecognitions;
import static ro.softvision.tensorflow.demo.detection.classifier.impl.TensorFlowObjectDetectionUtil.processBitmap;

public class TensorFlowObjectDetectionAPIModel implements Classifier {


    public TensorFlowObjectDetectionAPIModel(AssetManager assetManager, String modelFileName, String labelsFileName, int inputSize) throws IOException {

    }

    @Override
    public List<Recognition> recognizeImage(Bitmap bitmap) {
        return Collections.emptyList();
    }

}
