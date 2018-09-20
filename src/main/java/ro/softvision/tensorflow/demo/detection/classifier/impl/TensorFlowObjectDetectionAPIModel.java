package ro.softvision.tensorflow.demo.detection.classifier.impl;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.util.ArrayList;
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

    private static final int MAX_RESULTS = 100;

    private TensorFlowInferenceInterface tensorFlowInferenceInterface;

    private ArrayList<String> labels = new ArrayList<>();

    private int inputSize;
    private float[] detectionBoxes;
    private float[] detectionClasses;
    private float[] detectionScores;
    private float[] numDetection;

    private int[] pixelBuffer;
    private byte[] bufferByChannel;

    private ArrayList<Recognition> recognitions = new ArrayList<>();

    public TensorFlowObjectDetectionAPIModel(AssetManager assetManager, String modelFileName, String labelsFileName, int inputSize) throws IOException {
        this.inputSize = inputSize;

        initializeLabels(labels, assetManager, labelsFileName);

        tensorFlowInferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFileName);

        pixelBuffer = new int[inputSize * inputSize];
        bufferByChannel = new byte[inputSize * inputSize * 3];

        detectionBoxes = new float[MAX_RESULTS * 4];
        detectionClasses = new float[MAX_RESULTS];
        detectionScores = new float[MAX_RESULTS];
        numDetection = new float[1];
    }

    @Override
    public List<Recognition> recognizeImage(Bitmap bitmap) {

        processBitmap(bitmap, pixelBuffer, bufferByChannel);

        tensorFlowInferenceInterface.feed(INPUT_NAME, bufferByChannel, 1, inputSize, inputSize, 3);

        tensorFlowInferenceInterface.run(OUTPUTS);

        tensorFlowInferenceInterface.fetch(OUTPUT_DETECTION_BOXES, detectionBoxes);
        tensorFlowInferenceInterface.fetch(OUTPUT_DETECTION_CLASSES, detectionClasses);
        tensorFlowInferenceInterface.fetch(OUTPUT_DETECTION_SCORES, detectionScores);
        tensorFlowInferenceInterface.fetch(OUTPUT_NUM_DETECTION, numDetection);

        populateRecognitions(recognitions, Math.round(numDetection[0]), labels, detectionClasses, detectionScores, detectionBoxes, inputSize);

        return recognitions;
    }

}
