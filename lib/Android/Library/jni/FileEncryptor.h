//
//  FileEncryptor.h
//  ResourceEncryptor
//
//  Created by takegawa on 12/02/02.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#ifndef DynamicApp_FileEncryptor_h
#define DynamicApp_FileEncryptor_h

#include "cppdefine.h"
#include "vFile.h"
#include "md5.h"

class FileEncryptor : public vFile 
{    
public:
    FileEncryptor(void);
    //virtual ~FileEncryptor(void);
    ~FileEncryptor(void);
    bool Encrypt(const char *szFileName);
    bool Decrypt(const char *szFileName, unsigned char **pDecrypt, long *size);
    
private:
    bool execEncryptAndDecrypt(const char *szFileName, unsigned char **encrypted, long *size);
};

#endif