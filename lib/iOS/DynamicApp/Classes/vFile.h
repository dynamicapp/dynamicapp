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