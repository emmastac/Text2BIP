#include <stdio.h>

long bool_sizeof(const bool& q){
	return 1;
}
void bool_unmarshalling(bool* t, const char* c){
	if(c[0]==1)
		*t=true;
	else
		*t=false;
}
void bool_marshalling(char* c, const bool* q){
	if(q)
		c[0] = '1';
	else
		c[0] = '0';
}





#define PRINT_STATE 1
int OUTPUT_MODE = PRINT_STATE;

#define VERSION_NUM 1
int VAR_UPDATE_MODE = VERSION_NUM;

void printState(int c, char* id,  char* start,  char* end,  char* port){
	if(OUTPUT_MODE==PRINT_STATE){
		printf("[%d,%s,%s,%s,%s]", c++, id, start, end, port);
	}
}

void updateVar(int& to, int& from...){
	if(VAR_UPDATE_MODE==VERSION_NUM){
		to++; 
	}else{

	}
}

