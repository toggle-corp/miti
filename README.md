# miti-scraping

Script to pull calender data from given website(supporting limited website for now)

#### Install:
```bash
virtualenv -p python3 env # replace env with the required directory
source env/bin/activate
pip install -r requirements.txt
```

#### Usage:
```bash
python3 pullData.py 2070 2074
```

> Data are stored at dump/dump.json file(by default).

> For custom location:
```bash
python3 pullData.py 2070 2074 'dump_location'
```
  
