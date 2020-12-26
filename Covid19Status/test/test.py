import requests

state_data_url = "https://api.covid19india.org/data.json"
district_data_url = "https://api.covid19india.org/state_district_wise.json"
state_data_response = requests.get(state_data_url)
district_data_response = requests.get(district_data_url)
s_data = state_data_response.json()
d_data = district_data_response.json()
# print(d_data["Kerala"])
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
        'increase_deaths': state_data['delta']['deaths'],
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

print(len(stateData))
