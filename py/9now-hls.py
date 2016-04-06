#  Usage: 9now-hls.py URL
#
#  Grabs the HLS m3u8 URL from 9Now using a method similar to the mobile request 
#  To download the HLS stream using ffmpeg, use the following command:
#  ffmpeg -i <URL> -acodec copy -vcodec copy -bsf:a aac_adtstoasc <OUTPUT_FILE>
#
import sys, os, re, argparse, urllib2, urllib, urlparse, json

# Constants
BC_URL_1 = "http://api.brightcove.com/services/library?media_delivery=http&reference_id="
BC_URL_2 = "&command=find_video_by_reference_id&token=7jHYJN84oHHRRin6N6JpuDmm3ghgxP4o3GGXsatxe5aDKZ4MGOztLw..&video_fields=accountId%2CshortDescription%2CiOSRenditions%2CWVMRenditions%2CHLSURL%2CvideoFullLength" #token found via mobile request

def get_ref_id(url):
  data = urllib2.urlopen(url).read()
  #Find the video id
  refid = re.search('referenceId":"([^"]*)"',data).group(1)
  return (refid)
  
  
def make_brightcove_request(url):
  jsond = json.loads(urllib2.urlopen(url).read())
  renditions = sorted(jsond["IOSRenditions"], key=lambda rendition: rendition['encodingRate']) #clearly not taken from elsewhere :^)
  return renditions[0]["url"], renditions[0]["displayName"]
  
def main():
  argparser = argparse.ArgumentParser()
  argparser.add_argument('URL', help='The URL of the video')
  args = argparser.parse_args()
  
  #Get the video id from the url supplied
  vid = get_ref_id(args.URL)
  bcurl = BC_URL_1 + vid + BC_URL_2
  #Get the highest quality URL
  m3u8url, name = make_brightcove_request(bcurl)
  print m3u8url
  return 0

if __name__ == "__main__":
  sys.exit(main())
