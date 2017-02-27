from flask import Flask, render_template
app = Flask(__name__)

@app.route('/')
def show_home():
    return render_template('home.html')
