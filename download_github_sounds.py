import urllib.request
import json
import os
import time

out_dir = "app/src/main/res/raw"
os.makedirs(out_dir, exist_ok=True)

queries = {
    "rain.mp3": "rain.mp3",
    "forest.mp3": "forest.mp3",
    "cafe.mp3": "cafe.mp3",
    "white_noise.mp3": "white_noise.mp3"
}

def search_and_download(filename, query):
    api_url = f"https://api.github.com/search/code?q=filename:{query}+extension:mp3+size:>500000"
    try:
        req = urllib.request.Request(api_url, headers={'User-Agent': 'Python/3.10'})
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read())
            items = data.get('items', [])
            if not items:
                print(f"No results for {query} with size > 500KB. Trying without size limit...")
                api_url = f"https://api.github.com/search/code?q=filename:{query}+extension:mp3"
                req = urllib.request.Request(api_url, headers={'User-Agent': 'Python/3.10'})
                with urllib.request.urlopen(req) as response2:
                    data = json.loads(response2.read())
                    items = data.get('items', [])
            
            if items:
                # Get the first item
                item = items[0]
                repo = item['repository']['full_name']
                path = item['path']
                raw_url = f"https://raw.githubusercontent.com/{repo}/master/{path}"
                
                # Some default branch might be main, so try both
                raw_urls = [
                    f"https://raw.githubusercontent.com/{repo}/main/{path}",
                    f"https://raw.githubusercontent.com/{repo}/master/{path}"
                ]
                
                downloaded = false
                for r_url in raw_urls:
                    try:
                        print(f"Trying to download {filename} from {r_url}...")
                        req_dl = urllib.request.Request(r_url, headers={'User-Agent': 'Python/3.10'})
                        with urllib.request.urlopen(req_dl) as dl_resp, open(os.path.join(out_dir, filename), 'wb') as f:
                            f.write(dl_resp.read())
                        print(f"Success: {filename}")
                        downloaded = True
                        break
                    except Exception as e:
                        pass
                
                if not downloaded:
                    print(f"Failed to download {filename} from raw urls")
            else:
                print(f"No files found for {query}")
                
    except Exception as e:
        print(f"API Error for {query}: {e}")

for fname, q in queries.items():
    search_and_download(fname, q)
    time.sleep(3) # avoid rate limit

# cleanup old wav files
for old in ["rain.wav", "forest.wav", "cafe.wav", "white_noise.wav"]:
    p = os.path.join(out_dir, old)
    if os.path.exists(p):
        os.remove(p)
