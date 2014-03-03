//
//  vFile.h
//  ResourceEncryptor
//
//  Created by ZYYX on 12/02/01.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#ifndef DynamicApp_vFile_h
#define DynamicApp_vFile_h

#include "cppdefine.h"

class vFile 
{
public:
    vFile(void);
    virtual ~vFile(void);
    
    enum {
        modeRead = 0x01,
        modeWrite = 0x02,
        modeAppend = 0x04,
        modeCreate = 0x08,
    } ;
    
    virtual bool Open(const char *szFileName, unsigned int nOpenFlags);
    virtual void Close(void);
    virtual bool Write(const void* data, long datalen);
    virtual bool Read(void);
    int Seek(long offset, int origin);
    long Length(void);
    

    
protected:
    FILE *m_fp;
    unsigned char *m_pDataBuf;

private:
    int m_iFlags;
};

#endif