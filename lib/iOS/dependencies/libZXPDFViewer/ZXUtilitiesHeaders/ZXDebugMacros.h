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
