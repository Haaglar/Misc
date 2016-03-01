//Uses midifile https://github.com/craigsapp/midifile to shift pitch of a .MID
#include "MidiFile.h"
#include <string>
#include <iostream>
#include <algorithm>

int main(int argc, char* argv[]) {

    if (argc != 3)
    {
        cerr << "Usage: " << argv[0] << " file" << " semitones" << endl;
        return(1);
    }

    MidiFile midifile;
    midifile.read(argv[1]);

    if (!midifile.status()) { 
        cerr << "Error reading: " << argv[1] << endl;
        return(1);
    }

    int adjustment;
    try
    {
        adjustment = stoi(argv[2]);
    }
    catch (invalid_argument ia)
    {
        cerr << argv[2] << " Not valid input" << endl;
        cerr << "Usage: " << argv[0] << " file" << " semitones" << endl;
        return(1);
    }

    
    for (int track = 0; track < midifile.getTrackCount(); track++)
    {
        for (int eventNo = 0; eventNo < midifile[track].size(); eventNo++)
        {
            if (midifile[track][eventNo].isNote())
            {
                midifile[track][eventNo][1] += adjustment;
                if (midifile[track][eventNo][1] > 127 || midifile[track][eventNo][1] < 0)
                {
                    cerr << " Note exceeds min/max value pitch" << endl;
                    return(1);
                }
            }
        }
    }
    midifile.write(string(argv[1]) + "_adjustedpi.mid");
    cerr << "Complete" << endl;
    return 0;
}
