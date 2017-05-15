import numpy as np
import matplotlib.pyplot as plt
from data_filtration import filter_rssi_df

#Plot some graph over image of flat plan

def plot_over_image(img, x, y, **kwargs):
    scaleX = img.shape[1] / 13.9
    scaleY = img.shape[0] / 7.35
    plt.imshow(img)
    plt.plot(x * scaleX, y * scaleY, **kwargs)

#Scatter some graph over image of flat plan

def scatter_over_image(img, x, y, **kwargs):
    scaleX = img.shape[1] / 13.9
    scaleY = img.shape[0] / 7.35
    plt.imshow(img)
    plt.scatter(x * scaleX, y * scaleY, **kwargs)

#Plot RSSI over time for all minors

def plot_rssi_data(df, figsize=(15, 10), filter_func=None):
    min_time = df.Timestamp.min()
    unique_minors = sorted(df.Minor.unique())
    fig = plt.figure(figsize=figsize)
    for i, minor in enumerate(unique_minors):
        if filter_func != None:
            bd = filter_rssi_df(df[df.Minor == minor], filter_func)
        else:
            bd = df[df.Minor == minor]
        ax = plt.subplot(int(np.ceil(len(unique_minors) / 3)), 3, 1 + i)
        ax.set_title(str(minor))
        plt.plot(bd.Timestamp-min_time, bd.RSSI)

#Plot data points for all minors at their estimated location
#with color dependent on RSSI

def plot_rssi_map(df, flat_img, figsize=(15, 10), filter_func=None):
    min_time = df.Timestamp.min()
    unique_minors = sorted(df.Minor.unique())
    fig = plt.figure(figsize=figsize)
    for i, minor in enumerate(unique_minors):
        if filter_func != None:
            bd = filter_rssi_df(df[df.Minor == minor], filter_func)
        else:
            bd = df[df.Minor == minor]
        ax = plt.subplot(int(np.ceil(len(unique_minors) / 3)), 3, 1 + i)
        ax.set_title(str(minor))
        scatter_over_image(flat_img, bd.X, bd.Y, c=-bd.RSSI, cmap='Greys')

#Plot route, RSSI graphs and RSSI maps

def plot_session(df, flat_img, filter_func=None):
    plt.title("Route")
    scatter_over_image(flat_img, df.X, df.Y)
    plot_rssi_data(df, filter_func=filter_func)
    plot_rssi_map(df, flat_img, filter_func=filter_func)
