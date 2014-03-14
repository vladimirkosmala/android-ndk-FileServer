

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := ndkmain
LOCAL_SRC_FILES := native.c mongoose.c

# for logging
LOCAL_LDLIBS    += -llog
# for native windows
LOCAL_LDLIBS    += -landroid

LOCAL_CFLAGS    += -UNDEBUG
LOCAL_CFLAGS    += -std=c99 -O2 -W -Wall -pthread -pipe $(LUA_FLAGS) $(COPT)

include $(BUILD_SHARED_LIBRARY)
