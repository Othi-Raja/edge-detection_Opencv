#include <jni.h>
#include <string>
#include <android/log.h>
#include <opencv2/imgproc.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/core.hpp>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "native-lib", __VA_ARGS__)

using namespace cv;

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_example_edgeview_NativeBridge_processFrame(JNIEnv* env, jclass, jbyteArray input, jint width, jint height) {
  jbyte* inBuf = env->GetByteArrayElements(input, NULL);
  Mat yuv(height + height/2, width, CV_8UC1, (unsigned char*)inBuf);
  Mat rgb; cv::cvtColor(yuv, rgb, COLOR_YUV2RGBA_NV21);
  Mat gray; cv::cvtColor(rgb, gray, COLOR_RGBA2GRAY);
  Mat edges; cv::Canny(gray, edges, 50, 150);
  Mat out; cv::cvtColor(edges, out, COLOR_GRAY2RGBA);
  int outSize = out.total() * out.elemSize();
  jbyteArray outArr = env->NewByteArray(outSize);
  env->SetByteArrayRegion(outArr, 0, outSize, (jbyte*)out.data);
  env->ReleaseByteArrayElements(input, inBuf, 0);
  return outArr;
}
