import urllib.request
import urllib.parse
import json
import os

out_dir = "app/src/main/res/raw"
os.makedirs(out_dir, exist_ok=True)

# Wikimedia commons search API
def search_commons(query):
    query_enc = urllib.parse.quote(query)
    url = f"https://commons.wikimedia.org/w/api.php?action=query&list=search&srsearch={query_enc}+filetype:audio&srnamespace=6&format=json"
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    with urllib.request.urlopen(req) as resp:
        data = json.loads(resp.read())
        for item in data['query']['search']:
            title = item['title'].replace(" ", "_")
            if title.lower().endswith(".wav"):
                return title
    return None

def download_commons(title, filename):
    url = f"https://commons.wikimedia.org/w/api.php?action=query&titles={title}&prop=imageinfo&iiprop=url&format=json"
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    with urllib.request.urlopen(req) as resp:
        data = json.loads(resp.read())
        pages = data['query']['pages']
        for page_id in pages:
            file_url = pages[page_id]['imageinfo'][0]['url']
            print(f"Downloading {file_url}...")
            dl_req = urllib.request.Request(file_url, headers={'User-Agent': 'Mozilla/5.0'})
            with urllib.request.urlopen(dl_req) as dl_resp, open(os.path.join(out_dir, filename), 'wb') as f:
                f.write(dl_resp.read())
            print(f"Success: {filename}")
            return True
    return False

sounds = {
    "rain.wav": "rain",
    "forest.wav": "forest birds",
    "cafe.wav": "restaurant cafe",
    "white_noise.wav": "white noise"
}

for fname, q in sounds.items():
    print(f"Searching for {q} WAV...")
    title = search_commons(q)
    if title:
        print(f"Found {title}, downloading...")
        download_commons(title, fname)
    else:
        print(f"No WAV found for {q}")

# Delete old .ogg
for old in ["rain.ogg", "forest.ogg", "cafe.ogg", "white_noise.ogg"]:
    p = os.path.join(out_dir, old)
    if os.path.exists(p):
        os.remove(p)
