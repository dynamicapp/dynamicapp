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

#import <Foundation/Foundation.h>


// typically the value is equal to variable name by removing prefix 'kZXPDF'.
// e.g. kZXPDFType's value is "Type".

// dictionary keys or resource categories.
extern const char *kZXPDFType;
extern const char *kZXPDFSubtype;
extern const char *kZXPDFName;
extern const char *kZXPDFEncoding;
extern const char *kZXPDFBaseEncoding;
extern const char *kZXPDFDescendantFonts;
extern const char *kZXPDFToUnicode;
extern const char *kZXPDFBaseFont;
extern const char *kZXPDFCIDSystemInfo;
extern const char *kZXPDFFontDescriptor;

extern const char *kZXPDFRegistry;
extern const char *kZXPDFOrdering;
extern const char *kZXPDFSupplement;

extern const char *kZXPDFFont;
extern const char *kZXPDFExtGState;
extern const char *kZXPDFTK;

extern const char *kZXPDFCMapName;
extern const char *kZXPDFUseCMap;
extern const char *kZXPDFWMode;


// types
extern const char *kZXPDFCMap;


// font subtypes
extern const char *kZXPDFType0;
extern const char *kZXPDFType1;
extern const char *kZXPDFType3;
extern const char *kZXPDFTrueType;
extern const char *kZXPDFMMType1; // not supported.

extern const char *kZXPDFCIDFontType0;
extern const char *kZXPDFCIDFontType2;


// font dictionary keys
extern const char *kZXPDFDW;
extern const char *kZXPDFW;
extern const char *kZXPDFDW2;
extern const char *kZXPDFW2;
extern const char *kZXPDFFirstChar;
extern const char *kZXPDFLastChar;
extern const char *kZXPDFWidths;
extern const char *kZXPDFFontBBox;
extern const char *kZXPDFFontMatrix;
extern const char *kZXPDFCharProcs;
extern const char *kZXPDFResources;

// font descriptor dictionary keys
extern const char *kZXPDFFontFamily;
extern const char *kZXPDFFontStretch;
extern const char *kZXPDFFontWeight;
extern const char *kZXPDFFlags;
extern const char *kZXPDFItalicAngle;
extern const char *kZXPDFAscent;
extern const char *kZXPDFDescent;
extern const char *kZXPDFLeading;
extern const char *kZXPDFCapHeight;
extern const char *kZXPDFXHeight;
extern const char *kZXPDFStemV;
extern const char *kZXPDFStemH;
extern const char *kZXPDFAvgWidth;
extern const char *kZXPDFMaxWidth;
extern const char *kZXPDFFontFile;
extern const char *kZXPDFFontFile2;
extern const char *kZXPDFFontFile3;
extern const char *kZXPDFCharSet;

// font descriptior flags mask
extern const NSInteger mZXPDFFixedPitch;
extern const NSInteger mZXPDFSerif;
extern const NSInteger mZXPDFSymbolic;
extern const NSInteger mZXPDFScript;
extern const NSInteger mZXPDFNonsymbolic;
extern const NSInteger mZXPDFItalic;
extern const NSInteger mZXPDFAllCap;
extern const NSInteger mZXPDFSmallCap;
extern const NSInteger mZXPDFForceBold;
