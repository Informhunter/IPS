import socket
from bluetooth.ble import BeaconService

minors = {
		'egopi1' : 37925,
		'egopi2' : 38181,
		'egopi3' : 38437,
}

hostname = socket.gethostname()


service = BeaconService()

service.start_advertising("b9407f30-f5f8-466e-aff9-25556b57fe6d",
		256, minors[hostname], 1, 200)
