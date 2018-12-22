# -*- coding: utf-8 -*-
from app import app, db

@app.errorhandler(404)
def client(e):
    return app.send_static_file("index.html")


@app.errorhandler(500)
def error(e):
    db.session.rollback()
    return app.send_static_file("index.html")
