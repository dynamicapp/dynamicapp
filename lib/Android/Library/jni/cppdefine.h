//
//  define.h
//  ResourceEncryptor
//
//  Created by takegawa on 12/02/02.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#ifndef DynamicApp_cppdefine_h
#define DynamicApp_cppdefine_h

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#ifndef __WIN32__
#include <dirent.h>
#endif // __WIN32__
//#include <string>

#define FREE(p)         if(p) { delete p; p = NULL; }
#define FREEARRAY(p)    if(p) { delete[] p; p = NULL; }

#define M_FREE(p)       if(p) { free(p); p = NULL; }
#define F_FREE(p)       if(p) { fclose(p); p = NULL; }


#endif
