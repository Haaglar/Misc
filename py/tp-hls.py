#  Usage: tp-hls.py URL
#
import sys, os, re, argparse, urllib2, urllib, urlparse, subprocess

# Constants
PUB_ID = "&pubId=2199827728001" # ID taken from any m3u8
BC_URL = "http://c.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId=" #url taken from and m3u8
def get_video_id(url):
  data = urllib2.urlopen(url).read()
  #Find the video id
  videoid = re.search("data-video-id=[\"']([^'\"]+)",data).group(1)
  return (videoid)
  
  
def get_highest_quality(url):
  m3u8file = urllib2.urlopen(url).read()
  split = m3u8file.splitlines()
  index = 0
  row = 0
  bandwidth = 0
  for str in split:
    #We want the row after the current as it has the url so increment first
    index+=1
    bw=re.search("BANDWIDTH=([0-9]+)",str)
    if bw and int(bw.group(1)) > bandwidth:
      bandwidth = int(bw.group(1))
      row = index
  return split[row]
  
def main():
  argparser = argparse.ArgumentParser()
  argparser.add_argument('URL', help='The URL of the video')
  args = argparser.parse_args()
  
  #Get the video id from the url supplied
  vid = get_video_id(args.URL)
  #Get the master URL
  m3u8url = BC_URL + vid + PUB_ID
  #Get the highest quality URL
  finalurl = get_highest_quality(m3u8url)
  print finalurl
  return 0

if __name__ == "__main__":
  sys.exit(main())
