/*
 *  DebugMacros.h
 *  ZyyxLibraries
 *
 *  Created by gotow on 10/03/01.
 *  Copyright 2010 ZYYX Inc. All rights reserved.
 *
 */


#define NOP

#define DPRINT(format, ...) NOP
#define DPRINT_IF(cond, format, ...) NOP
#define INVOKE_LOG() NOP
#define OBJECT_LOG(variable) NOP
#define CGRECT_LOG(variable) NOP


#ifdef DEBUG

#undef DPRINT
#undef DPRINT_IF
#undef INVOKE_LOG
#undef OBJECT_LOG
#undef CGRECT_LOG

#define DPRINT(format, ...) do { NSLog(format, ##__VA_ARGS__); } while (0)

#define DPRINT_IF(cond, format, ...) if ((cond)) { NSLog(format, ##__VA_ARGS__); }

#define INVOKE_LOG() \
NSLog(@"invoking -[%@ %@]", [[self class] description], NSStringFromSelector(_cmd))

#define OBJECT_LOG(variable) \
do { NSLog(@"%s %@", #variable , (variable)); } while (0)

#define CGRECT_LOG(variable) \
do { NSLog(@"%s %@", #variable , [NSValue valueWithCGRect:(variable)]); } while (0)

#endif
