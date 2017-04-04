from peewee import DateTimeField, TimestampField, FloatField, IntegerField, TextField, ForeignKeyField
from peewee import Model, Proxy

database_proxy = Proxy()

class BaseModel(Model):
    class Meta:
        database = database_proxy

class CaptureSession(BaseModel):
    date = DateTimeField()
    device_id = TextField()
    device_type = TextField()

class Position(BaseModel):
    timestamp = TimestampField()
    x = FloatField()
    y = FloatField()
    capture_session = ForeignKeyField(CaptureSession, related_name='positions')

class RSSIValue(BaseModel):
    timestamp = TimestampField()
    rssi = IntegerField()
    beacon_name = TextField()
    capture_session = ForeignKeyField(CaptureSession, related_name='rssi_values')

def create_tables():
    database_proxy.create_tables([CaptureSession, Position, RSSIValue])
