import urllib.request
import json
import time

def get_url(query):
    url = f'https://api.github.com/search/code?q={query.replace(" ", "+")}+extension:mp3'
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    try:
        with urllib.request.urlopen(req) as r:
            data = json.loads(r.read())
            if data['items']:
                for it in data['items']:
                    raw = f"https://raw.githubusercontent.com/{it['repository']['full_name']}/master/{it['path']}"
                    print(raw)
    except Exception as e:
        print(f"Error for {query}: {e}")

get_url('coffee shop')
time.sleep(2)
get_url('cafe ambient')
time.sleep(2)
get_url('white noise')
