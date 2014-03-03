//
//  vFile.cpp
//  ResourceEncryptor
//
//  Created by ZYYX on 12/02/01.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#include "vFile.h"

vFile::vFile(void) 
{
    m_fp = NULL;
    m_pDataBuf = NULL;
    m_iFlags = -1;
}

vFile::~vFile(void) 
{
    Close();
}

bool vFile::Open(const char *szFileName, unsigned int nOpenFlags) 
{
    Close();
    
    char mode[4 + 1];
    memset(mode, NULL, sizeof(mode));
    
    if(nOpenFlags & modeRead) {
        if(nOpenFlags & modeCreate) {
            return false;
        } else if(nOpenFlags & modeWrite) {
            if(nOpenFlags & modeAppend) {
                strcat(mode, "a+");
            } else {
                strcat(mode, "w+");
            }
        } else if(nOpenFlags & modeAppend) {
            strcat(mode, "a+");
        } else {
            strcat(mode, "r");
        }
    }
    
    if((nOpenFlags & modeWrite) && !(nOpenFlags & modeRead)) {
        if(nOpenFlags & modeCreate) {
            strcat(mode, "a");
        } else if(nOpenFlags & modeAppend) {
            strcat(mode, "a+");
        } else {
            strcat(mode, "w");
        }
    }

    if((nOpenFlags & modeAppend) && !(nOpenFlags & modeRead) && !(nOpenFlags & modeWrite)) {
        strcat(mode, "a");
    }

    if((nOpenFlags & modeCreate) && !(nOpenFlags & modeRead) && !(nOpenFlags & modeWrite) && !(nOpenFlags & modeAppend)) {
        strcat(mode, "w");
    }
    
    strcat(mode, "b");
    if((m_fp = fopen(szFileName, mode)) == NULL) {
        return false;
    }
    
    m_iFlags = nOpenFlags;
    return true;
}

void vFile::Close(void) 
{
    F_FREE(m_fp);
    FREEARRAY(m_pDataBuf);
}

bool vFile::Write(const void* data, long len) 
{
    if(!m_fp) {
        return false;
    }
    
    if(!(m_iFlags & modeWrite)) {
        return false;
    }

    if(fwrite(data, len, 1, m_fp) < 1) {
        return false;
    }

    return true;
}

bool vFile::Read(void)
{
    if(!m_fp) {
        return false;
    }
    
    if(!(m_iFlags & modeRead)) {
        return false;
    }
    
    long size = Length();
    
    FREEARRAY(m_pDataBuf)
    m_pDataBuf = new unsigned char[size + 1];
    memset(m_pDataBuf, NULL, size + 1);
    
    Seek(0, SEEK_SET);
    size_t readSize = fread(m_pDataBuf, size, 1, m_fp);
    if(readSize < 1) {
        return false;
    }

    return true;
}

int vFile::Seek(long offset, int origin) 
{
    return fseek(m_fp, offset, origin);
}

long vFile::Length(void)
{
    Seek(0, SEEK_END);
    return ftell(m_fp);
}