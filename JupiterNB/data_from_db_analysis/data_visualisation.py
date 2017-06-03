import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from data_filtration import filter_rssi_df

def convert_coords(img, x, y):
    return x * img.shape[1] / 13.9, y * img.shape[0] / 7.35

#Plot some graph in image coordinates

def plot_in_image(img, x, y, **kwargs):
    x, y = convert_coords(img, x, y)
    plt.xticks([])
    plt.yticks([])
    plt.axis('off')
    plt.plot(x, y, **kwargs)

#Scatter some graph in image coordinates

def scatter_in_image(img, x, y, **kwargs):
    x, y = convert_coords(img, x, y)
    plt.xticks([])
    plt.yticks([])
    plt.axis('off')
    plt.scatter(x, y, **kwargs)
    
#Plot some graph over image of flat plan

def plot_over_image(img, x, y, **kwargs):
    x, y = convert_coords(img, x, y)
    plt.xticks([])
    plt.yticks([])
    plt.axis('off')
    plt.imshow(img)
    plt.plot(x, y, **kwargs)

#Scatter some graph over image of flat plan

def scatter_over_image(img, x, y, **kwargs):
    x, y = convert_coords(img, x, y)
    plt.xticks([])
    plt.yticks([])
    plt.axis('off')
    plt.imshow(img)
    plt.scatter(x, y, **kwargs)


def plot_beacons(beacons, img):
    for _, beacon in beacons.iterrows():
        x, y = convert_coords(img, beacon['X'], beacon['Y'])
        plt.scatter([x], [y])
        plt.annotate(beacon.Minor, xy=(x, y))

#Plot RSSI over time for all minors

def plot_rssi_data(df, figsize=(30, 20), filter_func=None):
    min_time = df.Timestamp.min()
    unique_minors = sorted(df.Minor.unique())
    fig = plt.figure(figsize=figsize)
    fig.subplots_adjust(wspace=0.3)
    for i, minor in enumerate(unique_minors):
        if filter_func != None:
            bd = filter_rssi_df(
                    df[df.Minor == minor],
                    filter_func=filter_func
                )
        else:
            bd = df[df.Minor == minor]
        ax = plt.subplot(int(np.ceil(len(unique_minors) / 3)), 3, 1 + i)
        ax.set_title(str(minor))
        plt.xlabel('Time (ms)')
        plt.ylabel('RSSI (db)')
        plt.plot(bd.Timestamp-min_time, bd.RSSI)

#Plot data points for all minors at their estimated location
#with color dependent on RSSI

def plot_rssi_map(df, flat_img, figsize=(30, 20), beacons=pd.DataFrame(), filter_func=None):
    min_time = df.Timestamp.min()
    unique_minors = sorted(df.Minor.unique())
    fig = plt.figure(figsize=figsize)
    fig.subplots_adjust(wspace=0.3)
    for i, minor in enumerate(unique_minors):
        if filter_func != None:
            bd = filter_rssi_df(
                    df[df.Minor == minor],
                    filter_func=filter_func
                )
        else:
            bd = df[df.Minor == minor]

        ax = plt.subplot(int(np.ceil(len(unique_minors) / 3)), 3, 1 + i)
        ax.set_title(str(minor))
        plt.xlabel('X')
        plt.ylabel('Y')
        scatter_over_image(flat_img, bd.X, bd.Y, c=-bd.RSSI, cmap='Greys')
        if not beacons.empty:
            plot_beacons(beacons, flat_img)

#Plot route, RSSI graphs and RSSI maps

def plot_session(df, flat_img, figsize=(10, 7), beacons=pd.DataFrame(), filter_func=None):
    fig = plt.figure(figsize=figsize)
    plt.title("Route")
    scatter_over_image(flat_img, df.X, df.Y)
    if not beacons.empty:
        plot_beacons(beacons, flat_img)
    plot_rssi_data(df, filter_func=filter_func)
    plot_rssi_map(df, flat_img, beacons=beacons, filter_func=filter_func)
