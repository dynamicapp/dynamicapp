/*
 * Copyright (C) 2014 ZYYX, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "FileEncryptor.h"
#include "md5.h"
#include "random.h"

FileEncryptor::FileEncryptor(void)
{
}

FileEncryptor::~FileEncryptor(void)
{
    
}

bool FileEncryptor::Encrypt(const char *szFileName)
{
    bool ret = false;
    char *pNewFileName = NULL;
    unsigned char *pEncrypt = NULL;
    long size;
    
    if(!execEncryptAndDecrypt(szFileName, &pEncrypt, &size)) {
        goto EXIT;
    }
    
    if(!pEncrypt) {
        goto EXIT;
    }
    
    {
        //FileLen + ".pkg" + NULL
        long filenameSize = strlen(szFileName) + 4 + 1;
    
        pNewFileName = new char[filenameSize];
        memset(pNewFileName, NULL, filenameSize);
    
        strcpy(pNewFileName, szFileName);
        strcat(pNewFileName, ".pkg");

        printf("%s¥n", pNewFileName);
        if(!Open(pNewFileName, vFile::modeWrite | vFile::modeCreate)) {
            goto EXIT;
        }
  
        if(!Write(pEncrypt, size)) {
            goto EXIT;
        }
        
        Close();
        
        ret = true;
    }
    
EXIT:
	FREEARRAY(pEncrypt)
    FREEARRAY(pNewFileName)

    return ret;
}

bool FileEncryptor::Decrypt(const char *szFileName, unsigned char **pDecrypt, long *size)
{
    bool ret = false;
    
    if(!execEncryptAndDecrypt(szFileName, pDecrypt, &(*size))) {
        goto EXIT;
    }
 
    ret = true;

EXIT:    
    return ret;
}

bool FileEncryptor::execEncryptAndDecrypt(const char *szFileName, unsigned char **encrypted, long *size) 
{
    if(!Open(szFileName, vFile::modeRead)) {
        return false;
    }
    
    long fileLen = Length();

    if(!Read()) {
        return false;
    }
    
    unsigned char *pBuf = new unsigned char[fileLen];
    memcpy(pBuf, m_pDataBuf, fileLen);
    
    Close();
    
    char * pFileName;
    
#ifdef __WIN32__
    pFileName = (char *)strrchr(szFileName, '\\');
#else
    pFileName = strrchr(szFileName, '/');
#endif
    
    if(pFileName)
        ++pFileName;
    else
        pFileName = (char *)szFileName;

    char *p = NULL;
    if((p = strstr(pFileName, ".pkg"))) {
        *p = 0x00;
    }
    
    md5_state_t state;
	md5_byte_t digest[16];
    
    md5_init(&state);
	md5_append(&state, (const md5_byte_t *)pFileName, (int)strlen(pFileName));
    md5_finish(&state, digest);
                                                                                            
    unsigned int var[4];
    for(int i=0; i<4; i++) {
        var[i] = 0;
        for(int j=0; j<4; j++) {
            var[i] += (digest[15 - (4*i+j)] << 8*(3-j));
        }
    }
    
    unsigned int seed = 0;
    for(int i=0; i<4; i++) {
        seed += var[i];
    }
    dynamicApp_srand(seed);
    
    unsigned char *encryptArray = new unsigned char[1024];
    for(int i=0; i<1024; i++) {
        encryptArray[i] = (unsigned char)dynamicApp_rand();
    }
    
    for(int i=0; i<fileLen; i++){
        pBuf[i] ^= encryptArray[i%1024];
    }

    *encrypted = pBuf;
    *size = fileLen;
    
    FREEARRAY(encryptArray);
    return true;
}
