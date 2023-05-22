from flask import Flask, send_file

app = Flask(__name__)

@app.route('/images/<image_name>')
def serve_image(image_name):
    response = send_file('images/' + image_name, mimetype='image/png')
    response.headers['Content-Disposition'] = 'attachment; filename={}'.format(image_name)
    return response

if __name__ == "__main__":
    app.run(port=8080)
