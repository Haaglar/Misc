// Uses midifile https://github.com/craigsapp/midifile
// Adjusts note values and tempo by a multiple of the user input
// For example if the input was 2
// Tempo, note lengths and start times will double
// Devd in VS, untested on other sys.
#include "MidiFile.h"
#include <string>
#include <iostream>

using namespace std;
  
int main(int argc, char * argv[])
{
    if (argc != 3)
    {
        cerr << "Usage: " << argv[0] << " file multiplier" << endl;
        return(1);
    }
    MidiFile midi;
    midi.read(argv[1]);
    
    if (!midi.status()) { //
        cerr << "Error reading: " << argv[1] << endl;
        return(1);
    }
    float adjustment;
    try
    {
        adjustment = stof(argv[2]);
    }
    catch (invalid_argument ia)
    {
        cerr << argv[2] << " Not valid input" << endl;
        cerr << "Usage: " << argv[0] << " file multiplier" << endl;
        return(1);
    }

    if (adjustment == 0)
    {
        cerr << "Usage: " << argv[0] << " file" << endl;
        return(1);
    }
    //Iterate doubling ticks and Tempo on tempo events
    for (int track = 0; track < midi.getTrackCount(); track++)
    {
        for (int eventNo = 0; eventNo < midi[track].size(); eventNo++)
        {
            midi[track][eventNo].tick = (int)(midi[track][eventNo].tick * adjustment);
            if (midi[track][eventNo].isTempo())
            {
                midi[track][eventNo].setTempo(midi[track][eventNo].getTempoBPM() * adjustment);
            }
        }
    }
    midi.write(string(argv[1]) + "_adjustedmf.mid");
    cerr << "Complete" << endl;
    return 0;
}
