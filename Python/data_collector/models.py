import datetime
from peewee import DateTimeField, TimestampField, FloatField, IntegerField, TextField, ForeignKeyField
from peewee import Model, SqliteDatabase

database = SqliteDatabase('data.db')

class BaseModel(Model):
    class Meta:
        database = database

class CaptureSession(BaseModel):
    date = DateTimeField(default=datetime.datetime.now)
    device_id = TextField(null=True)
    device_type = TextField(null=True)

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
