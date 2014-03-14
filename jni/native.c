#include <jni.h>
#include <string.h>
#include "mongoose.h"
#include <android/log.h>

int server_loop = 0;

void Java_vladimir_fileserver_MainActivity_startNativeServer(JNIEnv* env, jclass clazz, jstring path) {
	const char *path_utf8 = (*env)->GetStringUTFChars(env, path, NULL);
	server_loop = 1;
	struct mg_server *server;
	server = mg_create_server(NULL, NULL);
	__android_log_print(ANDROID_LOG_VERBOSE, "HTTP", "server created");
	mg_set_option(server, "document_root", path_utf8);      // Serve current directory
	mg_set_option(server, "listening_port", "8090");  // Open port 8080
	__android_log_print(ANDROID_LOG_VERBOSE, "HTTP", "root path %s", path_utf8);
	__android_log_print(ANDROID_LOG_VERBOSE, "HTTP", "port 8090");
	__android_log_print(ANDROID_LOG_VERBOSE, "HTTP", "starting loop...");
	while (server_loop) {
		mg_poll_server(server, 500);            // Infinite loop, Ctrl-C to stop
	}
	__android_log_print(ANDROID_LOG_VERBOSE, "HTTP", "stopping...");
	mg_destroy_server(&server);
	__android_log_print(ANDROID_LOG_VERBOSE, "HTTP", "stopped");
}

void Java_vladimir_fileserver_MainActivity_stopNativeServer(JNIEnv* env, jclass clazz) {
	server_loop = 0;
}
