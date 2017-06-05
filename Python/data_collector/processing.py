import pandas as pd
import numpy as np
from sklearn.neighbors import KNeighborsRegressor
from data_filtration import filter_rssi_df, create_rssi_avg_filter
import csv
from models import CaptureSession, RSSIValue, Position
from models import database

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
        f.write('X,Y,Timestamp\n')
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
            
            while i < len(processed_rssi) and processed_rssi[i].timestamp <= end_t:
                px = start_x + delta_x * (session.rssi_values[i].timestamp - start_t) / float(delta_t)
                py = start_y + delta_y * (session.rssi_values[i].timestamp - start_t) / float(delta_t)
                data.append((
                    processed_rssi[i].beacon_uuid,
                    processed_rssi[i].beacon_major,
                    processed_rssi[i].beacon_minor,
                    processed_rssi[i].rssi, px, py,
                    processed_rssi[i].timestamp,
                    session.id
                ))
                i += 1
    return data

def export_rssi_pos(session_names=[], outname='data.csv'):
    data = build_rssi_pos(session_names)
    with open(outname, 'w') as f:
        f.write("UUID,Major,Minor,RSSI,X,Y,Timestamp,SessId\n")
        for item in data:
            f.write("{},{},{},{},{},{},{},{}\n".format(*item))

def build_rssi_map(session_names=[]):
	data = build_rssi_pos(session_names)
	df = pd.DataFrame(data, columns=['UUID','Major','Minor','RSSI','X','Y','Timestamp','SessId'])
	df = df[df.UUID == 'b9407f30f5f8466eaff925556b57fe6d']
	df = filter_rssi_df(df, filter_func=create_rssi_avg_filter(17))
	result = np.array([])
	minors = sorted(df.Minor.unique())
	xy = np.mgrid[0:15:1, 0:-8:-1].reshape(2,-1).T
	for minor in minors:
		minor_df = df[df.Minor == minor]
		train_in = minor_df[['X', 'Y']].as_matrix()
		train_out = -minor_df[['RSSI']].as_matrix().ravel()
		reg = KNeighborsRegressor(n_neighbors=3)
		reg.fit(train_in, train_out)
		pred = reg.predict(xy)
		if len(result) == 0:
			result = pred.reshape(-1, 1)
		else:
			result = np.hstack((result, pred.reshape(-1, 1)))
	
	return (xy, result)

def build_alternative_rssi_map(session_names=[]):
	data = build_rssi_pos(session_names)
	df = pd.DataFrame(data, columns=['UUID','Major','Minor','RSSI','X','Y','Timestamp','SessId'])
	df = df[df.UUID == 'b9407f30f5f8466eaff925556b57fe6d']
	sessions = df.SessId.unique()
	coords = []
	packs = []
	for session in sessions:
		sess_df = df[df.SessId == session]
		x = sess_df.X.mean()
		y = sess_df.Y.mean()
		minors = sorted(sess_df.Minor.unique())
		rssis = []
		for minor in minors:
			rssis.append(sess_df[sess_df.Minor == minor].RSSI.mean())
		coords.append((x, y))
		packs.append(rssis)
	return coords, packs




def export_rssi_map(session_names=[], outname='map.csv'):
	xy, m = build_alternative_rssi_map(session_names)
	with open(outname, 'w') as f:
		for coords, rssis in zip(xy, m):
			f.write("{} {} {} {} {} {} {}\n".format(*coords, *rssis))

