LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := lbr
LOCAL_SRC_FILES := native.cpp
include $(BUILD_SHARED_LIBRARY)