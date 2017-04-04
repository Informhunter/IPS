import numpy as np
from peewee import SqliteDatabase
from models import CaptureSession, RSSIValue, Position
from models import database_proxy

def save_capture_data(rssi_file, position_file):
    rssi_data = np.loadtxt(rssi_file, delimiter=',', skiprows=1)
    position_data = np.loadtxt(position_file, delimiter=',', skiprows=1)


    with database_proxy.atomic():
        sess = CaptureSession()
        for row in rssi_data:



database_proxy.initialize(SqliteDatabase('data.db'))
save_capture_data('rssi_data.csv', 'position_data.csv')