from tempfile import NamedTemporaryFile
from flask import Flask, request, redirect, url_for
from processing import save_data_to_db

UPLOAD_FOLDER = 'uploads'

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


@app.route('/upload', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        if 'rssi_data' not in request.files or 'position_data' not in request.files:
            return redirect(request.url)

        rssi_data = request.files['rssi_data']
        position_data = request.files['position_data']

        if rssi_data and position_data:

            rssi_file = NamedTemporaryFile(dir='uploads', delete=False, prefix='rssi_')
            position_file = NamedTemporaryFile(dir='uploads', delete=False, prefix='position_')

            rssi_data.save(rssi_file)
            position_data.save(position_file)

            rssi_name = rssi_file.name
            position_name = position_file.name

            rssi_file.close()
            position_file.close()

            save_data_to_db(rssi_name, position_name)

            return redirect(url_for('upload_file'))

    return '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new File</h1>
    <form method='POST' enctype='multipart/form-data'>
      <input type='file' name='rssi_data'>
      <input type='file' name='position_data'>
      <input type='submit' value='Upload'>
    </form>
    '''

if __name__ == '__main__':
    app.run(host="0.0.0.0")
