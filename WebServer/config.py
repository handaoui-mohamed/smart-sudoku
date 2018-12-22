# -*- coding: utf-8 -*-
import os
basedir = os.path.abspath(os.path.dirname(__file__))
WTF_CSRF_ENABLED = False
SECRET_KEY = 'k@tj5C:!uj7Bmfgn}vtJi2p7a0_vGu["x418E=_wU&WohA#>lRYWkX))q5.T}h9M_!kp'
if os.environ.get('DATABASE_URL') is None:
    SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(basedir, 'db.sqlite')
    HOST_URL = 'http://localhost:5000/api'
else:
    SQLALCHEMY_DATABASE_URI = os.environ['DATABASE_URL']
    HOST_URL = 'https://smart-sudoku.herokuapp.com/api'
SQLALCHEMY_COMMIT_ON_TEARDOWN=True
SQLALCHEMY_TRACK_MODIFICATIONS=True
CORS_HEADERS = 'Content-Type'
SQLALCHEMY_MIGRATE_REPO = os.path.join(basedir, 'db_repository')
