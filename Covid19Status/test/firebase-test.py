import pyrebase


config = {
    "apiKey": "AIzaSyCWFuVC7ajAdFakF9JOLaZJZ7rlIDQKFGQ",
    "authDomain": "covid19status-d56e4.firebaseapp.com",
    "databaseURL": "https://covid19status-d56e4.firebaseio.com",
    "storageBucket": "covid19status-d56e4.appspot.com",
    "serviceAccount": "./covid19status-d56e4-firebase-adminsdk-2u99p-54629e5824.json"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()


def set_status(key, data):
    db.child("status").child(key.lower()).set(data)


def set_state_status(state_name, key, data):
    db.child(state_name.lower()).child(key.lower()).set(data)


set_status("confirmed", 1040)
set_status("ACTIVE", 929)
set_status("RECOVERED", 85)
set_status("DECEASED", 85)

set_state_status("Maharashtra", "confirmed", 189)
set_state_status("Maharashtra", "ACTIVE", 156)
set_state_status("Maharashtra", "RECOVERED", 25)
set_state_status("Maharashtra", "DECEASED", 6)

s = db.child("status").child("confirmed").get()
print(s.val())

# for s in status.each():
#     print(s.key())
