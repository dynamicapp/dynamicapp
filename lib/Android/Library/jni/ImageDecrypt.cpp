#include "FileEncryptor.h"
#include <ImageDecrypt.h>

JNIEXPORT jbyteArray JNICALL Java_jp_zyyx_dynamicapp_plugins_ImageDecrypt_decrypt
  (JNIEnv *env, jobject obj, jstring src)
{
	const char *filename = env->GetStringUTFChars(src, 0);
	unsigned char *imgData = NULL;
	long size = 0;
	bool isDecrypted = false;
	
	FileEncryptor *fileEncryptor = new FileEncryptor();
	isDecrypted = fileEncryptor->Decrypt(filename, &imgData, &size);
	delete fileEncryptor;
	
	jbyteArray imgBytes = NULL;
	if(isDecrypted) {
		imgBytes = env->NewByteArray(size);
		
		jboolean isCopy;
		void *data = env->GetPrimitiveArrayCritical(imgBytes, &isCopy);
		memcpy(data, imgData, size);
		
		env->ReleasePrimitiveArrayCritical(imgBytes, data, isCopy);
	}

	env->ReleaseStringUTFChars(src, filename);

    return imgBytes;
}