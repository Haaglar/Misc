/*
p5sbr2gbs 0.2 (by haaglar)
Searchs a file for single music data created using paragon5 (Game Boy) tracker (by Stéphane Hockenhull) compiles and creates a gbs of it
To be used on the single bank player roms
Really just searches for a pattern takes some data then slaps on some stuff before it into a new file
Lazy mod of my carillion one
Worked on the demo songs and a couple of files that had lying around

Update Log:
0.2: Gbs actually works now on things other than VLC, removal of excess data to gbs
0.1: Initial Release.
*/

/* Size of Single-Bank ROM (maybe could use 12KB instead, to lazy to check) */
#define BLOCK_SIZE 16384
//For visual studio users
//#define _CRT_SECURE_NO_WARNINGS 
#include <stdio.h>
#include <string.h>
#include <conio.h>
#include <stdlib.h>
#include <stddef.h>


void *memmem(const void *l, size_t l_len, const void *s, size_t s_len)
{
	register char *cur, *last;
	const char *cl = (const char *)l;
	const char *cs = (const char *)s;

	/* we need something to compare */
	if (l_len == 0 || s_len == 0)
		return NULL;

	/* "s" must be smaller or equal to "l" */
	if (l_len < s_len)
		return NULL;

	/* the last position where its possible to find "s" in "l" */
	last = (char *)cl + l_len - s_len;

	for (cur = (char *)cl; cur <= last; cur++)
		if (cur[0] == cs[0] && memcmp(cur, cs, s_len) == 0)
			return cur;

	return NULL;
}

int main (int argc, char *argv[])
{
	if ( argc != 2 )
	{
		printf("Supply a damn file next time\n");
		return 2;
	}
	else
	{
		int Length;
		FILE *file = fopen(argv[1], "rb" );
		if ( file == 0 )
		{
			printf( "Could not open file\n" );
			return 1;
		}
		else
		{
			unsigned int gbs = 0x534247; /*Gbs header*/
			unsigned int track = 0x010101; /* version default and number of tracks*/
			unsigned int init = 0x3ff0; /*Gbs init*/
			unsigned int load = 0x3ff0; /*Gbs load*/
			unsigned int play = 0x4003; /*Gbs play*/
			unsigned int stackpointer = 0x8000cffe; /*Stackpointer and timer controls*/
			char title[101]={0}; /* Title max 32 bytes*/
			char author[101]={0}; /* author max 32 bytes*/
			char copyright[101] ={0}; /* copyright max 32 bytes*/
			unsigned char *block; /*data to be copied from orignal file*/
			unsigned char needle[6] = {0xC3,0x1B,0x40,0xC3,0x79,0x41 }; /* Search for the header*/
			unsigned char RST_VECTORS[16] = { 0xCD, 0x00, 0x40, 0xC3, 0x06, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }; /*Call 4000; jp 4006; nop *10 , Thanks Kingshriek*/

			void *local;
			fseek(file, 0, SEEK_END);
			Length = ftell(file);
			rewind(file);
			block = (unsigned char *)malloc(sizeof(unsigned char)*Length);
			fread(block,1,Length,file);
			local = memmem(block,Length,needle,6);
			if (local)
			{
				FILE *toCreate = fopen("tmp.gbs", "wb");
				printf("Title info (max 32 characters): ");
				fgets(title,101,stdin);
				printf("Author info (max 32 characters): ");
				fgets(author,101,stdin);
				printf("Copyright info (max 32 characters): ");
				fgets(copyright,101,stdin);
				/*Write the GBS FILE SPECIFICATION */
				fwrite(&gbs,1,3,toCreate);
				fwrite(&track,1,3,toCreate);
				fwrite(&init,1,2,toCreate);
				fwrite(&load,1,2,toCreate);
				fwrite(&play,1,2,toCreate);
				fwrite(&stackpointer,1,4,toCreate);
				fwrite(title,1,32,toCreate);
				fwrite(author,1,32,toCreate);
				fwrite(copyright,1,32,toCreate);
				fwrite(RST_VECTORS, 1, 16, toCreate);
				if(block == local) /* File is just music data, hopefully.........*/
				{
					fwrite(local,1, Length,toCreate);
				}
				else
				{
					fwrite(local,1, BLOCK_SIZE,toCreate); 
				}
				fclose(toCreate);
				/*fwrite(block,1,Length,toCreate);*/
			}
			else
			{
				printf("GB music data not found");
			}
			free(block);
			fclose(file);
		}
	}
	return 0;
}
