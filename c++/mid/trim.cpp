//Uses midifile https://github.com/craigsapp/midifile to trim rests from the start of a .MID
//e.g.
// d d
//  d  a
//Becomes
//d d
// d  a
//Devd in window, untested on other sys.
#include "MidiFile.h"
#include <string>
#include <iostream>
#include <algorithm>
using namespace std;

int main(int argc, char* argv[]) {

    if (argc != 2)
    {
        cerr << "Usage: " << argv[0] << " file" << endl;
        return(1);
    }

    MidiFile midifile;
    midifile.read(argv[1]);

    if (!midifile.status()) { //
        cerr << "Error reading: " << argv[1] << endl;
        return(1);
    }
    int iTicks = 999999;
    //Find
    for (int track = 0; track < midifile.getTrackCount(); track++)
    {
        for (int eventNo = 0; eventNo < midifile[track].size(); eventNo++)
        {
            if (midifile[track][eventNo].isNoteOn()) //Find first note on for track
            {
                iTicks = min(midifile[track][eventNo].tick, iTicks);
                break;
            }
        }
    }
    if (iTicks > 0 && iTicks != 999999)
    {
        //iterate over it all
        for (int track = 0; track < midifile.getTrackCount(); track++)
        {
            for (int eventNo = 0; eventNo < midifile[track].size(); eventNo++)
            {
                if (midifile[track][eventNo].tick >= iTicks)
                    midifile[track][eventNo].tick -= iTicks;
                else
                    midifile[track][eventNo].tick = 0;
            }
        }
        midifile.write(string(argv[1]) + "_trim.mid");
        cout << "Complete" << endl;
        return(0);
    }
    cout << "File can't be rest trimed." << endl;
    return(1);
}
