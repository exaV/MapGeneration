//
//  ethergl_video.m
//  ethergl-video
//
//  Created by Stefan Müller Arisona on 29/06/14.
//  Copyright (c) 2014 Corebounce Association. All rights reserved.
//

#import "JavaNativeFoundation/JNFJNI.h"

#include <string>

#import <AVFoundation/AVFoundation.h>


#import "ethergl_video.h"

//#define MSG(a) printf(a); fflush(0);
#define MSG(...) { printf(__VA_ARGS__); fflush(stdout); }

class AVAssetWrapper {
private:
    AVAssetReader* reader;
    AVAssetImageGenerator* generator;
    
    double duration;
    double frameRate;
    CGSize size;
    
public:
    AVAssetWrapper(std::string url) {
        printf("create asset: %s\n", url.c_str());
        
		NSURL* nsUrl = [NSURL URLWithString:[NSString stringWithCString:url.c_str() encoding:NSUTF8StringEncoding]];
		if (!nsUrl) {
			MSG("avassetwrapper: invalid url '%s'\n", url.c_str());
            throw std::invalid_argument("invalid url");
		}
		
        NSDictionary* options = @{ AVURLAssetPreferPreciseDurationAndTimingKey : @YES };
        
		AVAsset* asset = [AVURLAsset URLAssetWithURL:nsUrl options:options];
		if (!asset) {
			MSG("avassetwrapper: invalid url '%s'\n", url.c_str());
            throw std::invalid_argument("invalid url");
		}
        
		NSArray* tracks = [asset tracksWithMediaType:AVMediaTypeVideo];
		if ([tracks count] < 1) {
			MSG("avassetwrapper: no video track for '%s'\n", url.c_str());
            throw std::invalid_argument("no video track");
		}
		AVAssetTrack* videoTrack = [tracks objectAtIndex:0];
        
        duration = CMTimeGetSeconds([asset duration]);
		frameRate = [videoTrack nominalFrameRate];
        size = [videoTrack naturalSize];
		
        // create reader (for sequential frame-by-frame access)
        
		NSError* error = nil;
		reader = [[AVAssetReader alloc] initWithAsset:asset error:&error];
		if (!reader || error) {
			MSG("avassetwrapper: could not initialize reader for '%s'\n", url.c_str());
            throw std::invalid_argument("could not initialize reader");
		}
		
		NSDictionary* settings = [NSDictionary dictionaryWithObjectsAndKeys:
								  [NSNumber numberWithUnsignedInt:kCVPixelFormatType_32BGRA],
								  (NSString*)kCVPixelBufferPixelFormatTypeKey,
								  nil];
		[reader addOutput:[AVAssetReaderTrackOutput assetReaderTrackOutputWithTrack:videoTrack outputSettings:settings]];
		if ([reader startReading] != YES) {
			[reader release];
			MSG("avfoundation: could not start reading from '%s': %s\n", url.c_str(), [[[reader error] localizedDescription] UTF8String]);
            throw std::invalid_argument("could not start reading");
		}
        
        // create generator (for random access)
        generator = [[AVAssetImageGenerator alloc] initWithAsset:asset];
        
        
        MSG("create asset: %s: duration=%f framerate=%f size=%dx%d\n", url.c_str(), duration, frameRate, (int)size.width, (int)size.height);
    }
    
    ~AVAssetWrapper() {
        [reader release];
        [generator release];
    }
    
    void rewind() {
        // TODO: need to recreate everything, the reader doesn't support seeking/restarting
    }
    
    jintArray getFrame(JNIEnv* env, double time) {
        CMTime cmtime = CMTimeMakeWithSeconds(time, 600);
        
        CMTime actualTime;
        NSError *error;
        CGImageRef image = [generator copyCGImageAtTime:cmtime actualTime:&actualTime error:&error];
        
        if (!image)
            return nullptr;

        NSData* data = (NSData*)CGDataProviderCopyData(CGImageGetDataProvider(image));
        uint32_t* src = (uint32_t*)[data bytes];
        size_t length = [data length]/4;
        
        jintArray array = env->NewIntArray((int)length);
        uint32_t* dst = (uint32_t*)env->GetIntArrayElements(array, nullptr);
        
        for (int i = 0; i < length; ++i) {
            dst[i] = convertBGRAToARGB(src[i]);
        }
        
        env->ReleaseIntArrayElements(array, (int*)dst, 0);
        
        CGImageRelease(image);
        
        return array;
    }

    jintArray getNextFrame(JNIEnv* env) {
        if ([reader status] != AVAssetReaderStatusReading) {
            MSG("get next frame: reached end of movie\n");
            return nullptr;
        }
        
        AVAssetReaderTrackOutput* output = [reader.outputs objectAtIndex:0];
        CMSampleBufferRef sampleBuffer = [output copyNextSampleBuffer];
        if (!sampleBuffer) {
            MSG("getnextframe: could not copy sample buffer\n");
            return nullptr;
        }
        
        CVImageBufferRef imageBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
        
        // Lock the image buffer
        CVPixelBufferLockBaseAddress(imageBuffer, 0);
        
        // Get information of the image
        size_t width = CVPixelBufferGetWidth(imageBuffer);
        size_t height = CVPixelBufferGetHeight(imageBuffer);
        size_t length = width * height;
        uint32_t* src = (uint32_t*)CVPixelBufferGetBaseAddress(imageBuffer);
    
        // Copy image to java int array
        jintArray array = env->NewIntArray((int)length);
        env->SetIntArrayRegion(array, 0, (int)length, (int*)src);
        
        // Unlock the image buffer & cleanup
        CVPixelBufferUnlockBaseAddress(imageBuffer, 0);
        CFRelease(sampleBuffer);
        
        return array;
    }

