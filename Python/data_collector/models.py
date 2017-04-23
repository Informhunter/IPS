import datetime
from peewee import DateTimeField, FloatField, IntegerField, TextField, ForeignKeyField
from peewee import Model, SqliteDatabase

database = SqliteDatabase('data.db')

class BaseModel(Model):
    class Meta:
        database = database

class CaptureSession(BaseModel):
    name = TextField(unique=True)
    date = DateTimeField(default=datetime.datetime.now)

class Position(BaseModel):
    timestamp = IntegerField()
    x = FloatField()
    y = FloatField()
    capture_session = ForeignKeyField(CaptureSession, related_name='positions')

class RSSIValue(BaseModel):
    timestamp = IntegerField()
    rssi = IntegerField()
    beacon_uuid = TextField()
    beacon_major = IntegerField()
    beacon_minor = IntegerField()
    capture_session = ForeignKeyField(CaptureSession, related_name='rssi_values')

def create_tables():
    database.create_tables([CaptureSession, Position, RSSIValue])
