import pandas as pd
from statsmodels.nonparametric.smoothers_lowess import lowess
from scipy.signal import medfilt

#Filter RSSI values with lowess filter

def rssi_lowess_filter(df):
    smoothed = lowess(df.RSSI, df.Timestamp)
    new_df = df.copy()
    new_df.RSSI = smoothed[:, 1]
    new_df.Timestamp = smoothed[:, 0]
    return new_df

#Create median filter function with desired kernel size

def create_rssi_median_filter(kernel=9):
    def median_filter(df):
        smoothed = medfilt(df.RSSI, kernel)
        new_df = df.copy()
        new_df.RSSI = smoothed
        return new_df
    return median_filter

#Create averaging filter function with desired kernel size

def create_rssi_avg_filter(kernel=9):
	def avg_filter(df):
		smoothed = df.RSSI.rolling(kernel).mean()
		new_df = df.copy()
		new_df.RSSI = smoothed
		return new_df
	return avg_filter

#Filter session RSSI values for each minor with desired filter

def filter_session(sess_df, filter_func=rssi_lowess_filter):
    result = pd.DataFrame()
    for minor in sorted(sess_df.Minor.unique()):
        result = result.append(
				filter_func(sess_df[sess_df.Minor == minor])
			)
    return result

#Filter RSSI values for each minor with desired filter in
#DataFrame with several sessions

def filter_rssi_df(df, filter_func=rssi_lowess_filter):
    result = pd.DataFrame()
    for sess_id in sorted(df.SessId.unique()):
        result = result.append(
				filter_session(df[df.SessId == sess_id], filter_func)
			)
    return result