    int getWidth() {
        return size.width;
    }
    
    int getHeight() {
        return size.height;
    }
    
    double getDuration() {
        return duration;
    }
    
    double getFrameRate() {
        return frameRate;
    }

    int getFrameCount() {
        return duration * frameRate;
    }

private:
    inline uint32_t convertBGRAToARGB(uint32_t i) {
        // BGRA -> ARGB
        uint32_t b = i & 0xff000000;
        uint32_t g = i & 0x00ff0000;
        uint32_t r = i & 0x0000ff00;
        uint32_t a = i & 0x000000ff;
        return a << 24 | r << 8 | g >> 8 | b >> 24;
    }
};


/*
 * Class:     ch_ethz_ether_video_avfoundation_AVAsset
 * Method:    nativeCreate
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_ch_ethz_ether_video_avfoundation_AVAsset_nativeCreate
(JNIEnv * env, jobject, jstring javaURL) {
    JNF_COCOA_ENTER(env);
    
    const char* url = env->GetStringUTFChars(javaURL, JNI_FALSE);
    
    jlong nativeHandle = 0;
    try {
        nativeHandle = (jlong)new AVAssetWrapper(url);
    } catch(std::exception& e) {
        // fall through, return zero
    }
    
    env->ReleaseStringUTFChars(javaURL, url);
    
    return nativeHandle;

    JNF_COCOA_EXIT(env);
}

/*
 * Class:     ch_ethz_ether_video_avfoundation_AVAsset
 * Method:    nativeDispose
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_ch_ethz_ether_video_avfoundation_AVAsset_nativeDispose
(JNIEnv * env, jobject, jlong nativeHandle) {
    JNF_COCOA_ENTER(env);
    
    delete (AVAssetWrapper*)nativeHandle;

    JNF_COCOA_EXIT(env);
}

/*
 * Class:     ch_ethz_ether_video_avfoundation_AVAsset
 * Method:    nativeRewind
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_ch_ethz_ether_video_avfoundation_AVAsset_nativeRewind
(JNIEnv * env, jobject, jlong nativeHandle) {
    JNF_COCOA_ENTER(env);

    ((AVAssetWrapper*)nativeHandle)->rewind();
    
    JNF_COCOA_EXIT(env);
}

/*
 * Class:     ch_ethz_ether_video_avfoundation_AVAsset
 * Method:    nativeGetFrame
 * Signature: (J)[B
 */
JNIEXPORT jintArray JNICALL Java_ch_ethz_ether_video_avfoundation_AVAsset_nativeGetFrame
(JNIEnv * env, jobject, jlong nativeHandle, jdouble time) {
    JNF_COCOA_ENTER(env);

    return ((AVAssetWrapper*)nativeHandle)->getFrame(env, time);
    
    JNF_COCOA_EXIT(env);
}

/*
 * Class:     ch_ethz_ether_video_avfoundation_AVAsset
 * Method:    nativeGetNextFrame
 * Signature: (J)[B
 */
JNIEXPORT jintArray JNICALL Java_ch_ethz_ether_video_avfoundation_AVAsset_nativeGetNextFrame
(JNIEnv * env, jobject, jlong nativeHandle) {
    JNF_COCOA_ENTER(env);

    return ((AVAssetWrapper*)nativeHandle)->getNextFrame(env);
    
    JNF_COCOA_EXIT(env);
}

/*
 * Class:     ch_ethz_ether_video_avfoundation_AVAsset
 * Method:    nativeGetWidth
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_ch_ethz_ether_video_avfoundation_AVAsset_nativeGetWidth
(JNIEnv * env, jobject, jlong nativeHandle) {
    JNF_COCOA_ENTER(env);

    return ((AVAssetWrapper*)nativeHandle)->getWidth();
    
    JNF_COCOA_EXIT(env);
}

/*
 * Class:     ch_ethz_ether_video_avfoundation_AVAsset
 * Method:    nativeGetHeight
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_ch_ethz_ether_video_avfoundation_AVAsset_nativeGetHeight
(JNIEnv * env, jobject, jlong nativeHandle) {
    JNF_COCOA_ENTER(env);

    return ((AVAssetWrapper*)nativeHandle)->getHeight();
    
    JNF_COCOA_EXIT(env);
}

/*
 * Class:     ch_ethz_ether_video_avfoundation_AVAsset
 * Method:    nativeGetDuration
 * Signature: (J)D
 */
JNIEXPORT jdouble JNICALL Java_ch_ethz_ether_video_avfoundation_AVAsset_nativeGetDuration
(JNIEnv * env, jobject, jlong nativeHandle) {
    JNF_COCOA_ENTER(env);

    return ((AVAssetWrapper*)nativeHandle)->getDuration();
    
    JNF_COCOA_EXIT(env);
}

/*
 * Class:     ch_ethz_ether_video_avfoundation_AVAsset
 * Method:    nativeGetFrameRate
 * Signature: (J)D
 */
JNIEXPORT jdouble JNICALL Java_ch_ethz_ether_video_avfoundation_AVAsset_nativeGetFrameRate
(JNIEnv * env, jobject, jlong nativeHandle) {
    JNF_COCOA_ENTER(env);

    return ((AVAssetWrapper*)nativeHandle)->getFrameRate();
    
    JNF_COCOA_EXIT(env);
}

/*
 * Class:     ch_ethz_ether_video_avfoundation_AVAsset
 * Method:    nativeGetFrameCount
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_ch_ethz_ether_video_avfoundation_AVAsset_nativeGetFrameCount
(JNIEnv * env, jobject, jlong nativeHandle) {
    JNF_COCOA_ENTER(env);

    return ((AVAssetWrapper*)nativeHandle)->getFrameCount();
    
    JNF_COCOA_EXIT(env);
}

