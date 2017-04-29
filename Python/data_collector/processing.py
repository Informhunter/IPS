import numpy as np
import csv
from peewee import SqliteDatabase
from models import CaptureSession, RSSIValue, Position
from models import database, create_tables

def save_data_to_db(rssi_file, position_file, session_file):
    rssi_data = list(csv.reader(open(rssi_file, 'r')))
    position_data = list(csv.reader(open(position_file, 'r')))

    with open(session_file, 'r') as f:
        session_name, session_date = [x.rstrip() for x in f]

    with database.atomic():
        sess = CaptureSession()
        sess.name = session_name
        sess.date = session_date
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

def export_data_from_db(session_name):
    session = (CaptureSession.select()
               .where(CaptureSession.name == session_name)).get()

    with open(session_name + '_rssi.txt', 'w') as f:
        f.write('UUID,Major,Minor,RSSI,Timestamp\n')
        for rv in session.rssi_values:
            f.write("{},{},{},{},{}\n".format(
                rv.beacon_uuid,
                rv.beacon_major,
                rv.beacon_minor,
                rv.rssi,
                rv.timestamp
            ))

    with open(session_name + '_position.txt', 'w') as f:
        f.write('PosX,PosY,Timestamp\n')
        for pos in session.positions:
            f.write("{},{},{}\n".format(
                pos.x,
                pos.y,
                pos.timestamp
            ))


def apply_smoothing_to_rssi_values(data):
    return data

def build_rssi_pos(session_names=[]):
    sessions = (CaptureSession.select()
                .where(CaptureSession.name << session_names))

    data = []
    for session in sessions:
        processed_rssi = apply_smoothing_to_rssi_values(session.rssi_values)
        i = 0
        for current in range(1, len(session.positions)):
            start_t = session.positions[current - 1].timestamp
            end_t = session.positions[current].timestamp
            delta_t = end_t - start_t
            
            start_x = session.positions[current - 1].x
            end_x = session.positions[current].x
            delta_x = end_x - start_x
            
            start_y = session.positions[current - 1].y
            end_y = session.positions[current].y
            delta_y = end_y - start_y
            
            while processed_rssi[i].timestamp <= end_t:
                px = start_x + delta_x * (session.rssi_values[i].timestamp - start_t) / float(delta_t)
                py = start_y + delta_y * (session.rssi_values[i].timestamp - start_t) / float(delta_t)
                data.append((
                    processed_rssi[i].beacon_uuid,
                    processed_rssi[i].beacon_major,
                    processed_rssi[i].beacon_minor,
                    processed_rssi[i].rssi, px, py))
                i += 1
