/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package ro.softvision.tensorflow.demo.ui.common.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.TypedValue;

import ro.softvision.tensorflow.demo.detection.classifier.Recognition;
import ro.softvision.tensorflow.demo.ui.common.BorderedTextRenderer;
import ro.softvision.tensorflow.demo.util.Logger;

/**
 * A tracker wrapping ObjectTracker that also handles non-max suppression and matching existing
 * objects to new detections.
 */
public class RecognitionRenderer {
  private final Logger logger = new Logger();

  private static final float TEXT_SIZE_DIP = 18;

  private static final float MIN_SIZE = 16.0f;

  private final Paint boxPaint = new Paint();

  private final float textSizePx;
  private final BorderedTextRenderer borderedTextRenderer;

  private Matrix frameToCanvasMatrix;

  private int frameWidth;
  private int frameHeight;

  private int sensorOrientation;

  private Recognition trackedObject;

  public RecognitionRenderer(final Context context) {
    boxPaint.setColor(Color.RED);
    boxPaint.setStyle(Style.STROKE);
    boxPaint.setStrokeWidth(12.0f);
    boxPaint.setStrokeCap(Cap.ROUND);
    boxPaint.setStrokeJoin(Join.ROUND);
    boxPaint.setStrokeMiter(100);

    textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, context.getResources().getDisplayMetrics());
    borderedTextRenderer = new BorderedTextRenderer(textSizePx);
  }

  private Matrix getFrameToCanvasMatrix() {
    return frameToCanvasMatrix;
  }

  public synchronized void drawDebug(final Canvas canvas) {
    final Paint textPaint = new Paint();
    textPaint.setColor(Color.WHITE);
    textPaint.setTextSize(60.0f);

    final Paint boxPaint = new Paint();
    boxPaint.setColor(Color.RED);
    boxPaint.setAlpha(200);
    boxPaint.setStyle(Style.STROKE);

    final RectF rect = trackedObject.getLocation();
    canvas.drawRect(rect, boxPaint);
    canvas.drawText("" + trackedObject.getConfidence(), rect.left, rect.top, textPaint);
    borderedTextRenderer.drawText(canvas, rect.centerX(), rect.centerY(), "" + trackedObject.getTitle());
  }

  public synchronized void draw(final Canvas canvas) {
    final boolean rotated = sensorOrientation % 180 == 90;
    final float multiplier =
        Math.min(canvas.getHeight() / (float) (rotated ? frameWidth : frameHeight),
                 canvas.getWidth() / (float) (rotated ? frameHeight : frameWidth));
    frameToCanvasMatrix =
        ImageUtils.getTransformationMatrix(
            frameWidth,
            frameHeight,
            (int) (multiplier * (rotated ? frameHeight : frameWidth)),
            (int) (multiplier * (rotated ? frameWidth : frameHeight)),
            sensorOrientation,
            false);

    if (trackedObject != null) {
      final RectF trackedPos = new RectF(trackedObject.getLocation());
      getFrameToCanvasMatrix().mapRect(trackedPos);
      boxPaint.setColor(Color.BLUE);

      final float cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f;
      canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint);

      final String labelString =
              !TextUtils.isEmpty(trackedObject.getTitle())
                      ? String.format("%s %.2f", trackedObject.getTitle(), trackedObject.getConfidence())
                      : String.format("%.2f", trackedObject.getConfidence());

      borderedTextRenderer.drawText(canvas, trackedPos.left + cornerSize, trackedPos.bottom, labelString);
    }
  }

  private boolean initialized = false;

  public synchronized void onFrame(
      final int w,
      final int h,
      final int sensorOrientation) {
    if (!initialized) {
      logger.i("Initializing ObjectTracker: %dx%d", w, h);
      frameWidth = w;
      frameHeight = h;
      this.sensorOrientation = sensorOrientation;
      initialized = true;
    }
  }

  public void processResult(Recognition recognition) {
    final Matrix rgbFrameToScreen = new Matrix(getFrameToCanvasMatrix());

    if (recognition.getLocation() == null) {
      return;
    }

    final RectF detectionFrameRect = new RectF(recognition.getLocation());

    final RectF detectionScreenRect = new RectF();
    rgbFrameToScreen.mapRect(detectionScreenRect, detectionFrameRect);

    logger.v(
            "Result! Frame: " + recognition.getLocation() + " mapped to screen:" + detectionScreenRect);

    if (detectionFrameRect.width() < MIN_SIZE || detectionFrameRect.height() < MIN_SIZE) {
      logger.w("Degenerate rectangle! " + detectionFrameRect);
      return;
    }

    trackedObject = recognition;
  }

}
