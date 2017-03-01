from bs4 import BeautifulSoup
import configparser
import argparse
import requests
import json


config = configparser.ConfigParser()
config.read('conf/conf.ini')
web_url = config['website']['url']


headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1)'
           'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95'
           'Safari/537.36'}


def map_value(num):
    number = [b'\xc3\xa0\xc2\xa5\xc2\xa6',  # 0
              b'\xc3\xa0\xc2\xa5\xc2\xa7',  # 1
              b'\xc3\xa0\xc2\xa5\xc2\xa8',  # 2
              b'\xc3\xa0\xc2\xa5\xc2\xa9',  # 3
              b'\xc3\xa0\xc2\xa5\xc2\xaa',  # 4
              b'\xc3\xa0\xc2\xa5\xc2\xab',  # 5
              b'\xc3\xa0\xc2\xa5\xc2\xac',  # 6
              b'\xc3\xa0\xc2\xa5\xc2\xad',  # 7
              b'\xc3\xa0\xc2\xa5\xc2\xae',  # 8
              b'\xc3\xa0\xc2\xa5\xc2\xaf']  # 9
    val = []
    # 3 -> 03
    val.append('0') if len(num) == 3 else None
    for i in range(0, len(num), 3):
        val.append(str(number.index(num[i:i+3].encode('utf-8'))))
    return ''.join(val)


def pull_data_from_month(year, month):
    """
    Year and month integer
    """
    html_page = requests.get(web_url+str(year)+'/'+str(month)+'/',
                             headers=headers)

    if html_page.status_code is not 200:
        print('Internet Error!!')
        return

    soup = BeautifulSoup(html_page.text, 'html.parser')
    calender = soup.find('div', class_="calendar")

    if not calender:
        print('Calender<div> Not Found!!')
        return

    dates = calender.find('ul', class_="dates")
    data = []

    # 2071/1 -> 2071/01
    from_date = '/'.join(
            [str(year), ''.join(['0', str(month)])
             if len(str(month)) == 1 else str(month)])

    for day in dates.find_all('li'):

        # Skip disable (i.e not belonging to current month'
        if 'disable' in day.get('class'):
            continue

        event = day.find('span', class_='event').text
        event = event if event != '--' else None
        holiday = True if 'holiday' in day.get('class') else False

        data.append({
            'date': from_date+'/'+map_value(day.find('span',
                                                     class_='nep').text),
            'extra': {
                'event': event,
                'holiday': holiday,
                },
            'tithi': day.find('span', class_='tithi').text,
            })

    return data


def pull_data(init_year, end_year, dump='json.dumps'):
    data = []
    for year in range(init_year, end_year+1):
        print('*'*30)
        print('Pulling For Year:', year)
        print('*'*30)
        for month in range(1, 12+1):
            print('\tPulling For Month:', month)
            data.extend(pull_data_from_month(year, month))
            print('\t'+'-'*25, 'Done', '-'*25)

    with open(dump, 'w') as file:
        file.write(json.dumps({'init_year': init_year,
                               'end_year': end_year,
                               'data': data}))
        print('Data Dumped to:', dump)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Pull Tithi Data')
    parser.add_argument('init_year', metavar='i', type=int,
                        help='Init Year')
    parser.add_argument('end_year', metavar='e', type=int,
                        help='End Month')
    parser.add_argument('--dump', metavar='-d', type=str,
                        help='Dump file location',
                        default='dump/json.dumps')
    args = parser.parse_args()

    init_year = args.init_year
    end_year = args.end_year
    dump = args.dump

    pull_data(init_year, end_year, dump)