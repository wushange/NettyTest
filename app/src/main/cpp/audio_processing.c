#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

void calculate_fft(double* data, int n);

JNIEXPORT jdoubleArray JNICALL Java_com_example_testsocket_jni_AudioProcessing_getSpectrum
        (JNIEnv *env, jobject obj, jstring jFilePath) {
    const char* file_path = (*env)->GetStringUTFChars(env, jFilePath, NULL);
    FILE* file = fopen(file_path, "rb");
    if (!file) {
        return NULL;
    }

    fseek(file, 44, SEEK_SET);  // Skip WAV header
    short buffer[1024];
    double data[1024];
    int n = 1024;

    if (fread(buffer, sizeof(short), n, file) == n) {
        for (int i = 0; i < n; i++) {
            data[i] = buffer[i] / 32768.0;  // Normalize
        }
        calculate_fft(data, n);
    }

    fclose(file);

    jdoubleArray result = (*env)->NewDoubleArray(env, n / 2);
    if (result == NULL) {
        return NULL;  // Out of memory error thrown
    }
    (*env)->SetDoubleArrayRegion(env, result, 0, n / 2, data);
    (*env)->ReleaseStringUTFChars(env, jFilePath, file_path);
    return result;
}

void calculate_fft(double* data, int n) {
    // Simple FFT implementation (for demonstration purposes only)
    // Use a proper FFT library for real applications
    int i, j, k, m;
    int M = log2(n);
    for (i = 0; i < n; ++i) {
        j = 0;
        for (k = 0; k < M; ++k) {
            j |= ((i >> k) & 1) << (M - 1 - k);
        }
        if (j > i) {
            double temp = data[i];
            data[i] = data[j];
            data[j] = temp;
        }
    }

    for (i = 1; i <= M; ++i) {
        int m = 1 << i;
        double delta = M_PI / (m >> 1);
        double w_real = cos(delta);
        double w_imag = -sin(delta);
        for (j = 0; j < n; j += m) {
            double u_real = 1;
            double u_imag = 0;
            for (k = 0; k < (m >> 1); ++k) {
                double t_real = u_real * data[j + k + (m >> 1)] - u_imag * data[j + k + (m >> 1)];
                double t_imag = u_real * data[j + k + (m >> 1)] + u_imag * data[j + k + (m >> 1)];
                data[j + k + (m >> 1)] = data[j + k] - t_real;
                data[j + k] += t_real;
                double temp_real = u_real;
                u_real = u_real * w_real - u_imag * w_imag;
                u_imag = temp_real * w_imag + u_imag * w_real;
            }
        }
    }
}
