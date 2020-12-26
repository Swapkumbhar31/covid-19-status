import pyrebase
import pytz
import requests
tz = pytz.timezone('Asia/Kolkata')
store_data = False
config = {
    "apiKey": "AIzaSyCWFuVC7ajAdFakF9JOLaZJZ7rlIDQKFGQ",
    "authDomain": "covid19status-d56e4.firebaseapp.com",
    "databaseURL": "https://covid19status-d56e4.firebaseio.com",
    "storageBucket": "covid19status-d56e4.appspot.com",
    "serviceAccount": "./covid19status-d56e4-firebase-adminsdk-2u99p-54629e5824.json"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()

state_data_url = "https://api.covid19india.org/data.json"
district_data_url = "https://api.covid19india.org/state_district_wise.json"
state_data_response = requests.get(state_data_url)
district_data_response = requests.get(district_data_url)
s_data = state_data_response.json()
d_data = district_data_response.json()

states = []
stateData = {}


for state_data in s_data['statewise']:
    obj = {
        'name': state_data['state'],
        'confirmed': state_data['confirmed'],
        'active': state_data['active'],
        'recovered': state_data['recovered'],
        'deceased': state_data['deaths'],
        'increase_confirmed': state_data['delta']['confirmed'],
        'increase_active': state_data['delta']['active'],
        'increase_deceased': state_data['delta']['deaths'],
        'increase_recovered': state_data['delta']['recovered'],
    }
    state_name = state_data['state'].lower().replace(".", "")
    if state_name != 'total':
        states.append(state_name)
    districtData = {}
    if state_data['state'] in d_data:
        temp = d_data[state_data['state']]['districtData']
        for d in temp:
            districtData[d.lower().replace(".", "")] = {
                'name': d,
                'increase': temp[d]['delta']['confirmed'],
                'confirmed': temp[d]['confirmed']
            }
    obj['data'] = districtData
    stateData[state_name] = obj

stateData['total']['increase_confirmed'] = int(s_data['key_values'][0]['confirmeddelta'])
stateData['total']['increase_active'] = int(s_data['key_values'][0]['statesdelta'])
stateData['total']['increase_deceased'] = int(s_data['key_values'][0]['deceaseddelta'])
stateData['total']['increase_recovered'] = int(s_data['key_values'][0]['recovereddelta'])
states.append('total')
if store_data:
    db.child("state").remove()
    db.child("data").remove()
    db.child('updated_at').set(s_data['key_values'][0]['lastupdatedtime'])
    db.child("data").set(stateData)
    db.child("state").set(states)


print(stateData['total'])

