import csv
from peewee import SqliteDatabase
from models import CaptureSession, RSSIValue, Position
from models import database, create_tables

def save_data_to_db(rssi_file, position_file):
    rssi_data = list(csv.reader(open(rssi_file, 'r')))
    position_data = list(csv.reader(open(position_file, 'r')))

    with database.atomic():
        sess = CaptureSession()
        sess.save()
        for row in rssi_data[1:]:
            rssi = RSSIValue()
            rssi.beacon_uuid = row[0]
            rssi.beacon_major = int(row[1])
            rssi.beacon_minor = int(row[2])
            rssi.rssi = int(row[3])
            rssi.timestamp = int(row[4])
            rssi.capture_session = sess
            rssi.save()

        for row in position_data[1:]:
            pos = Position()
            pos.x = float(row[0])
            pos.y = float(row[1])
            pos.timestamp = int(row[2])
            pos.capture_session = sess
            pos.save()
