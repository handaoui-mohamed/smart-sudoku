# -*- coding: utf-8 -*-
from app import app, db

@app.route('/')
@app.route('/home')
def home():
    return app.send_static_file("index.html")

@app.route('/register')
def register():
    return app.send_static_file("register.html")

@app.errorhandler(500)
def error(e):
    db.session.rollback()
    return app.send_static_file("index.html")
