//
//  random.c
//  DynamicApp
//
//  Created by Alliance Software Inc on 7/9/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include "random.h"

static unsigned long next = 1;

int dynamicApp_rand(void) {
    next = next * 1103515245 + 12345;
    return((unsigned)(next/65536) % 32768);
}

void dynamicApp_srand(unsigned seed) {
    next = seed;
}
